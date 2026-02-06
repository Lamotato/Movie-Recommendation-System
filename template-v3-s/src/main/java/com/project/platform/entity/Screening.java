package com.project.platform.entity;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 放映场次表
 */
@Data
public class Screening {
    /**
     * 场次ID
     */
    private Integer id;

    /**
     * 电影ID
     */
    private Integer movieId;

    /**
     * 影院ID
     */
    private Integer cinemaId;

    /**
     * 房间ID
     */
    private Integer roomId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 票价（元）
     */
    private BigDecimal price;

    /**
     * 状态：pending/active/cancelled（pending需要管理员审批）
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 电影名称（非数据库字段，仅用于查询展示）
     */
    private String movieName;

    /**
     * 影院名称（非数据库字段，仅用于查询展示）
     */
    private String cinemaName;

    /**
     * 房间名称（非数据库字段，仅用于查询展示）
     */
    private String roomName;

}