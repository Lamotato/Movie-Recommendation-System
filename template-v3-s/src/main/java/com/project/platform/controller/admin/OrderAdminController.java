package com.project.platform.controller.admin;

import com.project.platform.entity.Order;
import com.project.platform.service.OrderService;
import com.project.platform.vo.PageVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 订单管理控制器（管理员端）
 */
@RestController
@RequestMapping("/admin/order")
public class OrderAdminController {

    @Resource
    private OrderService orderService;

    /**
     * 分页查询订单
     */
    @GetMapping("page")
    public ResponseVO<PageVO<Order>> page(@RequestParam Map<String, Object> query,
                                           @RequestParam(defaultValue = "1") Integer pageNum,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageVO<Order> page = orderService.page(query, pageNum, pageSize);
        return ResponseVO.ok(page);
    }

    /**
     * 根据订单号查询订单详情
     */
    @GetMapping("{orderNo}")
    public ResponseVO<Order> getOrder(@PathVariable("orderNo") String orderNo) {
        Order order = orderService.selectByOrderNo(orderNo);
        return ResponseVO.ok(order);
    }
}
