package com.project.platform.service.impl;

import com.project.platform.entity.MovieType;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.MovieTypeMapper;
import com.project.platform.service.MovieTypeService;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 电影类型服务实现
 */
@Service
public class MovieTypeServiceImpl implements MovieTypeService {

    @Resource
    private MovieTypeMapper movieTypeMapper;

    @Override
    public PageVO<MovieType> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<MovieType> page = new PageVO<>();
        List<MovieType> list = movieTypeMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(movieTypeMapper.queryCount(query));
        return page;
    }

    @Override
    public MovieType selectById(Integer id) {
        return movieTypeMapper.selectById(id);
    }

    @Override
    public List<MovieType> list() {
        return movieTypeMapper.list();
    }

    @Override
    public void insert(MovieType entity) {
        validate(entity);
        // 名称唯一
        MovieType exist = movieTypeMapper.selectByName(entity.getName());
        if (exist != null) {
            throw new CustomException("电影类型名称已存在");
        }
        if (entity.getStatus() == null) {
            entity.setStatus("active");
        }
        movieTypeMapper.insert(entity);
    }

    @Override
    public void updateById(MovieType entity) {
        validate(entity);
        MovieType exist = movieTypeMapper.selectByName(entity.getName());
        if (exist != null && !exist.getId().equals(entity.getId())) {
            throw new CustomException("电影类型名称已存在");
        }
        movieTypeMapper.updateById(entity);
    }

    @Override
    public void removeByIds(List<Integer> ids) {
        movieTypeMapper.removeByIds(ids);
    }

    private void validate(MovieType entity) {
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new CustomException("电影类型名称不能为空");
        }
    }
}

