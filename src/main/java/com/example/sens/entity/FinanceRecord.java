package com.example.sens.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.sens.common.base.BaseEntity;
import lombok.Data;

/**
 * 收支记录
 *
 * @author xxx
 * @date 2021/3/22 3:15 下午
 */
@TableName("finance_record")
@Data
public class FinanceRecord extends BaseEntity {

    /**
     * 类型：租金收入/押金收入/租金退回收入/押金退回收入
     */
    private String type;

    /**
     * 详细描述
     */
    private String content;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 金额
     */
    private Integer money;

    /**
     * 用户
     */
    @TableField(exist = false)
    private User user;
}
