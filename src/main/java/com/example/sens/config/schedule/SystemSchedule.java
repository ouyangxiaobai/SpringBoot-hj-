package com.example.sens.config.schedule;

import com.example.sens.entity.FinanceRecord;
import com.example.sens.entity.Order;
import com.example.sens.entity.Post;
import com.example.sens.entity.User;
import com.example.sens.enums.OffLeaseStatusEnum;
import com.example.sens.enums.OrderStatusEnum;
import com.example.sens.enums.PostStatusEnum;
import com.example.sens.mapper.OrderMapper;
import com.example.sens.mapper.PostMapper;
import com.example.sens.mapper.UserMapper;
import com.example.sens.service.FinanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 定时器
 *
 * @author xxx
 * @date 2021/3/21 7:18 下午
 */
@Component
public class SystemSchedule {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FinanceRecordService financeRecordService;

    /**
     * 更新到期的订单
     */
    @Scheduled(fixedRate = 10000)
    @Transactional(rollbackFor = Exception.class)
    public void updatePostStatus() {
        List<Order> orderList = orderMapper.findOverDueOrder();
        for (Order order : orderList) {


            // 更新房屋状态
            Post post = postMapper.selectById(order.getPostId());
            if (post == null) {
                continue;
            }

            // 退还押金
            User user = userMapper.selectById(order.getUserId());
            if (user == null) {
                continue;
            }

            User ownerUser = userMapper.selectById(post.getId());
            if (ownerUser == null) {
                continue;
            }

            // 添加租客收支明细
            FinanceRecord financeRecord = new FinanceRecord();
            financeRecord.setUserId(order.getUserId());
            financeRecord.setType("押金退回收入");
            financeRecord.setContent("押金" + order.getDeposit() + ",订单ID:" + order.getId());
            financeRecord.setMoney(order.getOffLeasePrice() != null ? order.getOffLeasePrice() : 0 + order.getDeposit());
            financeRecord.setCreateTime(new Date());
            financeRecordService.insert(financeRecord);

            // 添加房东收支明细
            FinanceRecord financeRecord2 = new FinanceRecord();
            financeRecord2.setUserId(order.getOwnerUserId());
            financeRecord2.setType("押金退回支出");
            financeRecord2.setContent("押金" + order.getDeposit() + ",订单ID:" + order.getId());
            financeRecord2.setMoney(-(order.getOffLeasePrice() != null ? order.getOffLeasePrice() : 0 + order.getDeposit()));
            financeRecord2.setCreateTime(new Date());
            financeRecordService.insert(financeRecord2);

            // 结束订单
            order.setStatus(OrderStatusEnum.FINISHED.getCode());
            orderMapper.updateById(order);

            post.setPostStatus(PostStatusEnum.ON_SALE.getCode());
            postMapper.updateById(post);

        }


    }

}
