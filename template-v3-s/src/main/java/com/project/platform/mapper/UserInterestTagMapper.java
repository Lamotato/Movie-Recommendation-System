package com.project.platform.mapper;

import com.project.platform.entity.UserInterestTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author TOYA
* @description 针对表【user_interest_tag(用户兴趣标签表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:34
* @Entity com.project.platform.entity.UserInterestTag
*/
public interface UserInterestTagMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UserInterestTag record);

    int insertSelective(UserInterestTag record);

    UserInterestTag selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserInterestTag record);

    int updateByPrimaryKey(UserInterestTag record);

    /**
     * 根据用户ID查询兴趣标签列表
     * @param userId 用户ID
     * @return 兴趣标签列表
     */
    List<UserInterestTag> selectByUserId(@Param("userId") Integer userId);
}
