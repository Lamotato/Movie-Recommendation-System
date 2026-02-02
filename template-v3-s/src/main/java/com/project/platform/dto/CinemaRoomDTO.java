package com.project.platform.dto;

import lombok.Data;

/**
 * 影院房间新增/修改 DTO
 */
@Data
public class CinemaRoomDTO {
    private Integer id;
    private Integer cinemaId;
    private String name;
    private Integer rowCount;
    private Integer colCount;
    private String description;
    private String status;
}
