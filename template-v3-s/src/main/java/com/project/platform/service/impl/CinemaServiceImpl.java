package com.project.platform.service.impl;

import com.project.platform.dto.CinemaDTO;
import com.project.platform.dto.CinemaRoomDTO;
import com.project.platform.entity.Cinema;
import com.project.platform.entity.CinemaRoom;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.CinemaMapper;
import com.project.platform.mapper.CinemaRoomMapper;
import com.project.platform.mapper.SeatMapper;
import com.project.platform.service.CinemaService;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 影院服务实现类
 */
@Service
public class CinemaServiceImpl implements CinemaService {

    @Resource
    private CinemaMapper cinemaMapper;
    @Resource
    private CinemaRoomMapper cinemaRoomMapper;
    @Resource
    private SeatMapper seatMapper;

    @Override
    public PageVO<Cinema> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<Cinema> page = new PageVO<>();
        List<Cinema> list = cinemaMapper.queryPage((pageNum - 1) * pageSize, pageSize, query);
        page.setList(list);
        page.setTotal(cinemaMapper.queryCount(query));
        return page;
    }

    @Override
    public Cinema selectById(Integer id) {
        return cinemaMapper.selectByPrimaryKey(id);
    }

    @Override
    public void insert(CinemaDTO dto) {
        validate(dto);
        Cinema cinema = new Cinema();
        BeanUtils.copyProperties(dto, cinema);
        if (cinema.getStatus() == null) {
            cinema.setStatus("pending");
        }
        cinema.setCreateTime(LocalDateTime.now());
        cinema.setUpdateTime(LocalDateTime.now());
        cinemaMapper.insert(cinema);
    }

    @Override
    public void update(CinemaDTO dto) {
        if (dto.getId() == null) {
            throw new CustomException("影院ID不能为空");
        }
        validate(dto);
        Cinema cinema = new Cinema();
        BeanUtils.copyProperties(dto, cinema);
        cinema.setUpdateTime(LocalDateTime.now());
        cinemaMapper.updateById(cinema);
    }

    @Override
    public void approve(Integer id, String status) {
        Cinema cinema = cinemaMapper.selectByPrimaryKey(id);
        if (cinema == null) {
            throw new CustomException("影院不存在");
        }
        // 验证状态值是否有效
        if (!"active".equals(status) && !"inactive".equals(status)) {
            throw new CustomException("无效的状态值");
        }
        cinema.setStatus(status);
        cinema.setUpdateTime(LocalDateTime.now());
        cinemaMapper.updateById(cinema);
    }

    @Override
    public void removeById(Integer id) {
        // 检查是否有房间
        List<CinemaRoom> rooms = cinemaRoomMapper.listByCinemaId(id);
        if (rooms != null && !rooms.isEmpty()) {
            throw new CustomException("该影院下存在房间，无法删除");
        }
        cinemaMapper.removeById(id);
    }

    @Override
    public List<CinemaRoom> listRoomsByCinemaId(Integer cinemaId) {
        return cinemaRoomMapper.listByCinemaId(cinemaId);
    }

    @Override
    public void insertRoom(CinemaRoomDTO dto) {
        validateRoom(dto);
        CinemaRoom room = new CinemaRoom();
        BeanUtils.copyProperties(dto, room);
        if (room.getStatus() == null) {
            room.setStatus("active");
        }
        room.setCreateTime(LocalDateTime.now());
        cinemaRoomMapper.insert(room);
    }

    @Override
    public void updateRoom(CinemaRoomDTO dto) {
        if (dto.getId() == null) {
            throw new CustomException("房间ID不能为空");
        }
        validateRoom(dto);
        CinemaRoom room = new CinemaRoom();
        BeanUtils.copyProperties(dto, room);
        cinemaRoomMapper.updateById(room);
    }

    @Override
    public void removeRoomById(Integer id) {
        // 检查是否有座位
        List<com.project.platform.entity.Seat> seats = seatMapper.listByRoomId(id);
        if (seats != null && !seats.isEmpty()) {
            throw new CustomException("该房间下存在座位，无法删除");
        }
        cinemaRoomMapper.removeById(id);
    }

    private void validate(CinemaDTO dto) {
        if (StringUtils.isBlank(dto.getName())) {
            throw new CustomException("影院名称不能为空");
        }
    }

    private void validateRoom(CinemaRoomDTO dto) {
        if (dto.getCinemaId() == null) {
            throw new CustomException("影院ID不能为空");
        }
        if (StringUtils.isBlank(dto.getName())) {
            throw new CustomException("房间名称不能为空");
        }
        if (dto.getRowCount() == null || dto.getRowCount() <= 0) {
            throw new CustomException("行数必须大于0");
        }
        if (dto.getColCount() == null || dto.getColCount() <= 0) {
            throw new CustomException("列数必须大于0");
        }
    }
}
