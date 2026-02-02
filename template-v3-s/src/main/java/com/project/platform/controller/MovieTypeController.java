package com.project.platform.controller;

import com.project.platform.entity.MovieType;
import com.project.platform.service.MovieTypeService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 电影类型管理
 */
@RestController
@RequestMapping("/admin/movie-type")
public class MovieTypeController {

    @Resource
    private MovieTypeService movieTypeService;

    @GetMapping("page")
    public ResponseVO<PageVO<MovieType>> page(@RequestParam Map<String, Object> query,
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<MovieType> page = movieTypeService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);
    }

    @GetMapping("selectById/{id}")
    public ResponseVO<MovieType> selectById(@PathVariable("id") Integer id) {
        return ResponseVO.ok(movieTypeService.selectById(id));
    }

    @GetMapping("list")
    public ResponseVO<List<MovieType>> list() {
        return ResponseVO.ok(movieTypeService.list());
    }

    @PostMapping("add")
    public ResponseVO<Void> add(@RequestBody MovieType entity) {
        movieTypeService.insert(entity);
        return ResponseVO.ok();
    }

    @PutMapping("update")
    public ResponseVO<Void> update(@RequestBody MovieType entity) {
        movieTypeService.updateById(entity);
        return ResponseVO.ok();
    }

    @DeleteMapping("delete")
    public ResponseVO<Void> delete(@RequestBody List<Integer> ids) {
        movieTypeService.removeByIds(ids);
        return ResponseVO.ok();
    }
}

