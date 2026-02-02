package com.project.platform.service.impl;

import com.project.platform.dto.UserBehaviorDTO;
import com.project.platform.entity.UserBehavior;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.UserBehaviorMapper;
import com.project.platform.service.BehaviorService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.UserBehaviorStatisticsVO;
import com.project.platform.vo.UserBehaviorVO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户行为服务实现类
 */
@Service
public class BehaviorServiceImpl implements BehaviorService {

    @Resource
    private UserBehaviorMapper userBehaviorMapper;

    /**
     * 行为类型常量
     */
    private static final String BEHAVIOR_TYPE_BROWSE = "browse";
    private static final String BEHAVIOR_TYPE_RATE = "rate";
    private static final String BEHAVIOR_TYPE_FAVORITE = "favorite";
    private static final String BEHAVIOR_TYPE_COMMENT = "comment";
    private static final String BEHAVIOR_TYPE_SEARCH = "search";
    private static final String BEHAVIOR_TYPE_ORDER = "order";

    @Override
    public void recordBehavior(UserBehaviorDTO dto) {
        // 获取当前用户
        com.project.platform.dto.CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            throw new CustomException("请先登录");
        }

        // 验证行为类型
        String behaviorType = dto.getBehaviorType();
        if (behaviorType == null || behaviorType.trim().isEmpty()) {
            throw new CustomException("行为类型不能为空");
        }

        // 验证行为类型是否有效
        if (!isValidBehaviorType(behaviorType)) {
            throw new CustomException("无效的行为类型: " + behaviorType);
        }

        // 验证行为数据
        validateBehaviorData(dto);

        // 创建行为记录
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(currentUser.getId());
        behavior.setBehaviorType(behaviorType);
        behavior.setMovieId(dto.getMovieId());
        behavior.setSearchKeyword(dto.getSearchKeyword());
        behavior.setRating(dto.getRating());
        behavior.setDuration(dto.getDuration());
        behavior.setDeviceType(dto.getDeviceType());
        behavior.setCreateTime(LocalDateTime.now());

        // 获取IP地址
        String ipAddress = getClientIpAddress();
        behavior.setIpAddress(ipAddress);

        // 数据清洗：过滤重复浏览（同一用户、同一电影、5分钟内重复浏览）
        if (BEHAVIOR_TYPE_BROWSE.equals(behaviorType) && dto.getMovieId() != null) {
            if (isDuplicateBrowse(currentUser.getId(), dto.getMovieId())) {
                // 重复浏览，不记录或更新浏览时长
                return;
            }
        }

