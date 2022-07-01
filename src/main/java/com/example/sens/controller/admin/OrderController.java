package com.example.sens.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.controller.common.BaseController;
import com.example.sens.dto.JsonResult;
import com.example.sens.entity.FinanceRecord;
import com.example.sens.entity.Order;
import com.example.sens.entity.Post;
import com.example.sens.enums.OffLeaseStatusEnum;
import com.example.sens.enums.OrderStatusEnum;
import com.example.sens.enums.PostStatusEnum;
import com.example.sens.service.FinanceRecordService;
import com.example.sens.service.OrderService;
import com.example.sens.service.PostService;
import com.example.sens.service.UserService;
import com.example.sens.util.DateUtil;
import com.example.sens.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * <pre>
 *     订单管理控制器
 * </pre>
 */
@Slf4j
@Controller("adminOrderController")
@RequestMapping(value = "/admin/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private FinanceRecordService financeRecordService;

    /**
     * 查询所有订单并渲染order页面
     *
     * @return 模板路径admin/admin_order
     */
    @GetMapping
    public String orders(@RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
                         @RequestParam(value = "size", defaultValue = "6") Integer pageSize,
                         @RequestParam(value = "sort", defaultValue = "id") String sort,
                         @RequestParam(value = "order", defaultValue = "desc") String order, Model model) {
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Order> orderPage = null;
        Order orderCondition = new Order();
        // 如果是房东
        if (loginUserIsOwner()) {
            orderCondition.setOwnerUserId(getLoginUserId());
        }
        // 如果是客户
        else if (loginUserIsCustomer()) {
            orderCondition.setUserId(getLoginUserId());
        }
        orderPage = orderService.findAll(orderCondition, page);
        model.addAttribute("orders", orderPage.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));

        // 管理员或者房东可以审核
        model.addAttribute("hasCheckRole", loginUserIsAdmin() || loginUserIsOwner());
        return "admin/admin_order";
    }

    /**
     * 取消订单，待支付取消订单
     *
     * @param id 订单Id
     * @return JsonResult
     */
    @PostMapping(value = "/cancel")
    @ResponseBody
    @Transactional
    public JsonResult close(@RequestParam("id") Long id) {
        Order order = orderService.get(id);
        if (order == null) {
            return JsonResult.error("订单不存在");
        }
        // 只有当前订单的租客、房东和管理员有权限操作
        if (!Objects.equals(order.getUserId(), getLoginUserId()) &&
                !Objects.equals(order.getOwnerUserId(), getLoginUserId()) &&
                !loginUserIsAdmin()) {
            return JsonResult.error("没有权限操作");
        }

        // 更新订单状态
        order.setStatus(OrderStatusEnum.CANCEL_PAY.getCode());
        orderService.update(order);

        return JsonResult.success("取消订单成功");
    }


    /**
     * 租客，退租申请
     *
     * @param id 订单Id
     * @return JsonResult
     */
    @PostMapping(value = "/offLeaseApply")
    @ResponseBody
    @Transactional
    public JsonResult offLeaseApply(@RequestParam("id") Long id) {
        Order order = orderService.get(id);
        if (order == null) {
            return JsonResult.error("订单不存在");
        }
        // 只有当前订单的租客、房东和管理员有权限操作
        if (!Objects.equals(order.getUserId(), getLoginUserId()) &&
                !Objects.equals(order.getOwnerUserId(), getLoginUserId()) &&
                !loginUserIsAdmin()) {
            return JsonResult.error("没有权限操作");
        }


        Date offLeaseDate = new Date();
        // 如果退租日期小于入住日期或退租日期大于
        if (offLeaseDate.before(order.getStartDate()) || offLeaseDate.after(order.getEndDate())) {
            return JsonResult.error("退租日期不合法");
        }

        // 修改退租状态，计算需要退租金额
        Date now = new Date();
        Integer offLeaseDayNum = DateUtil.daysBetween(now, order.getEndDate());
        order.setOffLeaseStatus(OffLeaseStatusEnum.OFF_LEASE_APPLY.getCode());
        order.setOffLeaseDate(now);
        order.setOffLeasePrice(offLeaseDayNum * order.getDayPrice());
        orderService.update(order);
        return JsonResult.success("退租申请成功");
    }

    /**
     * 退租申请通过
     *
     * @param id 订单Id
     * @return JsonResult
     */
    @PostMapping(value = "/offLeaseApplyPass")
    @Transactional
    @ResponseBody
    public JsonResult offLeaseApplyPass(@RequestParam("id") Long id) {
        Order order = orderService.get(id);
        if (order == null) {
            return JsonResult.error("订单不存在");
        }
        // 只有当前订单的房东和管理员有权限操作
        if (!Objects.equals(order.getOwnerUserId(), getLoginUserId()) &&
                !loginUserIsAdmin()) {
            return JsonResult.error("没有权限操作");
        }

        // 修改退租状态为已通过,订单状态为已完成
        order.setOffLeaseStatus(OffLeaseStatusEnum.OFF_LEASE_APPLY_PASS.getCode());
        order.setStatus(OrderStatusEnum.FINISHED.getCode());
        orderService.update(order);

        // 更新房屋状态
        Post post = postService.get(order.getPostId());
        if (post != null) {
            // 更新房子状态为已租出
            post.setPostStatus(PostStatusEnum.ON_SALE.getCode());
            postService.update(post);
        }

        // 添加租客收支明细
        FinanceRecord financeRecord = new FinanceRecord();
        financeRecord.setUserId(order.getUserId());
        financeRecord.setType("房租退回收入(包括押金)");
        financeRecord.setContent("包括租金退回" + order.getOffLeasePrice() + ",押金" + order.getDeposit() + ",订单ID:" + order.getId());
        financeRecord.setMoney(order.getOffLeasePrice() + order.getDeposit());
        financeRecord.setCreateTime(new Date());
        financeRecordService.insert(financeRecord);

        // 添加房东收支明细
        FinanceRecord financeRecord2 = new FinanceRecord();
        financeRecord2.setUserId(order.getOwnerUserId());
        financeRecord2.setType("房租退回支出(包括租金)");
        financeRecord2.setContent("包括租金退回" + order.getOffLeasePrice() + ",押金" + order.getDeposit() + ",订单ID:" + order.getId());
        financeRecord2.setMoney(-(order.getOffLeasePrice() + order.getDeposit()));
        financeRecord2.setCreateTime(new Date());
        financeRecordService.insert(financeRecord2);
        return JsonResult.success("退租完成，退款结算成功");
    }


    /**
     * 退租申请拒绝
     *
     * @param id 订单Id
     * @return JsonResult
     */
    @PostMapping(value = "/offLeaseApplyReject")
    @ResponseBody
    public JsonResult offLeaseApplyReject(@RequestParam("id") Long id) {
        // 修改订单状态
        Order order = orderService.get(id);
        if (order == null) {
            return JsonResult.error("订单不存在");
        }
        // 只有管理员或房东有权限操作
        if (!Objects.equals(order.getOwnerUserId(), getLoginUserId()) && !loginUserIsAdmin()) {
            return JsonResult.error("没有权限操作");
        }
        order.setOffLeaseStatus(OffLeaseStatusEnum.OFF_LEASE_APPLY_REJECT.getCode());
        orderService.update(order);
        return JsonResult.success("退租申请拒绝成功");
    }


}
