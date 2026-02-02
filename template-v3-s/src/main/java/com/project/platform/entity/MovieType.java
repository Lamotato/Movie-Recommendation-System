package com.project.platform.entity;


import java.time.LocalDateTime;
import lombok.Data;

/**
 * 电影类型表
 */
@Data
public class MovieType {
    /**
     * 类型ID
     */
    private Integer id;

    /**
     * 类型名称（如：动作、喜剧、科幻）
     */
    private String name;

    /**
     * 类型描述
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