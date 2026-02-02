package com.project.platform.entity;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 电影信息表
 */
@Data
public class Movie {
    /**
     * 电影ID
     */
    private Integer id;

    /**
     * 电影名称
     */
    private String name;

    /**
     * 英文名称
     */
    private String englishName;

    /**
     * 导演
     */
    private String director;

    /**
     * 剧情简介
     */
    private String description;

    /**
     * 海报URL
     */
    private String posterUrl;

    /**
     * 预告片URL
     */
    private String trailerUrl;

    /**
     * 时长（分钟）
     */
    private Integer duration;

    /**
     * 上映日期
     */
    private LocalDate releaseDate;

    /**
     * 制片国家
     */
    private String country;

    /**
     * 语言
     */
    private String language;

    /**
     * 平均评分（0-10）
     */
    private BigDecimal rating;

    /**
     * 评分人数
     */
    private Integer ratingCount;

    /**
     * 票房（元）
     */
    private BigDecimal boxOffice;

    /**
     * 观看次数
     */
    private Integer viewCount;

    /**
     * 收藏次数
     */
    private Integer favoriteCount;

    /**
     * 状态：pending/active/inactive
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

}