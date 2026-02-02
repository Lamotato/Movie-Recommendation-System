package com.project.platform.mapper;

import com.project.platform.entity.MovieRating;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author TOYA
* @description 针对表【movie_rating(电影评分表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:33
* @Entity com.project.platform.entity.MovieRating
*/
public interface MovieRatingMapper {

    int deleteByPrimaryKey(Long id);

    int insert(MovieRating record);

    int insertSelective(MovieRating record);

    MovieRating selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MovieRating record);

    int updateByPrimaryKey(MovieRating record);

    /**
     * 根据用户ID查询评分列表
     * @param userId 用户ID
     * @return 评分列表
     */
    List<MovieRating> selectByUserId(@Param("userId") Integer userId);

    /**
     * 根据用户ID和电影ID查询评分
     * @param userId 用户ID
     * @param movieId 电影ID
     * @return 评分
     */
    MovieRating selectByUserIdAndMovieId(@Param("userId") Integer userId, @Param("movieId") Integer movieId);

    /**
     * 查询所有用户的评分（用于协同过滤）
     * @return 评分列表
     */
    List<MovieRating> selectAll();
}
