package com.project.platform.mapper;

import com.project.platform.entity.Announcement;

/**
* @author TOYA
* @description 针对表【announcement(公告表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:33
* @Entity com.project.platform.entity.Announcement
*/
public interface AnnouncementMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Announcement record);

    int insertSelective(Announcement record);

    Announcement selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Announcement record);

    int updateByPrimaryKey(Announcement record);

}
