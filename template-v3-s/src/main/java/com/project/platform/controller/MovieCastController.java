package com.project.platform.controller;

import com.project.platform.entity.MovieCast;
import com.project.platform.service.MovieCastService;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 演职人员管理
 */
@RestController
@RequestMapping("/admin/movie-cast")
public class MovieCastController {

    @Resource
    private MovieCastService movieCastService;

    /**
     * 根据电影ID查询演职人员列表
     */
    @GetMapping("list/{movieId}")
    public ResponseVO<List<MovieCast>> listByMovieId(@PathVariable("movieId") Integer movieId) {
        return ResponseVO.ok(movieCastService.listByMovieId(movieId));
    }

    @PostMapping("add")
    public ResponseVO<Void> add(@RequestBody MovieCast entity) {
        movieCastService.insert(entity);
        return ResponseVO.ok();
    }

    @PutMapping("update")
    public ResponseVO<Void> update(@RequestBody MovieCast entity) {
        movieCastService.updateById(entity);
        return ResponseVO.ok();
    }

    @DeleteMapping("delete/{id}")
    public ResponseVO<Void> delete(@PathVariable("id") Integer id) {
        movieCastService.deleteById(id);
        return ResponseVO.ok();
    }
}

