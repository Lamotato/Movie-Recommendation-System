package com.project.platform.entity;


import java.time.LocalDateTime;
import lombok.Data;

/**
 * 影院房间表
 */
@Data
public class CinemaRoom {
    /**
     * 房间ID
     */
    private Integer id;

    /**
     * 影院ID
     */
    private Integer cinemaId;

    /**
     * 房间名称（如：1号厅）
     */
    private String name;

    /**
     * 行数
     */
    private Integer rowCount;

    /**
     * 列数
     */
    private Integer colCount;

    /**
     * 房间描述
     */
    private String description;

    /**
     * 状态：active/inactive
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}