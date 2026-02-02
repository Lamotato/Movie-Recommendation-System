package com.project.platform.service;

import com.project.platform.entity.MovieCast;

import java.util.List;

/**
 * 演职人员服务
 */
public interface MovieCastService {

    List<MovieCast> listByMovieId(Integer movieId);

    MovieCast selectById(Integer id);

    void insert(MovieCast entity);

    void updateById(MovieCast entity);

    void deleteById(Integer id);
}

