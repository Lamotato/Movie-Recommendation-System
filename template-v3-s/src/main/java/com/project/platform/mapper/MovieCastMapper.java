package com.project.platform.mapper;

import com.project.platform.entity.MovieCast;
import java.util.List;

/**
* @author TOYA
* @description 针对表【movie_cast(演职人员表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:33
* @Entity com.project.platform.entity.MovieCast
*/
public interface MovieCastMapper {

    List<MovieCast> listByMovieId(Integer movieId);

    MovieCast selectById(Integer id);

    int insert(MovieCast record);

    int updateById(MovieCast record);

    int deleteById(Integer id);

    int deleteByMovieId(Integer movieId);

}
