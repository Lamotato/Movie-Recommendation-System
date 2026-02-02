package com.project.platform.entity;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 用户兴趣标签表
 */
@Data
public class UserInterestTag {
    /**
     * ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 标签名称（如：科幻、喜剧、悬疑）
     */
    private String tagName;

    /**
     * 权重（0-1之间）
     */
    private BigDecimal weight;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}