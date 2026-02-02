package com.project.platform.mapper;

import com.project.platform.entity.Order;
import java.util.List;
import java.util.Map;

/**
* @author TOYA
* @description 针对表【order(订单表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:33
* @Entity com.project.platform.entity.Order
*/
public interface OrderMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByOrderNo(String orderNo);

    List<Order> queryPage(int offset, int pageSize, Map<String, Object> query);

    int queryCount(Map<String, Object> query);

    List<Order> listByUserId(Integer userId);

    int updateById(Order record);

}
