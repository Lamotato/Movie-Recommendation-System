package com.project.platform.mapper;

import com.project.platform.entity.MovieTagRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author TOYA
* @description 针对表【movie_tag_relation(电影标签关联表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:33
* @Entity com.project.platform.entity.MovieTagRelation
*/
public interface MovieTagRelationMapper {

    List<Integer> listTagIdsByMovieId(Integer movieId);

    int batchInsert(List<MovieTagRelation> list);

    int deleteByMovieId(Integer movieId);

    /**
     * 根据标签ID查询电影ID列表
     * @param tagId 标签ID
     * @return 电影ID列表
     */
    List<Integer> listMovieIdsByTagId(@Param("tagId") Integer tagId);

    /**
     * 根据标签名称查询电影ID列表（通过标签名称找到标签ID，再找电影）
     * @param tagName 标签名称
     * @param limit 限制数量
     * @return 电影ID列表
     */
    List<Integer> listMovieIdsByTagName(@Param("tagName") String tagName, @Param("limit") Integer limit);
}
