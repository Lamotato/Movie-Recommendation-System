package com.project.platform.service;

import com.project.platform.dto.CinemaDTO;
import com.project.platform.dto.CinemaRoomDTO;
import com.project.platform.entity.Cinema;
import com.project.platform.entity.CinemaRoom;
import com.project.platform.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 影院服务接口
 */
public interface CinemaService {

    /**
     * 分页查询影院
     */
    PageVO<Cinema> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    /**
     * 根据ID查询影院
     */
    Cinema selectById(Integer id);

    /**
     * 新增影院
     */
    void insert(CinemaDTO dto);

    /**
     * 更新影院
     */
    void update(CinemaDTO dto);

    /**
     * 审批影院（管理员）
     */
    void approve(Integer id, String status);

    /**
     * 删除影院
     */
    void removeById(Integer id);

    /**
     * 根据影院ID查询房间列表
     */
    List<CinemaRoom> listRoomsByCinemaId(Integer cinemaId);

    /**
     * 根据ID查询房间
     */
    CinemaRoom getRoomById(Integer id);

    /**
     * 新增房间
     */
    void insertRoom(CinemaRoomDTO dto);

    /**
     * 更新房间
     */
    void updateRoom(CinemaRoomDTO dto);

    /**
     * 删除房间
     */
    void removeRoomById(Integer id);

}
