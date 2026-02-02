package com.project.platform.mapper;

import com.project.platform.entity.OrderDetail;
import java.util.List;

/**
* @author TOYA
* @description 针对表【order_detail(订单明细表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:33
* @Entity com.project.platform.entity.OrderDetail
*/
public interface OrderDetailMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(OrderDetail record);

    int insertSelective(OrderDetail record);

    OrderDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderDetail record);

    int updateByPrimaryKey(OrderDetail record);

    List<OrderDetail> listByOrderId(Integer orderId);

    int deleteByOrderId(Integer orderId);

    int batchInsert(List<OrderDetail> details);

}
