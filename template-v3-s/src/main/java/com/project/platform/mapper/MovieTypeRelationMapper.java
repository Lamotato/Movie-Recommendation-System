package com.project.platform.mapper;

import com.project.platform.entity.MovieTypeRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author TOYA
* @description 针对表【movie_type_relation(电影类型关联表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:33
* @Entity com.project.platform.entity.MovieTypeRelation
*/
public interface MovieTypeRelationMapper {

    List<Integer> listTypeIdsByMovieId(Integer movieId);

    int batchInsert(List<MovieTypeRelation> list);

    int deleteByMovieId(Integer movieId);

    /**
     * 根据类型ID查询电影ID列表
     * @param typeId 类型ID
     * @return 电影ID列表
     */
    List<Integer> listMovieIdsByTypeId(@Param("typeId") Integer typeId);

    /**
     * 根据类型ID列表查询有共同类型的电影ID列表（用于相似度计算）
     * @param typeIds 类型ID列表
     * @param excludeMovieId 排除的电影ID
     * @param limit 限制数量
     * @return 电影ID列表
     */
    List<Integer> listSimilarMovieIdsByTypeIds(@Param("typeIds") List<Integer> typeIds, 
                                                 @Param("excludeMovieId") Integer excludeMovieId,
                                                 @Param("limit") Integer limit);
}
