package com.project.platform.mapper;

import com.project.platform.entity.CinemaRoom;
import java.util.List;

/**
* @author TOYA
* @description 针对表【cinema_room(影院房间表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:33
* @Entity com.project.platform.entity.CinemaRoom
*/
public interface CinemaRoomMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(CinemaRoom record);

    int insertSelective(CinemaRoom record);

    CinemaRoom selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CinemaRoom record);

    int updateByPrimaryKey(CinemaRoom record);

    List<CinemaRoom> listByCinemaId(Integer cinemaId);

    int updateById(CinemaRoom record);

    int removeById(Integer id);

}
