package com.project.platform.mapper;

import com.project.platform.entity.Cinema;
import java.util.List;
import java.util.Map;

/**
* @author TOYA
* @description 针对表【cinema(影院表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:33
* @Entity com.project.platform.entity.Cinema
*/
public interface CinemaMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Cinema record);

    int insertSelective(Cinema record);

    Cinema selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cinema record);

    int updateByPrimaryKey(Cinema record);

    List<Cinema> queryPage(int offset, int pageSize, Map<String, Object> query);

    int queryCount(Map<String, Object> query);

    int updateById(Cinema record);

    int removeById(Integer id);

}
