package com.project.platform.controller;

import com.project.platform.dto.MovieDTO;
import com.project.platform.entity.Movie;
import com.project.platform.mapper.MovieTagRelationMapper;
import com.project.platform.mapper.MovieTypeRelationMapper;
import com.project.platform.service.MovieService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 电影信息管理
 */
@RestController
@RequestMapping("/admin/movie")
public class MovieController {

    @Resource
    private MovieService movieService;
    @Resource
    private MovieTypeRelationMapper movieTypeRelationMapper;
    @Resource
    private MovieTagRelationMapper movieTagRelationMapper;

    @GetMapping("page")
    public ResponseVO<PageVO<Movie>> page(@RequestParam Map<String, Object> query,
                                          @RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<Movie> page = movieService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);
    }

    /**
     * 电影详情（含类型ID、标签ID列表）
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<Map<String, Object>> selectById(@PathVariable("id") Integer id) {
        Movie movie = movieService.selectById(id);
        Map<String, Object> result = new HashMap<>();
        result.put("movie", movie);
        result.put("typeIds", movieTypeRelationMapper.listTypeIdsByMovieId(id));
        result.put("tagIds", movieTagRelationMapper.listTagIdsByMovieId(id));
        return ResponseVO.ok(result);
    }

    @GetMapping("list")
    public ResponseVO<List<Movie>> list() {
        return ResponseVO.ok(movieService.list());
    }

    @PostMapping("add")
    public ResponseVO<Void> add(@RequestBody MovieDTO dto) {
        movieService.insert(dto);
        return ResponseVO.ok();
    }

    @PutMapping("update")
    public ResponseVO<Void> update(@RequestBody MovieDTO dto) {
        movieService.update(dto);
        return ResponseVO.ok();
    }

    @DeleteMapping("delete")
    public ResponseVO<Void> delete(@RequestBody List<Integer> ids) {
        movieService.removeByIds(ids);
        return ResponseVO.ok();
    }
}

