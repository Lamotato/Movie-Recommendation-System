package com.project.platform.controller;

import com.project.platform.entity.Seat;
import com.project.platform.entity.Screening;
import com.project.platform.mapper.OrderDetailMapper;
import com.project.platform.mapper.OrderMapper;
import com.project.platform.mapper.ScreeningMapper;
import com.project.platform.mapper.SeatMapper;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 场次座位控制器（用户端）
 */
@RestController
@RequestMapping("/user/screening")
public class ScreeningUserController {

    @Resource
    private ScreeningMapper screeningMapper;
    @Resource
    private SeatMapper seatMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;

    /**
     * 获取场次座位信息（含已占用座位）
     */
    @GetMapping("{id}/seats")
    public ResponseVO<Map<String, Object>> getSeats(@PathVariable("id") Integer id) {
        // 查询场次信息
        Screening screening = screeningMapper.selectByPrimaryKey(id);
        if (screening == null) {
            return ResponseVO.fail(404, "场次不存在");
        }

        // 查询该场次的所有座位
        List<Seat> allSeats = seatMapper.listByRoomId(screening.getRoomId());

        // 查询该场次已支付的订单，获取已占用的座位ID
        Map<String, Object> query = new HashMap<>();
        query.put("screeningId", id);
        query.put("status", "paid");
        List<com.project.platform.entity.Order> paidOrders = orderMapper.queryPage(0, Integer.MAX_VALUE, query);
        
        List<Integer> occupiedSeatIds = new ArrayList<>();
        for (com.project.platform.entity.Order order : paidOrders) {
            List<com.project.platform.entity.OrderDetail> details = orderDetailMapper.listByOrderId(order.getId());
            for (com.project.platform.entity.OrderDetail detail : details) {
                occupiedSeatIds.add(detail.getSeatId());
            }
        }

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("screening", screening);
        result.put("seats", allSeats);
        result.put("occupiedSeatIds", occupiedSeatIds);
        
        return ResponseVO.ok(result);
    }
}
