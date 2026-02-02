package com.project.platform.service;

import com.project.platform.entity.MovieTag;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 电影标签服务
 */
public interface MovieTagService {

    PageVO<MovieTag> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    MovieTag selectById(Integer id);

    List<MovieTag> list();

    void insert(MovieTag entity);

    void updateById(MovieTag entity);

    void removeByIds(List<Integer> ids);
}

