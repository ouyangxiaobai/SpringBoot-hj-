package com.example.sens.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sens.common.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * 订单
 *
 * @author xxx
 * @date 2021/4/5 3:25 下午
 */
@Data
@TableName("t_order")
public class Order extends BaseEntity {

    /**
     * 下单用户ID，租客ID
     */
    private Long userId;

    /**
     * 房东ID
     */
    private Long ownerUserId;

    /**
     * 房间ID
     */
    private Long postId;


    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 订单状态，-1已取消，0待支付，1已支付，2已退租
     *
     * @see com.example.sens.enums.OrderStatusEnum
     */
    private Integer status;


    /**
     * 房子日租金
     */
    private Integer dayPrice;

    /**
     * 数量，租住天数
     */
    private Integer dayNum;

    /**
     * 总租金
     */
    private Integer totalAmount;


    /**
     * 退租状态： -1驳回，0待审核，1通过
     *
     * @see com.example.sens.enums.OffLeaseStatusEnum
     */
    private Integer offLeaseStatus;

    /**
     * 退租日期
     */
    private Date offLeaseDate;

    /**
     * 退租总价
     */
    private Integer offLeasePrice;

    /**
     * 押金
     */
    private Integer deposit;

    /**
     * 房屋
     */
    @TableField(exist = false)
    private Post post;

    /**
     * 下单用户
     */
    @TableField(exist = false)
    private User user;

    /**
     * 房东用户
     */
    @TableField(exist = false)
    private User ownerUser;

}
