package com.project.platform.mapper;

import com.project.platform.entity.UserFavorite;

/**
* @author TOYA
* @description 针对表【user_favorite(用户收藏表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:34
* @Entity com.project.platform.entity.UserFavorite
*/
public interface UserFavoriteMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UserFavorite record);

    int insertSelective(UserFavorite record);

    UserFavorite selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserFavorite record);

    int updateByPrimaryKey(UserFavorite record);

}
