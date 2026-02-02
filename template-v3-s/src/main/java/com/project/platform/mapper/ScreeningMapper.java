package com.project.platform.mapper;

import com.project.platform.entity.Screening;
import java.util.List;
import java.util.Map;

/**
* @author TOYA
* @description 针对表【screening(放映场次表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:34
* @Entity com.project.platform.entity.Screening
*/
public interface ScreeningMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(Screening record);

    int insertSelective(Screening record);

    Screening selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Screening record);

    int updateByPrimaryKey(Screening record);

    List<Screening> queryPage(int offset, int pageSize, Map<String, Object> query);

    int queryCount(Map<String, Object> query);

    int updateById(Screening record);

    int removeById(Integer id);

}
