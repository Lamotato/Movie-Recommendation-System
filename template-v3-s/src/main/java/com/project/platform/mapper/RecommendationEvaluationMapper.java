package com.project.platform.mapper;

import com.project.platform.entity.RecommendationEvaluation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author TOYA
* @description 针对表【recommendation_evaluation(推荐效果评估表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:34
* @Entity com.project.platform.entity.RecommendationEvaluation
*/
public interface RecommendationEvaluationMapper {

    int deleteByPrimaryKey(Long id);

    int insert(RecommendationEvaluation record);

    int insertSelective(RecommendationEvaluation record);

    RecommendationEvaluation selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RecommendationEvaluation record);

    int updateByPrimaryKey(RecommendationEvaluation record);

    /**
     * 根据日期范围和推荐类型查询评估结果
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param recommendationType 推荐类型（可选）
     * @return 评估结果列表
     */
    List<RecommendationEvaluation> selectByDateRange(@Param("startDate") java.time.LocalDate startDate,
                                                      @Param("endDate") java.time.LocalDate endDate,
                                                      @Param("recommendationType") String recommendationType);

    /**
     * 根据推荐类型和日期查询评估结果
     * @param recommendationType 推荐类型
     * @param evaluationDate 评估日期
     * @return 评估结果
     */
    RecommendationEvaluation selectByTypeAndDate(@Param("recommendationType") String recommendationType,
                                                  @Param("evaluationDate") java.time.LocalDate evaluationDate);

}
