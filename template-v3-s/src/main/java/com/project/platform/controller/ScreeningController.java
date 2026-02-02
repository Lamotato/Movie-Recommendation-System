package com.project.platform.controller;

import com.project.platform.dto.ScreeningDTO;
import com.project.platform.entity.Screening;
import com.project.platform.service.ScreeningService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 放映场次管理控制器（管理员端）
 */
@RestController
@RequestMapping("/admin/screening")
public class ScreeningController {

    @Resource
    private ScreeningService screeningService;

    /**
     * 分页查询场次
     */
    @GetMapping("page")
    public ResponseVO<PageVO<Screening>> page(@RequestParam Map<String, Object> query,
                                               @RequestParam(defaultValue = "1") Integer pageNum,
                                               @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<Screening> page = screeningService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);
    }

    /**
     * 根据ID查询场次
     */
    @GetMapping("{id}")
    public ResponseVO<Screening> selectById(@PathVariable("id") Integer id) {
        Screening screening = screeningService.selectById(id);
        return ResponseVO.ok(screening);
    }

    /**
     * 新增场次
     */
    @PostMapping("add")
    public ResponseVO<Void> add(@RequestBody ScreeningDTO dto) {
        screeningService.insert(dto);
        return ResponseVO.ok();
    }

    /**
     * 更新场次
     */
    @PutMapping("update")
    public ResponseVO<Void> update(@RequestBody ScreeningDTO dto) {
        screeningService.update(dto);
        return ResponseVO.ok();
    }

    /**
     * 审批场次
     */
    @PutMapping("approve/{id}")
    public ResponseVO<Void> approve(@PathVariable("id") Integer id) {
        screeningService.approve(id);
        return ResponseVO.ok();
    }

    /**
     * 删除场次
     */
    @DeleteMapping("delete/{id}")
    public ResponseVO<Void> delete(@PathVariable("id") Integer id) {
        screeningService.removeById(id);
        return ResponseVO.ok();
    }
}
