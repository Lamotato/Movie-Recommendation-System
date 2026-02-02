package com.project.platform.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 影院新增/修改 DTO
 */
@Data
public class CinemaDTO {
    private Integer id;
    private String name;
    private String address;
    private String tel;
    private String email;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String status;
}
