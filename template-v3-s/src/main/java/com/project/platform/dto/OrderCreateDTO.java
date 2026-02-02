package com.project.platform.dto;

import lombok.Data;

import java.util.List;

/**
 * 创建订单 DTO
 */
@Data
public class OrderCreateDTO {
    /**
     * 场次ID
     */
    private Integer screeningId;
    
    /**
     * 座位ID列表
     */
    private List<Integer> seatIds;
}
