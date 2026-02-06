package com.project.platform.controller;

import com.project.platform.entity.Seat;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.SeatMapper;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 座位管理控制器（管理员端）
 */
@RestController
@RequestMapping("/admin/seat")
public class SeatController {

    @Resource
    private SeatMapper seatMapper;

    /**
     * 根据房间ID查询座位列表
     */
    @GetMapping("list/{roomId}")
    public ResponseVO<List<Seat>> listByRoomId(@PathVariable("roomId") Integer roomId) {
        List<Seat> seats = seatMapper.listByRoomId(roomId);
        return ResponseVO.ok(seats);
    }

    /**
     * 生成座位
     */
    @PostMapping("generate")
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> generate(@RequestBody GenerateSeatRequest request) {
        // 先删除该房间的所有座位
        List<Seat> existingSeats = seatMapper.listByRoomId(request.getRoomId());
        for (Seat seat : existingSeats) {
            seatMapper.deleteByPrimaryKey(seat.getId());
        }

        // 生成新座位
        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= request.getRowCount(); row++) {
            for (int col = 1; col <= request.getColCount(); col++) {
                Seat seat = new Seat();
                seat.setRoomId(request.getRoomId());
                seat.setRowNum(row);
                seat.setColNum(col);
                seat.setSeatType("normal");
                seat.setStatus("active");
                seat.setSalesStatus("unsold");
                seats.add(seat);
            }
        }

        // 批量插入
        for (Seat seat : seats) {
            seatMapper.insert(seat);
        }

        return ResponseVO.ok();
    }

    /**
     * 更新座位
     */
    @PutMapping("update")
    public ResponseVO<Void> update(@RequestBody Seat seat) {
        if (seat.getId() == null) {
            throw new CustomException("座位ID不能为空");
        }
        seatMapper.updateByPrimaryKeySelective(seat);
        return ResponseVO.ok();
    }

    /**
     * 生成座位请求参数
     */
    public static class GenerateSeatRequest {
        private Integer roomId;
        private Integer rowCount;
        private Integer colCount;

        public Integer getRoomId() {
            return roomId;
        }

        public void setRoomId(Integer roomId) {
            this.roomId = roomId;
        }

        public Integer getRowCount() {
            return rowCount;
        }

        public void setRowCount(Integer rowCount) {
            this.rowCount = rowCount;
        }

        public Integer getColCount() {
            return colCount;
        }

        public void setColCount(Integer colCount) {
            this.colCount = colCount;
        }
    }
}
