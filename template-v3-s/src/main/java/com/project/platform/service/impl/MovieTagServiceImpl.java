package com.project.platform.service.impl;

import com.project.platform.entity.MovieTag;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.MovieTagMapper;
import com.project.platform.service.MovieTagService;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 电影标签服务实现
 */
@Service
public class MovieTagServiceImpl implements MovieTagService {

    @Resource
    private MovieTagMapper movieTagMapper;

    @Override
    public PageVO<MovieTag> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<MovieTag> page = new PageVO<>();
        List<MovieTag> list = movieTagMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(movieTagMapper.queryCount(query));
        return page;
    }

    @Override
    public MovieTag selectById(Integer id) {
        return movieTagMapper.selectById(id);
    }

    @Override
    public List<MovieTag> list() {
        return movieTagMapper.list();
    }

    @Override
    public void insert(MovieTag entity) {
        validate(entity);
        MovieTag exist = movieTagMapper.selectByName(entity.getName());
        if (exist != null) {
            throw new CustomException("电影标签名称已存在");
        }
        if (entity.getStatus() == null) {
            entity.setStatus("active");
        }
        movieTagMapper.insert(entity);
    }

    @Override
    public void updateById(MovieTag entity) {
        validate(entity);
        MovieTag exist = movieTagMapper.selectByName(entity.getName());
        if (exist != null && !exist.getId().equals(entity.getId())) {
            throw new CustomException("电影标签名称已存在");
        }
        movieTagMapper.updateById(entity);
    }

    @Override
    public void removeByIds(List<Integer> ids) {
        movieTagMapper.removeByIds(ids);
    }

    private void validate(MovieTag entity) {
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new CustomException("电影标签名称不能为空");
        }
    }
}

