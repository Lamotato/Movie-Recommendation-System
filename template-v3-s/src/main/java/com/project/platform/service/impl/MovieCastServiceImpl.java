package com.project.platform.service.impl;

import com.project.platform.entity.MovieCast;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.MovieCastMapper;
import com.project.platform.service.MovieCastService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 演职人员服务实现
 */
@Service
public class MovieCastServiceImpl implements MovieCastService {

    @Resource
    private MovieCastMapper movieCastMapper;

    @Override
    public List<MovieCast> listByMovieId(Integer movieId) {
        return movieCastMapper.listByMovieId(movieId);
    }

    @Override
    public MovieCast selectById(Integer id) {
        return movieCastMapper.selectById(id);
    }

    @Override
    public void insert(MovieCast entity) {
        validate(entity);
        movieCastMapper.insert(entity);
    }

    @Override
    public void updateById(MovieCast entity) {
        if (entity.getId() == null) {
            throw new CustomException("演职人员ID不能为空");
        }
        validate(entity);
        movieCastMapper.updateById(entity);
    }

    @Override
    public void deleteById(Integer id) {
        movieCastMapper.deleteById(id);
    }

    private void validate(MovieCast entity) {
        if (entity.getMovieId() == null) {
            throw new CustomException("电影ID不能为空");
        }
        if (StringUtils.isBlank(entity.getName())) {
            throw new CustomException("演职人员姓名不能为空");
        }
        if (StringUtils.isBlank(entity.getRole())) {
            throw new CustomException("演职人员角色不能为空");
        }
    }
}

