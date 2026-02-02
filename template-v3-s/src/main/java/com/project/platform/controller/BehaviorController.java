package com.project.platform.controller;

import com.project.platform.dto.UserBehaviorDTO;
import com.project.platform.service.BehaviorService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.ResponseVO;
import com.project.platform.vo.UserBehaviorStatisticsVO;
import com.project.platform.vo.UserBehaviorVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户行为采集控制器
 */
@RestController
@RequestMapping("/behavior")
public class BehaviorController {

    @Resource
    private BehaviorService behaviorService;

    /**
     * 记录用户行为
     * @param dto 行为数据
     * @return 响应结果
     */
    @PostMapping("record")
    public ResponseVO<Void> recordBehavior(@RequestBody UserBehaviorDTO dto) {
        behaviorService.recordBehavior(dto);
        return ResponseVO.ok();
    }

    /**
     * 获取当前用户的行为统计
     * @return 统计数据
     */
    @GetMapping("statistics")
    public ResponseVO<UserBehaviorStatisticsVO> getCurrentUserStatistics() {
        com.project.platform.dto.CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            return ResponseVO.fail(401, "请先登录");
        }
        UserBehaviorStatisticsVO statistics = behaviorService.getStatistics(currentUser.getId());
        return ResponseVO.ok(statistics);
    }

    /**
     * 获取指定用户的行为统计（管理员功能）
     * @param userId 用户ID
     * @return 统计数据
     */
    @GetMapping("statistics/{userId}")
    public ResponseVO<UserBehaviorStatisticsVO> getStatistics(@PathVariable("userId") Integer userId) {
        UserBehaviorStatisticsVO statistics = behaviorService.getStatistics(userId);
        return ResponseVO.ok(statistics);
    }

    /**
     * 获取当前用户的行为列表
     * @param behaviorType 行为类型（可选）
     * @param limit 限制数量（可选，默认20）
     * @return 行为列表
     */
    @GetMapping("list")
    public ResponseVO<List<UserBehaviorVO>> getUserBehaviors(
            @RequestParam(required = false) String behaviorType,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        com.project.platform.dto.CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            return ResponseVO.fail(401, "请先登录");
        }
        List<UserBehaviorVO> behaviors = behaviorService.getUserBehaviors(currentUser.getId(), behaviorType, limit);
        return ResponseVO.ok(behaviors);
    }

    /**
     * 获取指定用户的行为列表（管理员功能）
     * @param userId 用户ID
     * @param behaviorType 行为类型（可选）
     * @param limit 限制数量（可选，默认20）
     * @return 行为列表
     */
    @GetMapping("list/{userId}")
    public ResponseVO<List<UserBehaviorVO>> getUserBehaviorsByUserId(
            @PathVariable("userId") Integer userId,
            @RequestParam(required = false) String behaviorType,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        List<UserBehaviorVO> behaviors = behaviorService.getUserBehaviors(userId, behaviorType, limit);
        return ResponseVO.ok(behaviors);
    }
}
