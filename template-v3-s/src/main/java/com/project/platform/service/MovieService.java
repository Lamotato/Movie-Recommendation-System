package com.project.platform.service;

import com.project.platform.dto.MovieDTO;
import com.project.platform.entity.Movie;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 电影信息服务（含多类型、多标签）
 */
public interface MovieService {

    PageVO<Movie> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    Movie selectById(Integer id);

    List<Movie> list();

    /**
     * 新增电影（含类型、标签关联）
     */
    void insert(MovieDTO dto);

    /**
     * 更新电影（含类型、标签关联）
     */
    void update(MovieDTO dto);

    /**
     * 批量删除电影（同时删除关联关系、演职人员）
     */
    void removeByIds(List<Integer> ids);
}

