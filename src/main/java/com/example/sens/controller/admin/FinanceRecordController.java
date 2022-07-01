package com.example.sens.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.sens.controller.common.BaseController;
import com.example.sens.entity.FinanceRecord;
import com.example.sens.service.FinanceRecordService;
import com.example.sens.service.UserService;
import com.example.sens.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/**
 * 收支明细
 *
 * @author xxx
 * @date 2021/3/17 11:39 下午
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/financeRecord")
public class FinanceRecordController extends BaseController {

    @Autowired
    private FinanceRecordService financeRecordService;

    /**
     * 查询所有收支明细并渲染financeRecord页面
     *
     * @return 模板路径admin/admin_financeRecord
     */
    @GetMapping
    public String financeRecords(@RequestParam(value = "startDate", defaultValue = "") String startDate,
                                 @RequestParam(value = "endDate", defaultValue = "") String endDate,
                                 @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                                 @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                                 @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                                 @RequestParam(value = "order", defaultValue = "desc") String order, Model model) {
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<FinanceRecord> financeRecords = null;

        if (loginUserIsAdmin()) {
            financeRecords = financeRecordService.findAll(startDate, endDate, page);
        } else {
            financeRecords = financeRecordService.findByUserId(startDate, endDate, getLoginUserId(), page);
        }

        model.addAttribute("financeRecords", financeRecords.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "admin/admin_financeRecord";
    }

}
