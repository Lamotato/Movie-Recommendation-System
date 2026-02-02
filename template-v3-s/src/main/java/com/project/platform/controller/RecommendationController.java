package com.project.platform.controller;

import com.project.platform.entity.Movie;
import com.project.platform.service.RecommendationEvaluationService;
import com.project.platform.service.RecommendationService;
import com.project.platform.utils.CurrentUserThreadLocal;
import com.project.platform.vo.RecommendationEvaluationVO;
import com.project.platform.vo.ResponseVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 推荐服务控制器
 */
@RestController
@RequestMapping("/recommendation")
public class RecommendationController {

    @Resource
    private RecommendationService recommendationService;
    
    @Resource
    private RecommendationEvaluationService evaluationService;

    /**
     * 获取用户推荐电影列表（混合推荐）
     * @param limit 推荐数量限制（可选，默认20）
     * @return 推荐电影列表
     */
    @GetMapping("/list")
    public ResponseVO<List<Movie>> getRecommendations(@RequestParam(required = false, defaultValue = "20") Integer limit) {
        // 获取当前登录用户
        com.project.platform.dto.CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            return ResponseVO.fail(401, "请先登录");
        }
        
        List<Movie> recommendations = recommendationService.getRecommendations(currentUser.getId(), limit);
        return ResponseVO.ok(recommendations);
    }

    /**
     * 获取用户画像类型
     * @return 用户画像类型
     */
    @GetMapping("/profile")
    public ResponseVO<String> getUserProfile() {
        com.project.platform.dto.CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            return ResponseVO.fail(401, "请先登录");
        }
        
        String profile = recommendationService.getUserProfile(currentUser.getId());
        return ResponseVO.ok(profile);
    }

    /**
     * 协同过滤推荐
     * @param limit 推荐数量（可选，默认20）
     * @return 推荐电影列表
     */
    @GetMapping("/collaborative")
    public ResponseVO<List<Movie>> collaborativeFiltering(@RequestParam(required = false, defaultValue = "20") Integer limit) {
        com.project.platform.dto.CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            return ResponseVO.fail(401, "请先登录");
        }
        
        List<Movie> recommendations = recommendationService.collaborativeFilteringRecommend(currentUser.getId(), limit);
        return ResponseVO.ok(recommendations);
    }

    /**
     * 内容推荐
     * @param limit 推荐数量（可选，默认20）
     * @return 推荐电影列表
     */
    @GetMapping("/content")
    public ResponseVO<List<Movie>> contentBased(@RequestParam(required = false, defaultValue = "20") Integer limit) {
        com.project.platform.dto.CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            return ResponseVO.fail(401, "请先登录");
        }
        
        List<Movie> recommendations = recommendationService.contentBasedRecommend(currentUser.getId(), limit);
        return ResponseVO.ok(recommendations);
    }

    /**
     * 热门推荐
     * @param limit 推荐数量（可选，默认20）
     * @return 推荐电影列表
     */
    @GetMapping("/popular")
    public ResponseVO<List<Movie>> popular(@RequestParam(required = false, defaultValue = "20") Integer limit) {
        List<Movie> recommendations = recommendationService.popularRecommend(limit);
        return ResponseVO.ok(recommendations);
    }

    /**
     * 实时推荐
     * @param limit 推荐数量（可选，默认20）
     * @return 推荐电影列表
     */
    @GetMapping("/realtime")
    public ResponseVO<List<Movie>> realTime(@RequestParam(required = false, defaultValue = "20") Integer limit) {
        com.project.platform.dto.CurrentUserDTO currentUser = CurrentUserThreadLocal.getCurrentUser();
        if (currentUser == null) {
            return ResponseVO.fail(401, "请先登录");
        }
        
        List<Movie> recommendations = recommendationService.realTimeRecommend(currentUser.getId(), limit);
        return ResponseVO.ok(recommendations);
    }

    /**
     * 计算并保存推荐效果评估（管理员接口）
     * @param evaluationDate 评估日期（可选，默认今天）
     * @param recommendationType 推荐类型（可选，null表示所有类型）
     * @param topN Top-N推荐数量（可选，默认10）
     * @return 评估结果
     */
    @PostMapping("/admin/evaluate")
    public ResponseVO<RecommendationEvaluationVO> evaluate(
            @RequestParam(required = false) LocalDate evaluationDate,
            @RequestParam(required = false) String recommendationType,
            @RequestParam(required = false, defaultValue = "10") Integer topN) {
        RecommendationEvaluationVO result = evaluationService.evaluateAndSave(evaluationDate, recommendationType, topN);
        return ResponseVO.ok(result);
    }

    /**
     * 获取评估结果列表（管理员接口）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param recommendationType 推荐类型（可选）
     * @return 评估结果列表
     */
    @GetMapping("/admin/evaluation/list")
    public ResponseVO<List<RecommendationEvaluationVO>> getEvaluationList(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String recommendationType) {
        List<RecommendationEvaluationVO> list = evaluationService.getEvaluationList(startDate, endDate, recommendationType);
        return ResponseVO.ok(list);
    }

    /**
     * 获取可视化报表数据（管理员接口）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param recommendationType 推荐类型（可选）
     * @return 报表数据
     */
    @GetMapping("/admin/evaluation/charts")
    public ResponseVO<Map<String, Object>> getVisualizationData(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) String recommendationType) {
        Map<String, Object> data = evaluationService.getVisualizationData(startDate, endDate, recommendationType);
        return ResponseVO.ok(data);
    }

    /**
     * 计算准确率和召回率（管理员接口）
     * @param evaluationDate 评估日期（可选，默认今天）
     * @param recommendationType 推荐类型（可选）
     * @param topN Top-N推荐数量（可选，默认10）
     * @return 准确率和召回率
     */
    @GetMapping("/admin/evaluation/precision-recall")
    public ResponseVO<Map<String, BigDecimal>> getPrecisionAndRecall(
            @RequestParam(required = false) LocalDate evaluationDate,
            @RequestParam(required = false) String recommendationType,
            @RequestParam(required = false, defaultValue = "10") Integer topN) {
        if (evaluationDate == null) {
            evaluationDate = LocalDate.now();
        }
        Map<String, BigDecimal> result = evaluationService.calculatePrecisionAndRecall(
            evaluationDate, recommendationType, topN);
        return ResponseVO.ok(result);
    }

    /**
     * 计算推荐多样性（管理员接口）
     * @param evaluationDate 评估日期（可选，默认今天）
     * @param recommendationType 推荐类型（可选）
     * @return 多样性指标
     */
    @GetMapping("/admin/evaluation/diversity")
    public ResponseVO<BigDecimal> getDiversity(
            @RequestParam(required = false) LocalDate evaluationDate,
            @RequestParam(required = false) String recommendationType) {
        if (evaluationDate == null) {
            evaluationDate = LocalDate.now();
        }
        BigDecimal diversity = evaluationService.calculateDiversity(evaluationDate, recommendationType);
        return ResponseVO.ok(diversity);
    }

    /**
     * 计算用户满意度（管理员接口）
     * @param evaluationDate 评估日期（可选，默认今天）
     * @param recommendationType 推荐类型（可选）
     * @return 用户满意度
     */
    @GetMapping("/admin/evaluation/satisfaction")
    public ResponseVO<BigDecimal> getSatisfaction(
            @RequestParam(required = false) LocalDate evaluationDate,
            @RequestParam(required = false) String recommendationType) {
        if (evaluationDate == null) {
            evaluationDate = LocalDate.now();
        }
        BigDecimal satisfaction = evaluationService.calculateUserSatisfaction(evaluationDate, recommendationType);
        return ResponseVO.ok(satisfaction);
    }

    /**
     * 计算点击率（管理员接口）
     * @param evaluationDate 评估日期（可选，默认今天）
     * @param recommendationType 推荐类型（可选）
     * @return 点击率
     */
    @GetMapping("/admin/evaluation/ctr")
    public ResponseVO<BigDecimal> getClickThroughRate(
            @RequestParam(required = false) LocalDate evaluationDate,
            @RequestParam(required = false) String recommendationType) {
        if (evaluationDate == null) {
            evaluationDate = LocalDate.now();
        }
        BigDecimal ctr = evaluationService.calculateClickThroughRate(evaluationDate, recommendationType);
        return ResponseVO.ok(ctr);
    }
}
