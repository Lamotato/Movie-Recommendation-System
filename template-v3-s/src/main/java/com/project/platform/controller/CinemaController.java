package com.project.platform.controller;

import com.project.platform.dto.CinemaDTO;
import com.project.platform.dto.CinemaRoomDTO;
import com.project.platform.entity.Cinema;
import com.project.platform.entity.CinemaRoom;
import com.project.platform.exception.NotFoundException;
import com.project.platform.service.CinemaService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 影院管理控制器（管理员端）
 */
@RestController
@RequestMapping("/admin/cinema")
public class CinemaController {

    @Resource
    private CinemaService cinemaService;

    /**
     * 分页查询影院
     */
    @GetMapping("page")
    public ResponseVO<PageVO<Cinema>> page(@RequestParam Map<String, Object> query,
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<Cinema> page = cinemaService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);
    }

    /**
     * 根据ID查询影院
     */
    @GetMapping("{id}")
    public ResponseVO<Cinema> selectById(@PathVariable("id") Integer id) {
        Cinema cinema = cinemaService.selectById(id);
        if (cinema == null) {
            throw new NotFoundException("影院不存在");
        }
        return ResponseVO.ok(cinema);
    }

    /**
     * 新增影院
     */
    @PostMapping("add")
    public ResponseVO<Void> add(@RequestBody CinemaDTO dto) {
        cinemaService.insert(dto);
        return ResponseVO.ok();
    }

    /**
     * 更新影院
     */
    @PutMapping("update")
    public ResponseVO<Void> update(@RequestBody CinemaDTO dto) {
        cinemaService.update(dto);
        return ResponseVO.ok();
    }

    /**
     * 审批影院
     */
    @PutMapping("approve/{id}")
    public ResponseVO<Void> approve(@PathVariable("id") Integer id) {
        cinemaService.approve(id);
        return ResponseVO.ok();
    }

    /**
     * 删除影院
     */
    @DeleteMapping("delete/{id}")
    public ResponseVO<Void> delete(@PathVariable("id") Integer id) {
        cinemaService.removeById(id);
        return ResponseVO.ok();
    }

    /**
     * 根据影院ID查询房间列表
     */
    @GetMapping("room/list/{cinemaId}")
    public ResponseVO<List<CinemaRoom>> listRooms(@PathVariable("cinemaId") Integer cinemaId) {
        List<CinemaRoom> rooms = cinemaService.listRoomsByCinemaId(cinemaId);
        return ResponseVO.ok(rooms);
    }

    /**
     * 新增房间
     */
    @PostMapping("room/add")
    public ResponseVO<Void> addRoom(@RequestBody CinemaRoomDTO dto) {
        cinemaService.insertRoom(dto);
        return ResponseVO.ok();
    }

    /**
     * 更新房间
     */
    @PutMapping("room/update")
    public ResponseVO<Void> updateRoom(@RequestBody CinemaRoomDTO dto) {
        cinemaService.updateRoom(dto);
        return ResponseVO.ok();
    }

    /**
     * 删除房间
     */
    @DeleteMapping("room/delete/{id}")
    public ResponseVO<Void> deleteRoom(@PathVariable("id") Integer id) {
        cinemaService.removeRoomById(id);
        return ResponseVO.ok();
    }
}
