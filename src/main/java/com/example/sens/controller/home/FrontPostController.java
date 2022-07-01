package com.example.sens.controller.home;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.controller.common.BaseController;
import com.example.sens.entity.*;
import com.example.sens.enums.PostStatusEnum;
import com.example.sens.service.*;
import com.example.sens.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author xxx
 * @date 2022/3/11 4:59 下午
 */
@Controller
public class FrontPostController extends BaseController {

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

    /**
     * 房屋列表
     *
     * @param model
     * @return
     */
    @GetMapping("/post")
    public String postList(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                           @RequestParam(value = "size", defaultValue = "6") Integer pageSize,
                           @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                           @RequestParam(value = "order", defaultValue = "desc") String order,
                           @RequestParam(value = "postTitle", defaultValue = "") String postTitle,
                           @RequestParam(value = "cityId", defaultValue = "-1") Long cityId,
                           @RequestParam(value = "cateId", defaultValue = "-1") Long cateId,
                           @RequestParam(value = "area", defaultValue = "") String area,
                           @RequestParam(value = "price", defaultValue = "") String price,
                           @RequestParam(value = "status", defaultValue = "-1") Integer status,
                           HttpSession session,
                           Model model) {
        Post condition = new Post();

        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);


        List<City> cityList = cityService.findAll();
        model.addAttribute("cityList", cityList);

        model.addAttribute("postCount", postService.count(null));

        try {
            if (StringUtils.isNotEmpty(price)) {
                String[] priceArr = price.split("-");
                if (priceArr.length == 2) {
                    condition.setMinPrice(Integer.valueOf(priceArr[0]));
                    condition.setMaxPrice(Integer.valueOf(priceArr[1]));
                }
            }
            if (StringUtils.isNotEmpty(area)) {
                String[] areaArr = area.split("-");
                if (areaArr.length == 2) {
                    condition.setMinArea(Integer.valueOf(areaArr[0]));
                    condition.setMaxArea(Integer.valueOf(areaArr[1]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 查询日期列表
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        condition.setPostTitle(postTitle);
        condition.setPostStatus(status);
        condition.setCateId(cateId);
        condition.setCityId(cityId);

        Page<Post> postPage = postService.findPostByCondition(condition, page);
        model.addAttribute("page", postPage);
        model.addAttribute("postTitle", postTitle);
        model.addAttribute("cityId", cityId);
        model.addAttribute("cateId", cateId);
        model.addAttribute("status", status);
        model.addAttribute("area", area);
        model.addAttribute("price", price);

        // 侧边栏
        model.addAttribute("onCount", postService.countByStatus(PostStatusEnum.ON_SALE.getCode()));
        model.addAttribute("offCount", postService.countByStatus(PostStatusEnum.OFF_SALE.getCode()));

        if (cityId != null && cityId != -1) {
            City city = cityService.get(cityId);
            if (city != null) {
                session.setAttribute("city", city);
            }
        } else {
            session.removeAttribute("city");
        }
        return "home/postList";
    }


    /**
     * 房屋详情
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/post/{id}")
    public String postDetails(@PathVariable("id") Long id,
                              @RequestParam(value = "startDate", required = false) String start,
                              @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                              HttpSession session,
                              Model model) {

        // 房屋
        Post post = postService.get(id);
        if (post == null) {
            return renderNotFound();
        }
        // 分类和城市
        Category category = categoryService.get(post.getCateId());
        City city = cityService.get(post.getCityId());
        User user = userService.get(post.getUserId());

        post.setCategory(category);
        post.setCity(city);
        post.setUser(user);
        model.addAttribute("post", post);

        boolean allowEdit = getLoginUser() != null && (loginUserIsAdmin() || Objects.equals(post.getUserId(), getLoginUserId()));
        model.addAttribute("allowEdit", allowEdit);

        String[] imgUrlList = post.getImgUrl().split(",");
        model.addAttribute("imgUrlList", imgUrlList);

        List<Category> categoryList = categoryService.findAll();
        model.addAttribute("categoryList", categoryList);
        List<City> cityList = cityService.findAll();
        model.addAttribute("cityList", cityList);

        City citySession = (City) session.getAttribute("city");
        Long cityId = citySession == null ? null : citySession.getId();
        List<Post> latestPostList = postService.getLatestPost(cityId, 6);
        model.addAttribute("latestPostList", latestPostList);

        // 可以考虑优化下，暂时没有时间优化
        List<Post> unionRentPost = postService.getUnionRentPost(post);
        List<Order> orderList = new ArrayList<>();
        for (Post temp : unionRentPost) {
            Order order = orderService.findByPostId(temp.getId());
            if (order == null) {
                order = new Order();
            } else {
                order.setUser(userService.get(order.getUserId()));
            }
            order.setPost(temp);
            orderList.add(order);
        }
        model.addAttribute("orderList", orderList);
        return "home/post";
    }


}
