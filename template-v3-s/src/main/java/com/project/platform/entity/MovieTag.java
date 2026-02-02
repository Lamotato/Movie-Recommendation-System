package com.project.platform.entity;


import java.time.LocalDateTime;
import lombok.Data;

/**
 * 电影标签表
 */
@Data
public class MovieTag {
    /**
     * 标签ID
     */
    private Integer id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签分类（如：genre/theme/mood）
     */
    private String category;

    /**
     * 状态：active/inactive
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}