package com.project.platform.mapper;

import com.project.platform.entity.RecommendationResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author TOYA
* @description 针对表【recommendation_result(推荐结果表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:34
* @Entity com.project.platform.entity.RecommendationResult
*/
public interface RecommendationResultMapper {

    int deleteByPrimaryKey(Long id);

    int insert(RecommendationResult record);

    int insertSelective(RecommendationResult record);

    RecommendationResult selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RecommendationResult record);

    int updateByPrimaryKey(RecommendationResult record);

    /**
     * 根据用户ID和推荐类型查询推荐结果
     * @param userId 用户ID
     * @param recommendationType 推荐类型
     * @return 推荐结果
     */
    RecommendationResult selectByUserIdAndType(@Param("userId") Integer userId, 
                                                @Param("recommendationType") String recommendationType);

    /**
     * 删除过期的推荐结果
     * @return 删除数量
     */
    int deleteExpiredResults();

    /**
     * 根据推荐类型查询推荐结果列表
     * @param recommendationType 推荐类型（可选，null表示所有类型）
     * @return 推荐结果列表
     */
    List<RecommendationResult> selectByType(@Param("recommendationType") String recommendationType);

    /**
     * 查询所有推荐结果
     * @return 推荐结果列表
     */
    List<RecommendationResult> selectAll();
}
