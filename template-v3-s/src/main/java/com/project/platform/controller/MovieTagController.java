package com.project.platform.controller;

import com.project.platform.entity.MovieTag;
import com.project.platform.service.MovieTagService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 电影标签管理
 */
@RestController
@RequestMapping("/admin/movie-tag")
public class MovieTagController {

    @Resource
    private MovieTagService movieTagService;

    @GetMapping("page")
    public ResponseVO<PageVO<MovieTag>> page(@RequestParam Map<String, Object> query,
                                             @RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<MovieTag> page = movieTagService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);
    }

    @GetMapping("selectById/{id}")
    public ResponseVO<MovieTag> selectById(@PathVariable("id") Integer id) {
        return ResponseVO.ok(movieTagService.selectById(id));
    }

    @GetMapping("list")
    public ResponseVO<List<MovieTag>> list() {
        return ResponseVO.ok(movieTagService.list());
    }

    @PostMapping("add")
    public ResponseVO<Void> add(@RequestBody MovieTag entity) {
        movieTagService.insert(entity);
        return ResponseVO.ok();
    }

    @PutMapping("update")
    public ResponseVO<Void> update(@RequestBody MovieTag entity) {
        movieTagService.updateById(entity);
        return ResponseVO.ok();
    }

    @DeleteMapping("delete")
    public ResponseVO<Void> delete(@RequestBody List<Integer> ids) {
        movieTagService.removeByIds(ids);
        return ResponseVO.ok();
    }
}

