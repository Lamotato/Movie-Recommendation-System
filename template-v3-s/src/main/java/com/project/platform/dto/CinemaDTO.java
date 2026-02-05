package com.project.platform.dto;

import lombok.Data;

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
    private String description;
    private String status;
}
