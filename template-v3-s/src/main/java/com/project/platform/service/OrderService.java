package com.project.platform.service;

import com.project.platform.dto.OrderCreateDTO;
import com.project.platform.entity.Order;
import com.project.platform.entity.OrderDetail;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单（选座购票）
     */
    Order createOrder(OrderCreateDTO dto);

    /**
     * 分页查询订单（管理员）
     */
    PageVO<Order> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    /**
     * 根据订单号查询订单
     */
    Order selectByOrderNo(String orderNo);

    /**
     * 根据ID查询订单
     */
    Order selectById(Integer id);

    /**
     * 查询用户订单列表
     */
    List<Order> listByUserId(Integer userId);

    /**
     * 查询订单明细（座位信息）
     */
    List<OrderDetail> listOrderDetails(Integer orderId);

    /**
     * 取消订单
     */
    void cancelOrder(String orderNo);

    /**
     * 支付订单
     */
    void payOrder(String orderNo);

}
