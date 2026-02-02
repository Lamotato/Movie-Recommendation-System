package com.project.platform.service.impl;

import com.project.platform.dto.OrderCreateDTO;
import com.project.platform.entity.*;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.*;
import com.project.platform.service.OrderService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private ScreeningMapper screeningMapper;
    @Resource
    private SeatMapper seatMapper;
    @Resource
    private MovieMapper movieMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(OrderCreateDTO dto) {
        // 获取当前用户
        var currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            throw new CustomException("请先登录");
        }
        Integer userId = currentUser.getId();

        // 验证场次
        Screening screening = screeningMapper.selectByPrimaryKey(dto.getScreeningId());
        if (screening == null) {
            throw new CustomException("场次不存在");
        }
        if (!"active".equals(screening.getStatus())) {
            throw new CustomException("场次未激活，无法购票");
        }
        if (screening.getStartTime().isBefore(LocalDateTime.now())) {
            throw new CustomException("场次已开始，无法购票");
        }

        // 验证座位
        if (dto.getSeatIds() == null || dto.getSeatIds().isEmpty()) {
            throw new CustomException("请选择座位");
        }
        List<Seat> seats = seatMapper.listByIds(dto.getSeatIds());
        if (seats.size() != dto.getSeatIds().size()) {
            throw new CustomException("部分座位不存在");
        }

        // 检查座位是否属于该场次的房间
        for (Seat seat : seats) {
            if (!seat.getRoomId().equals(screening.getRoomId())) {
                throw new CustomException("座位与场次不匹配");
            }
            if (!"active".equals(seat.getStatus())) {
                throw new CustomException("座位" + seat.getRowNum() + "排" + seat.getColNum() + "座不可用");
            }
        }

        // 检查座位是否已被占用（查询该场次已支付的订单）
        Map<String, Object> query = new java.util.HashMap<>();
        query.put("screeningId", dto.getScreeningId());
        query.put("status", "paid");
        List<Order> paidOrders = orderMapper.queryPage(0, Integer.MAX_VALUE, query);
        List<Integer> occupiedSeatIds = new ArrayList<>();
        for (Order order : paidOrders) {
            List<OrderDetail> details = orderDetailMapper.listByOrderId(order.getId());
            for (OrderDetail detail : details) {
                occupiedSeatIds.add(detail.getSeatId());
            }
        }
        for (Seat seat : seats) {
            if (occupiedSeatIds.contains(seat.getId())) {
                throw new CustomException("座位" + seat.getRowNum() + "排" + seat.getColNum() + "座已被占用");
            }
        }

        // 计算总金额
        BigDecimal totalAmount = screening.getPrice().multiply(new BigDecimal(seats.size()));

        // 生成订单号
        String orderNo = generateOrderNo();

        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setScreeningId(dto.getScreeningId());
        order.setTotalAmount(totalAmount);
        order.setSeatCount(seats.size());
        order.setStatus("pending");
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);

        // 创建订单明细
        List<OrderDetail> details = new ArrayList<>();
        for (Seat seat : seats) {
            OrderDetail detail = new OrderDetail();
            detail.setOrderId(order.getId());
            detail.setSeatId(seat.getId());
            detail.setPrice(screening.getPrice());
            details.add(detail);
        }
        orderDetailMapper.batchInsert(details);

        return order;
    }

    @Override
    public PageVO<Order> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<Order> page = new PageVO<>();
        page.setList(orderMapper.queryPage((pageNum - 1) * pageSize, pageSize, query));
        page.setTotal(orderMapper.queryCount(query));
        return page;
    }

    @Override
    public Order selectByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public Order selectById(Integer id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Order> listByUserId(Integer userId) {
        return orderMapper.listByUserId(userId);
    }

    @Override
    public List<OrderDetail> listOrderDetails(Integer orderId) {
        return orderDetailMapper.listByOrderId(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new CustomException("只能取消待支付订单");
        }
        order.setStatus("cancelled");
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new CustomException("订单不存在");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new CustomException("订单状态不正确，无法支付");
        }
        order.setStatus("paid");
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
