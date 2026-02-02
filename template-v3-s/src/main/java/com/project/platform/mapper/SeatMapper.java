package com.project.platform.mapper;

import com.project.platform.entity.Seat;
import java.util.List;

/**
* @author TOYA
* @description 针对表【seat(座位表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:34
* @Entity com.project.platform.entity.Seat
*/
public interface SeatMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Seat record);

    int insertSelective(Seat record);

    Seat selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Seat record);

    int updateByPrimaryKey(Seat record);

    List<Seat> listByRoomId(Integer roomId);

    Seat selectByRoomIdAndPosition(Integer roomId, Integer rowNum, Integer colNum);

    List<Seat> listByIds(List<Integer> seatIds);

}
