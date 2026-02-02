package com.project.platform.service;

import com.project.platform.dto.ScreeningDTO;
import com.project.platform.entity.Screening;
import com.project.platform.vo.PageVO;

import java.util.Map;

/**
 * 放映场次服务接口
 */
public interface ScreeningService {

    /**
     * 分页查询场次
     */
    PageVO<Screening> page(Map<String, Object> query, Integer pageNum, Integer pageSize);

    /**
     * 根据ID查询场次
     */
    Screening selectById(Integer id);

    /**
     * 新增场次
     */
    void insert(ScreeningDTO dto);

    /**
     * 更新场次
     */
    void update(ScreeningDTO dto);

    /**
     * 审批场次（管理员）
     */
    void approve(Integer id);

    /**
     * 删除场次
     */
    void removeById(Integer id);

}
