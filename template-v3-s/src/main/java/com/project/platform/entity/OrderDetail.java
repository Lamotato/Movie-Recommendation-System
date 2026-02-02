package com.project.platform.entity;


import java.math.BigDecimal;
import lombok.Data;

/**
 * 订单明细表
 */
@Data
public class OrderDetail {
    /**
     * ID
     */
    private Integer id;

    /**
     * 订单ID
     */
    private Integer orderId;

    /**
     * 座位ID
     */
    private Integer seatId;

    /**
     * 单价
     */
    private BigDecimal price;

}