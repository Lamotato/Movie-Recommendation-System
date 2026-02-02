package com.project.platform.service;

import com.project.platform.entity.MovieType;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 电影类型服务
 */
public interface MovieTypeService {

    PageVO<MovieType> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    MovieType selectById(Integer id);

    List<MovieType> list();

    void insert(MovieType entity);

    void updateById(MovieType entity);

    void removeByIds(List<Integer> ids);
}

