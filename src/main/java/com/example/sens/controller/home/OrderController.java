package com.example.sens.controller.home;

import com.example.sens.controller.common.BaseController;
import com.example.sens.dto.JsonResult;
import com.example.sens.entity.*;
import com.example.sens.enums.OrderStatusEnum;
import com.example.sens.enums.PostStatusEnum;
import com.example.sens.service.*;
import com.example.sens.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 订单控制器
 *
 * @author xxx
 * @date 2021/3/16 3:48 下午
 */
@Controller
public class OrderController extends BaseController {


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PostService postService;

    @Autowired
    private CityService cityService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private FinanceRecordService financeRecordService;


    /**
     * 创建订单
     *
     * @param postId
     * @param endDateStr
     * @return
     */
    @PostMapping("/order/create")
    @Transactional
    @ResponseBody
    public JsonResult addOrder(@RequestParam(value = "postId") Long postId,
                               @RequestParam(value = "endDate") String endDateStr) {
        User user = getLoginUser();
        if (user == null) {
            return JsonResult.error("请先登录");
        }


        // 处理退租日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = new Date();
        Date endDate;
        try {
            endDate = sdf.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return JsonResult.error("退租日期格式不合法");
        }
        // 计算总共多少天
        Integer dayNum = DateUtil.daysBetween(startDate, endDate);
        if (dayNum < 30) {
            return JsonResult.error("最少租住1月");
        }

        Post post = postService.get(postId);
        if (post == null) {
            return JsonResult.error("房屋不存在");
        }

        if (Objects.equals(post.getUserId(), user.getId())) {
            return JsonResult.error("不能租赁自己的房子哦");
        }

        if (!PostStatusEnum.ON_SALE.getCode().equals(post.getPostStatus())) {
            return JsonResult.error("房屋已租出，暂时无法预定");
        }

        Date today = new Date();

        // 添加订单
        Order order = new Order();
        order.setPostId(postId);
        order.setDayPrice(post.getDayPrice());
        order.setDayNum(dayNum);
        order.setTotalAmount(order.getDayPrice() * order.getDayNum());
        order.setDeposit(post.getDeposit());
        order.setStartDate(today);
        order.setEndDate(endDate);
        order.setUserId(user.getId());
        order.setOwnerUserId(post.getUserId());
        order.setStatus(OrderStatusEnum.NOT_PAY.getCode());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        orderService.insert(order);
        return JsonResult.success("订单创建成功", order.getId());
    }