        // 插入数据库
        userBehaviorMapper.insertSelective(behavior);
    }

    @Override
    public UserBehaviorStatisticsVO getStatistics(Integer userId) {
        if (userId == null) {
            throw new CustomException("用户ID不能为空");
        }

        UserBehaviorStatisticsVO statistics = new UserBehaviorStatisticsVO();
        statistics.setUserId(userId);

        // 统计总行为数
        Long totalBehaviors = userBehaviorMapper.countByUserId(userId);
        statistics.setTotalBehaviors(totalBehaviors != null ? totalBehaviors : 0L);

        // 按行为类型统计
        List<Map<String, Object>> behaviorTypeCounts = userBehaviorMapper.countByBehaviorType(userId);
        Map<String, Long> typeCountMap = new HashMap<>();
        if (behaviorTypeCounts != null) {
            for (Map<String, Object> item : behaviorTypeCounts) {
                String type = (String) item.get("behaviorType");
                Long count = ((Number) item.get("count")).longValue();
                typeCountMap.put(type, count);
            }
        }
        statistics.setBehaviorTypeCounts(typeCountMap);

        // 统计各类型行为数量
        Long browseCount = userBehaviorMapper.countByUserIdAndType(userId, BEHAVIOR_TYPE_BROWSE);
        statistics.setBrowseCount(browseCount != null ? browseCount : 0L);
        
        Long rateCount = userBehaviorMapper.countByUserIdAndType(userId, BEHAVIOR_TYPE_RATE);
        statistics.setRateCount(rateCount != null ? rateCount : 0L);
        
        Long favoriteCount = userBehaviorMapper.countByUserIdAndType(userId, BEHAVIOR_TYPE_FAVORITE);
        statistics.setFavoriteCount(favoriteCount != null ? favoriteCount : 0L);
        
        Long searchCount = userBehaviorMapper.countByUserIdAndType(userId, BEHAVIOR_TYPE_SEARCH);
        statistics.setSearchCount(searchCount != null ? searchCount : 0L);
        
        Long orderCount = userBehaviorMapper.countByUserIdAndType(userId, BEHAVIOR_TYPE_ORDER);
        statistics.setOrderCount(orderCount != null ? orderCount : 0L);

        // 获取最近行为时间
        LocalDateTime lastBehaviorTime = userBehaviorMapper.getLastBehaviorTime(userId);
        statistics.setLastBehaviorTime(lastBehaviorTime);

        return statistics;
    }

    @Override
    public List<UserBehaviorVO> getUserBehaviors(Integer userId, String behaviorType, Integer limit) {
        if (userId == null) {
            throw new CustomException("用户ID不能为空");
        }

        // 设置默认限制数量
        if (limit == null || limit <= 0) {
            limit = 20; // 默认返回20条
        }

        return userBehaviorMapper.selectByUserId(userId, behaviorType, limit);
    }

    /**
     * 验证行为类型是否有效
     */
    private boolean isValidBehaviorType(String behaviorType) {
        return BEHAVIOR_TYPE_BROWSE.equals(behaviorType) ||
               BEHAVIOR_TYPE_RATE.equals(behaviorType) ||
               BEHAVIOR_TYPE_FAVORITE.equals(behaviorType) ||
               BEHAVIOR_TYPE_COMMENT.equals(behaviorType) ||
               BEHAVIOR_TYPE_SEARCH.equals(behaviorType) ||
               BEHAVIOR_TYPE_ORDER.equals(behaviorType);
    }

    /**
     * 验证行为数据
     */
    private void validateBehaviorData(UserBehaviorDTO dto) {
        String behaviorType = dto.getBehaviorType();

        // 搜索行为必须有关键词
        if (BEHAVIOR_TYPE_SEARCH.equals(behaviorType)) {
            if (dto.getSearchKeyword() == null || dto.getSearchKeyword().trim().isEmpty()) {
                throw new CustomException("搜索行为必须提供搜索关键词");
            }
        } else {
            // 非搜索行为必须有电影ID
            if (dto.getMovieId() == null) {
                throw new CustomException("该行为类型必须提供电影ID");
            }
        }

        // 评分行为必须有评分值
        if (BEHAVIOR_TYPE_RATE.equals(behaviorType)) {
            if (dto.getRating() == null) {
                throw new CustomException("评分行为必须提供评分值");
            }
            // 验证评分范围（0-10）
            if (dto.getRating().compareTo(java.math.BigDecimal.ZERO) < 0 ||
                dto.getRating().compareTo(new java.math.BigDecimal("10")) > 0) {
                throw new CustomException("评分必须在0-10之间");
            }
        }
    }

    /**
     * 检查是否为重复浏览（5分钟内同一用户、同一电影）
     */
    private boolean isDuplicateBrowse(Integer userId, Integer movieId) {
        // 查询最近5分钟内是否有相同浏览记录
        List<UserBehaviorVO> recentBehaviors = userBehaviorMapper.selectByUserId(userId, BEHAVIOR_TYPE_BROWSE, 10);
        if (recentBehaviors != null && !recentBehaviors.isEmpty()) {
            LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
            for (UserBehaviorVO behavior : recentBehaviors) {
                if (movieId.equals(behavior.getMovieId()) &&
                    behavior.getCreateTime() != null &&
                    behavior.getCreateTime().isAfter(fiveMinutesAgo)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("X-Real-IP");
                }
                if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            // 忽略异常，返回默认值
        }
        return "unknown";
    }
}
