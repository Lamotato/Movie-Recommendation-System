package com.project.platform.service;

import com.project.platform.dto.UserBehaviorDTO;
import com.project.platform.vo.UserBehaviorStatisticsVO;
import com.project.platform.vo.UserBehaviorVO;

import java.util.List;

/**
 * 用户行为服务接口
 */
public interface BehaviorService {
    /**
     * 记录用户行为
     * @param dto 行为数据
     */
    void recordBehavior(UserBehaviorDTO dto);

    /**
     * 获取用户行为统计
     * @param userId 用户ID
     * @return 统计数据
     */
    UserBehaviorStatisticsVO getStatistics(Integer userId);

    /**
     * 获取用户行为列表
     * @param userId 用户ID
     * @param behaviorType 行为类型（可选）
     * @param limit 限制数量
     * @return 行为列表
     */
    List<UserBehaviorVO> getUserBehaviors(Integer userId, String behaviorType, Integer limit);
}
