package com.project.platform.service.impl;

import com.project.platform.dto.MovieDTO;
import com.project.platform.entity.Movie;
import com.project.platform.entity.MovieCast;
import com.project.platform.entity.MovieTagRelation;
import com.project.platform.entity.MovieTypeRelation;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.MovieCastMapper;
import com.project.platform.mapper.MovieMapper;
import com.project.platform.mapper.MovieTagRelationMapper;
import com.project.platform.mapper.MovieTypeRelationMapper;
import com.project.platform.service.MovieService;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 电影信息服务实现（含多类型、多标签、基础校验）
 */
@Service
public class MovieServiceImpl implements MovieService {

    @Resource
    private MovieMapper movieMapper;
    @Resource
    private MovieTypeRelationMapper movieTypeRelationMapper;
    @Resource
    private MovieTagRelationMapper movieTagRelationMapper;
    @Resource
    private MovieCastMapper movieCastMapper;

    @Override
    public PageVO<Movie> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<Movie> page = new PageVO<>();
        List<Movie> list = movieMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(movieMapper.queryCount(query));
        return page;
    }

    @Override
    public Movie selectById(Integer id) {
        return movieMapper.selectById(id);
    }

    @Override
    public List<Movie> list() {
        return movieMapper.list();
    }

    @Override
    public void insert(MovieDTO dto) {
        validate(dto);
        Movie movie = new Movie();
        BeanUtils.copyProperties(dto, movie);
        if (movie.getStatus() == null) {
            movie.setStatus("pending");
        }
        movieMapper.insert(movie);
        // 保存类型、标签关联
        saveRelations(movie.getId(), dto.getTypeIds(), dto.getTagIds());
        // 保存演职人员信息
        saveCast(movie.getId(), dto.getCast());
    }

    @Override
    public void update(MovieDTO dto) {
        if (dto.getId() == null) {
            throw new CustomException("电影ID不能为空");
        }
        validate(dto);
        Movie movie = new Movie();
        BeanUtils.copyProperties(dto, movie);
        movieMapper.updateById(movie);
        // 先删除旧关联，再重新插入
        movieTypeRelationMapper.deleteByMovieId(movie.getId());
        movieTagRelationMapper.deleteByMovieId(movie.getId());
        movieCastMapper.deleteByMovieId(movie.getId());
        saveRelations(movie.getId(), dto.getTypeIds(), dto.getTagIds());
        // 保存演职人员信息
        saveCast(movie.getId(), dto.getCast());
    }

    @Override
    public void removeByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        for (Integer id : ids) {
            // 删除类型、标签、演职人员关联
            movieTypeRelationMapper.deleteByMovieId(id);
            movieTagRelationMapper.deleteByMovieId(id);
            movieCastMapper.deleteByMovieId(id);
        }
        movieMapper.removeByIds(ids);
    }

    /**
     * 校验电影数据完整性与合法性
     */
    private void validate(MovieDTO dto) {
        if (StringUtils.isBlank(dto.getName())) {
            throw new CustomException("电影名称不能为空");
        }
        if (dto.getTypeIds() == null || dto.getTypeIds().isEmpty()) {
            throw new CustomException("电影类型至少选择1个");
        }
        if (dto.getTypeIds().size() > 3) {
            throw new CustomException("电影类型最多只能选择3个");
        }
        if (dto.getDuration() != null && dto.getDuration() <= 0) {
            throw new CustomException("电影时长必须为正数");
        }
        if (StringUtils.isNotBlank(dto.getPosterUrl()) && !isValidUrl(dto.getPosterUrl())) {
            throw new CustomException("海报URL格式不合法");
        }
        if (StringUtils.isNotBlank(dto.getTrailerUrl()) && !isValidUrl(dto.getTrailerUrl())) {
            throw new CustomException("预告片URL格式不合法");
        }
    }

    private boolean isValidUrl(String urlStr) {
        try {
            new URL(urlStr);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private void saveRelations(Integer movieId, List<Integer> typeIds, List<Integer> tagIds) {
        if (typeIds != null && !typeIds.isEmpty()) {
            List<MovieTypeRelation> typeRelations = new ArrayList<>();
            for (Integer typeId : typeIds) {
                MovieTypeRelation relation = new MovieTypeRelation();
                relation.setMovieId(movieId);
                relation.setTypeId(typeId);
                typeRelations.add(relation);
            }
            movieTypeRelationMapper.batchInsert(typeRelations);
        }
        if (tagIds != null && !tagIds.isEmpty()) {
            List<MovieTagRelation> tagRelations = new ArrayList<>();
            for (Integer tagId : tagIds) {
                MovieTagRelation relation = new MovieTagRelation();
                relation.setMovieId(movieId);
                relation.setTagId(tagId);
                tagRelations.add(relation);
            }
            movieTagRelationMapper.batchInsert(tagRelations);
        }
    }

    /**
     * 保存演职人员信息
     *
     * @param movieId 电影ID
     * @param cast    主演列表，多个主演用逗号分隔
     */
    private void saveCast(Integer movieId, String cast) {
        if (StringUtils.isBlank(cast)) {
            return;
        }
        // 将主演字符串按逗号分割，创建多个MovieCast记录
        String[] actors = cast.split(",");
        int sortOrder = 0;
        for (String actor : actors) {
            String name = actor.trim();
            if (StringUtils.isNotBlank(name)) {
                MovieCast movieCast = new MovieCast();
                movieCast.setMovieId(movieId);
                movieCast.setName(name);
                movieCast.setRole("actor");
                movieCast.setSortOrder(sortOrder++);
                movieCastMapper.insert(movieCast);
            }
        }
    }
}

