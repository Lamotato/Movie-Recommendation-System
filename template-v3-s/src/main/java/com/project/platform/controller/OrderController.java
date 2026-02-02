package com.project.platform.controller;

import com.project.platform.dto.OrderCreateDTO;
import com.project.platform.entity.Order;
import com.project.platform.entity.OrderDetail;
import com.project.platform.service.OrderService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单管理控制器
 */
@RestController
@RequestMapping("/user/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    /**
     * 创建订单（选座购票）
     */
    @PostMapping("create")
    public ResponseVO<Order> createOrder(@RequestBody OrderCreateDTO dto) {
        Order order = orderService.createOrder(dto);
        return ResponseVO.ok(order);
    }

    /**
     * 查询用户订单列表
     */
    @GetMapping("list")
    public ResponseVO<List<Order>> list() {
        // 从当前用户获取userId
        com.project.platform.dto.CurrentUserDTO currentUser = com.project.platform.utils.CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            return ResponseVO.fail(401, "请先登录");
        }
        List<Order> orders = orderService.listByUserId(currentUser.getId());
        return ResponseVO.ok(orders);
    }

    /**
     * 根据订单号查询订单详情
     */
    @GetMapping("{orderNo}")
    public ResponseVO<Map<String, Object>> getOrderDetail(@PathVariable("orderNo") String orderNo) {
        Order order = orderService.selectByOrderNo(orderNo);
        if (order == null) {
            return ResponseVO.fail(404, "订单不存在");
        }
        List<OrderDetail> details = orderService.listOrderDetails(order.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("details", details);
        return ResponseVO.ok(result);
    }

    /**
     * 取消订单
     */
    @PutMapping("cancel/{orderNo}")
    public ResponseVO<Void> cancelOrder(@PathVariable("orderNo") String orderNo) {
        orderService.cancelOrder(orderNo);
        return ResponseVO.ok();
    }

    /**
     * 支付订单
     */
    @PutMapping("pay/{orderNo}")
    public ResponseVO<Void> payOrder(@PathVariable("orderNo") String orderNo) {
        orderService.payOrder(orderNo);
        return ResponseVO.ok();
    }
}
