package com.project.platform.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 放映场次新增/修改 DTO
 */
@Data
public class ScreeningDTO {
    private Integer id;
    private Integer movieId;
    private Integer cinemaId;
    private Integer roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;
    private String status;
}
