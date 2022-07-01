package com.example.sens.enums;

/**
 * <pre>
 *     订单状态enum
 *
 * </pre>
 *
 * @author liuyanzhao
 */
public enum OrderStatusEnum {


    /**
     * 待支付，已取消
     */
    CANCEL_PAY(-1),

    /**
     * 待支付
     */
    NOT_PAY(0),

    /**
     * 已支付，合同生效
     */
    HAS_PAY(1),

    /**
     * 已退租
     */
    FINISHED(2);

    private Integer code;

    OrderStatusEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}
