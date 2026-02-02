package com.project.platform.mapper;

import com.project.platform.entity.Movie;
import java.util.List;
import java.util.Map;

/**
* @author TOYA
* @description 针对表【movie(电影信息表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:33
* @Entity com.project.platform.entity.Movie
*/
public interface MovieMapper {

    List<Movie> queryPage(int offset, int pageSize, Map<String, Object> query);

    int queryCount(Map<String, Object> query);

    Movie selectById(Integer id);

    List<Movie> list();

    int insert(Movie record);

    int updateById(Movie record);

    int removeByIds(List<Integer> ids);

}
