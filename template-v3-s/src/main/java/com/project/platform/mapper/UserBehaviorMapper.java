package com.project.platform.mapper;

import com.project.platform.entity.UserBehavior;
import com.project.platform.vo.UserBehaviorVO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
* @author TOYA
* @description 针对表【user_behavior(用户行为表)】的数据库操作Mapper
* @createDate 2026-01-12 14:45:34
* @Entity com.project.platform.entity.UserBehavior
*/
public interface UserBehaviorMapper {

    int deleteByPrimaryKey(Long id);

    int insert(UserBehavior record);

    int insertSelective(UserBehavior record);

    UserBehavior selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserBehavior record);

    int updateByPrimaryKey(UserBehavior record);

    /**
     * 根据用户ID和行为类型查询行为列表
     * @param userId 用户ID
     * @param behaviorType 行为类型（可选）
     * @param limit 限制数量
     * @return 行为列表
     */
    List<UserBehaviorVO> selectByUserId(@Param("userId") Integer userId, 
                                        @Param("behaviorType") String behaviorType, 
                                        @Param("limit") Integer limit);

    /**
     * 统计用户行为总数
     * @param userId 用户ID
     * @return 行为总数
     */
    Long countByUserId(@Param("userId") Integer userId);

    /**
     * 按行为类型统计用户行为数量
     * @param userId 用户ID
     * @return 行为类型统计Map（key: behaviorType, value: count）
     */
    List<Map<String, Object>> countByBehaviorType(@Param("userId") Integer userId);

    /**
     * 获取用户最近行为时间
     * @param userId 用户ID
     * @return 最近行为时间
     */
    LocalDateTime getLastBehaviorTime(@Param("userId") Integer userId);

    /**
     * 统计各类型行为数量
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @return 数量
     */
    Long countByUserIdAndType(@Param("userId") Integer userId, @Param("behaviorType") String behaviorType);

}
