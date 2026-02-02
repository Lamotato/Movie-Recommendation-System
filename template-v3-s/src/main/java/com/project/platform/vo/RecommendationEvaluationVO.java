package com.project.platform.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 推荐效果评估视图对象
 */
@Data
public class RecommendationEvaluationVO {
    /**
     * 评估日期
     */
    private LocalDate evaluationDate;
    
    /**
     * 推荐类型
     */
    private String recommendationType;
    
    /**
     * Precision@N（准确率）
     */
    private BigDecimal precisionAtN;
    
    /**
     * Recall@N（召回率）
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
     * 推荐总数
     */
    private Integer totalRecommendations;
    
    /**
     * 用户交互总数
     */
    private Integer totalInteractions;
    
    /**
     * 图表数据（用于可视化）
     */
    private Map<String, Object> chartData;
    
    /**
     * 时间序列数据（用于趋势分析）
     */
    private List<TimeSeriesData> timeSeriesData;
    
    @Data
    public static class TimeSeriesData {
        private LocalDate date;
        private BigDecimal precision;
        private BigDecimal recall;
        private BigDecimal diversity;
        private BigDecimal satisfaction;
    }
}
