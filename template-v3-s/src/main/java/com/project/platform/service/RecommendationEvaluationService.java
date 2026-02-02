package com.project.platform.service;

import com.project.platform.vo.RecommendationEvaluationVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 推荐效果评估服务接口
 */
public interface RecommendationEvaluationService {
    
    /**
     * 计算并保存推荐效果评估
     * @param evaluationDate 评估日期
     * @param recommendationType 推荐类型（可选，null表示所有类型）
     * @param topN Top-N推荐数量，默认10
     * @return 评估结果
     */
    RecommendationEvaluationVO evaluateAndSave(LocalDate evaluationDate, String recommendationType, Integer topN);
    
    /**
     * 计算准确率和召回率
     * @param evaluationDate 评估日期
     * @param recommendationType 推荐类型
     * @param topN Top-N推荐数量
     * @return 包含准确率和召回率的Map
     */
    Map<String, BigDecimal> calculatePrecisionAndRecall(LocalDate evaluationDate, String recommendationType, Integer topN);
    
    /**
     * 计算推荐多样性
     * @param evaluationDate 评估日期
     * @param recommendationType 推荐类型
     * @return 多样性指标（0-1之间）
     */
    BigDecimal calculateDiversity(LocalDate evaluationDate, String recommendationType);
    
    /**
     * 计算用户满意度
     * @param evaluationDate 评估日期
     * @param recommendationType 推荐类型
     * @return 用户满意度（0-1之间）
     */
    BigDecimal calculateUserSatisfaction(LocalDate evaluationDate, String recommendationType);
    
    /**
     * 计算点击率
     * @param evaluationDate 评估日期
     * @param recommendationType 推荐类型
     * @return 点击率（0-1之间）
     */
    BigDecimal calculateClickThroughRate(LocalDate evaluationDate, String recommendationType);
    
    /**
     * 获取评估结果列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param recommendationType 推荐类型（可选）
     * @return 评估结果列表
     */
    List<RecommendationEvaluationVO> getEvaluationList(LocalDate startDate, LocalDate endDate, String recommendationType);
    
    /**
     * 获取可视化报表数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param recommendationType 推荐类型（可选）
     * @return 报表数据
     */
    Map<String, Object> getVisualizationData(LocalDate startDate, LocalDate endDate, String recommendationType);
}
