package com.project.platform.vo;

import lombok.Data;

import java.util.Map;

/**
 * 用户行为统计视图对象
 */
@Data
public class UserBehaviorStatisticsVO {
    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 总行为数
     */
    private Long totalBehaviors;

    /**
     * 各类型行为统计
     */
    private Map<String, Long> behaviorTypeCounts;

    /**
     * 浏览的电影数
     */
    private Long browseCount;

    /**
     * 评分的电影数
     */
    private Long rateCount;

    /**
     * 收藏的电影数
     */
    private Long favoriteCount;

    /**
     * 搜索次数
     */
    private Long searchCount;

    /**
     * 下单次数
     */
    private Long orderCount;

    /**
     * 最近行为时间
     */
    private java.time.LocalDateTime lastBehaviorTime;
}
