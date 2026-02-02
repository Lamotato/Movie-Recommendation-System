package com.project.platform.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户行为视图对象
 */
@Data
public class UserBehaviorVO {
    /**
     * ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 行为类型：browse/rate/favorite/comment/search/order
     */
    private String behaviorType;

    /**
     * 电影ID（非搜索行为）
     */
    private Integer movieId;

    /**
     * 电影名称（关联查询）
     */
    private String movieName;

    /**
     * 搜索关键词（仅搜索行为）
     */
    private String searchKeyword;

    /**
     * 评分（仅评分行为）
     */
    private BigDecimal rating;

    /**
     * 浏览时长（秒，仅浏览行为）
     */
    private Integer duration;

    /**
     * 设备类型：web/mobile/app
     */
    private String deviceType;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 行为时间
     */
    private LocalDateTime createTime;
}
