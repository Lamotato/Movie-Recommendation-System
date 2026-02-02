package com.project.platform.entity;


import java.time.LocalDateTime;
import lombok.Data;

/**
 * 推荐结果表
 */
@Data
public class RecommendationResult {
    /**
     * ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 推荐类型：collaborative/content/popular/realtime/hybrid
     */
    private String recommendationType;

    /**
     * 推荐电影ID列表（JSON数组）
     */
    private String movieIds;

    /**
     * 推荐分数列表（JSON数组，与movie_ids对应）
     */
    private String scores;

    /**
     * 使用的推荐策略
     */
    private String strategy;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 生成时间
     */
    private LocalDateTime createTime;

}