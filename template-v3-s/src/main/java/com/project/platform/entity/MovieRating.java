package com.project.platform.entity;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 电影评分表
 */
@Data
public class MovieRating {
    /**
     * ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 电影ID
     */
    private Integer movieId;

    /**
     * 评分（0-10）
     */
    private BigDecimal rating;

    /**
     * 评论内容
     */
    private String comment;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}