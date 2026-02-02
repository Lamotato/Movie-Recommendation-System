package com.project.platform.entity;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 订单表
 */
@Data
public class Order {
    /**
     * 订单ID
     */
    private Integer id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 场次ID
     */
    private Integer screeningId;

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 座位数量
     */
    private Integer seatCount;

    /**
     * 状态：pending/paid/cancelled/completed
     */
    private String status;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 取票时间
     */
    private LocalDateTime ticketTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}