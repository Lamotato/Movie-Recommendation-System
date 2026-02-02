package com.project.platform.mapper;

import com.project.platform.entity.MovieTag;
import java.util.List;
import java.util.Map;

/**
* @author TOYA
* @description 针对表【movie_tag(电影标签表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:33
* @Entity com.project.platform.entity.MovieTag
*/
public interface MovieTagMapper {

    List<MovieTag> queryPage(int offset, int pageSize, Map<String, Object> query);

    int queryCount(Map<String, Object> query);

    MovieTag selectById(Integer id);

    MovieTag selectByName(String name);

    List<MovieTag> list();

    int insert(MovieTag record);

    int updateById(MovieTag record);

    int removeByIds(List<Integer> ids);

}