    @GetMapping("/order/{id}")
    public String order(@PathVariable("id") Long id, Model model) {
        Order order = orderService.get(id);
        if (order == null) {
            return this.renderNotFound();
        }

        User user = getLoginUser();
        if (user == null) {
            return "redirect:/login";
        }

        if (!Objects.equals(user.getId(), order.getUserId()) && !Objects.equals(user.getId(), order.getOwnerUserId()) && !loginUserIsAdmin()) {
            return this.renderNotAllowAccess();
        }
        model.addAttribute("order", order);


        // 分类列表
        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);
        List<City> cityList = cityService.findAll();
        model.addAttribute("cityList", cityList);
        model.addAttribute("user", userService.get(order.getUserId()));
        return "home/order";
    }

    /**
     * 电子合同
     *
     * @param orderId
     * @return
     */
    @GetMapping("/order/agreement")
    public String agreement(@RequestParam("orderId") Long orderId, Model model) {
        Order order = orderService.get(orderId);

        if (order == null) {
            return this.renderNotFound();
        }

        Post post = postService.get(order.getPostId());
        order.setPost(post);

        order.setUser(userService.get(order.getUserId()));
        order.setOwnerUser(userService.get(order.getOwnerUserId()));
//        User user = getLoginUser();
//        if (user == null) {
//            return "redirect:/login";
//        }
//
//        if (!Objects.equals(user.getId(), order.getUserId()) && !Objects.equals(user.getId(), order.getOwnerUserId()) && !loginUserIsAdmin()) {
//            return this.renderNotAllowAccess();
//        }

        model.addAttribute("order", order);
        List<City> cityList = cityService.findAll();
        model.addAttribute("cityList", cityList);
        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);
        return "home/agreement";
    }


    /**
     * 下载合同
     *
     * @param orderId
     * @param response
     */
    @GetMapping("/order/agreement/download")
    public void agreementDownload(@RequestParam("orderId") Long orderId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {


        StringBuffer requestURL = request.getRequestURL();
        String tempContextUrl = requestURL.delete(requestURL.length() - request.getRequestURI().length(), requestURL.length()).append("/").toString();
        ServletOutputStream out = null;
        InputStream inputStream = null;
        try {
            Order order = orderService.get(orderId);

            User user = userService.get(order.getUserId());
            User ownerUser = userService.get(order.getOwnerUserId());
            String pdfName = ownerUser.getUserDisplayName() + "&" + user.getUserDisplayName() + "租房合同.html";
            // 获取外部文件流
            URL url = new URL(tempContextUrl + "agreement?orderId=" + orderId);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            inputStream = conn.getInputStream();
            int len = 0;
            // 输出 下载的响应头，如果下载的文件是中文名，文件名需要经过url编码
            response.setContentType("text/html;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(pdfName, "UTF-8"));
            response.setHeader("Cache-Control", "no-cache");
            out = response.getOutputStream();
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 支付页面
     *
     * @param orderId
     * @param model
     * @return
     */
    @GetMapping("/order/pay")
    public String pay(@RequestParam("orderId") Long orderId, Model model) {
        Order order = orderService.get(orderId);

        if (order == null) {
            return this.renderNotFound();
        }

        Post post = postService.get(order.getPostId());
        order.setPost(post);

        User user = getLoginUser();
        if (user == null) {
            return "redirect:/login";
        }

        if (!Objects.equals(user.getId(), order.getUserId()) && !Objects.equals(user.getId(), order.getOwnerUserId()) && !loginUserIsAdmin()) {
            return this.renderNotAllowAccess();
        }


        if (!Objects.equals(OrderStatusEnum.NOT_PAY.getCode(), order.getStatus())) {
            return this.renderNotAllowAccess();
        }

        model.addAttribute("order", order);

        List<City> cityList = cityService.findAll();
        model.addAttribute("cityList", cityList);
        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);
        return "home/pay";
    }


    /**
     * 支付
     *
     * @return orderId
     */
    @PostMapping("/order/pay")
    @Transactional
    @ResponseBody
    public JsonResult paySuccess(@RequestParam(value = "orderId") Long orderId) {
        User user = userService.get(getLoginUserId());
        if (user == null) {
            return JsonResult.error("请先登录");
        }


        Order order = orderService.get(orderId);
        if (order == null) {
            return JsonResult.error("订单不存在");
        }


        // 只有当前订单的租客、房东和管理员有权限操作
        if (!Objects.equals(order.getUserId(), getLoginUserId()) &&
                !Objects.equals(order.getOwnerUserId(), getLoginUserId()) &&
                !loginUserIsAdmin()) {
            return JsonResult.error("没有权限操作");
        }
        Post post = postService.get(order.getPostId());
        if (post == null || !Objects.equals(post.getPostStatus(), PostStatusEnum.ON_SALE.getCode())) {
            return JsonResult.error("房屋已租出，暂时无法预定");
        }

        // 更新订单状态为已支付
        order.setStatus(OrderStatusEnum.HAS_PAY.getCode());
        orderService.update(order);

        // 更新房子状态为已租出
        post.setPostStatus(PostStatusEnum.OFF_SALE.getCode());
        postService.update(post);

        // 添加租客收支明细
        FinanceRecord financeRecord = new FinanceRecord();
        financeRecord.setUserId(order.getUserId());
        financeRecord.setType("房租支出(包括押金)");
        financeRecord.setContent("房屋总金额" + order.getTotalAmount() + ",押金" + order.getDeposit() + ",订单ID:" + order.getId());
        financeRecord.setMoney(-(order.getTotalAmount() + order.getDeposit()));
        financeRecord.setCreateTime(new Date());
        financeRecordService.insert(financeRecord);

        // 添加房东收支明细
        FinanceRecord financeRecord2 = new FinanceRecord();
        financeRecord2.setUserId(order.getOwnerUserId());
        financeRecord2.setType("房租收入(包括押金)");
        financeRecord2.setContent("房屋总金额" + order.getTotalAmount() + ",押金" + order.getDeposit() + ",订单ID:" + order.getId());
        financeRecord2.setMoney(order.getTotalAmount() + order.getDeposit());
        financeRecord2.setCreateTime(new Date());
        financeRecordService.insert(financeRecord2);
        return JsonResult.success("支付成功", order.getId());
    }

}
