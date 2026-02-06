package com.project.platform.service.impl;

import com.project.platform.dto.ScreeningDTO;
import com.project.platform.entity.Screening;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.ScreeningMapper;
import com.project.platform.service.ScreeningService;
import com.project.platform.vo.PageVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 放映场次服务实现类
 */
@Service
public class ScreeningServiceImpl implements ScreeningService {

    @Resource
    private ScreeningMapper screeningMapper;

    @Override
    public PageVO<Screening> page(Map<String, Object> query, Integer pageNum, Integer pageSize) {
        PageVO<Screening> page = new PageVO<>();
        page.setList(screeningMapper.queryPage((pageNum - 1) * pageSize, pageSize, query));
        page.setTotal(screeningMapper.queryCount(query));
        return page;
    }

    @Override
    public Screening selectById(Integer id) {
        return screeningMapper.selectByPrimaryKey(id);
    }

    @Override
    public void insert(ScreeningDTO dto) {
        validate(dto);
        Screening screening = new Screening();
        BeanUtils.copyProperties(dto, screening);
        if (screening.getStatus() == null) {
            screening.setStatus("pending");
        }
        screening.setCreateTime(LocalDateTime.now());
        screening.setUpdateTime(LocalDateTime.now());
        screeningMapper.insert(screening);
    }

    @Override
    public void update(ScreeningDTO dto) {
        if (dto.getId() == null) {
            throw new CustomException("场次ID不能为空");
        }
        validate(dto);
        Screening screening = new Screening();
        BeanUtils.copyProperties(dto, screening);
        screening.setUpdateTime(LocalDateTime.now());
        screeningMapper.updateByPrimaryKey(screening);
    }

    @Override
    public void approve(Integer id, String status) {
        Screening screening = screeningMapper.selectByPrimaryKey(id);
        if (screening == null) {
            throw new CustomException("场次不存在");
        }
        // 验证状态值是否有效
        if (!"active".equals(status) && !"cancelled".equals(status)) {
            throw new CustomException("无效的状态值");
        }
        screening.setStatus(status);
        screening.setUpdateTime(LocalDateTime.now());
        screeningMapper.updateById(screening);
    }

    @Override
    public void removeById(Integer id) {
        screeningMapper.removeById(id);
    }

    private void validate(ScreeningDTO dto) {
        if (dto.getMovieId() == null) {
            throw new CustomException("电影ID不能为空");
        }
        if (dto.getCinemaId() == null) {
            throw new CustomException("影院ID不能为空");
        }
        if (dto.getRoomId() == null) {
            throw new CustomException("房间ID不能为空");
        }
        if (dto.getStartTime() == null) {
            throw new CustomException("开始时间不能为空");
        }
        if (dto.getEndTime() == null) {
            throw new CustomException("结束时间不能为空");
        }
        if (dto.getStartTime().isAfter(dto.getEndTime())) {
            throw new CustomException("开始时间不能晚于结束时间");
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new CustomException("票价必须大于0");
        }
    }
}
