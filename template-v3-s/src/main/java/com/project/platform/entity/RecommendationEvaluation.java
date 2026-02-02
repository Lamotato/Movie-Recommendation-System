package com.project.platform.entity;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 推荐效果评估表
 */
@Data
public class RecommendationEvaluation {
    /**
     * ID
     */
    private Integer id;

    /**
     * 评估日期
     */
    private LocalDate evaluationDate;

    /**
     * 推荐类型
     */
    private String recommendationType;

    /**
     * Precision@N
     */
    private BigDecimal precisionAtN;

    /**
     * Recall@N
     */
    private BigDecimal recallAtN;

    /**
     * 多样性指标
     */
    private BigDecimal diversity;

    /**
     * 用户满意度
     */
    private BigDecimal userSatisfaction;

    /**
     * 点击率
     */
    private BigDecimal clickThroughRate;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}