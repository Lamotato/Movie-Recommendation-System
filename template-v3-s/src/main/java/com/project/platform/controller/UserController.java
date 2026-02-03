package com.project.platform.controller;

import com.project.platform.entity.User;
import com.project.platform.exception.NotFoundException;
import com.project.platform.service.UserService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户管理（管理员）
 */
@RestController
@RequestMapping("/admin/user")
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 分页查询
     */
    @GetMapping("page")
    public ResponseVO<PageVO<User>> page(@RequestParam Map<String, Object> query, 
                                         @RequestParam(defaultValue = "1") Integer pageNum, 
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<User> page = userService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);
    }

    /**
     * 根据id查询
     */
    @GetMapping("selectById/{id}")
    public ResponseVO<User> selectById(@PathVariable("id") Integer id) {
        User entity = userService.selectById(id);
        if (entity == null) {
            throw new NotFoundException("用户不存在");
        }
        return ResponseVO.ok(entity);
    }

    /**
     * 列表
     */
    @GetMapping("list")
    public ResponseVO<List<User>> list() {
        return ResponseVO.ok(userService.list());
    }

    /**
     * 新增
     */
    @PostMapping("add")
    public ResponseVO<Void> add(@RequestBody User entity) {
        userService.insert(entity);
        return ResponseVO.ok();
    }

    /**
     * 更新
     */
    @PutMapping("update")
    public ResponseVO<Void> update(@RequestBody User entity) {
        userService.updateById(entity);
        return ResponseVO.ok();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("delete")
    public ResponseVO<Void> delete(@RequestBody List<Integer> ids) {
        userService.removeByIds(ids);
        return ResponseVO.ok();
    }
}
