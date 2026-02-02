package com.project.platform.mapper;

import com.project.platform.entity.MovieType;
import java.util.List;
import java.util.Map;

/**
* @author TOYA
 * @description 针对表【movie_type(电影类型表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:33
* @Entity com.project.platform.entity.MovieType
*/
public interface MovieTypeMapper {

    List<MovieType> queryPage(int offset, int pageSize, Map<String, Object> query);

    int queryCount(Map<String, Object> query);

    MovieType selectById(Integer id);

    MovieType selectByName(String name);

    List<MovieType> list();

    int insert(MovieType record);

    int updateById(MovieType record);

    int removeByIds(List<Integer> ids);

}
