package com.project.platform.entity;


import java.time.LocalDateTime;
import lombok.Data;

/**
 * 演职人员表
 */
@Data
public class MovieCast {
    /**
     * ID
     */
    private Integer id;

    /**
     * 电影ID
     */
    private Integer movieId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 角色：actor/director/writer/producer
     */
    private String role;

    /**
     * 饰演角色名（仅演员）
     */
    private String characterName;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}