package com.project.platform.service.impl;

import com.alibaba.fastjson2.JSON;
import com.project.platform.entity.*;
import com.project.platform.mapper.*;
import com.project.platform.service.RecommendationEvaluationService;
import com.project.platform.vo.RecommendationEvaluationVO;
import com.project.platform.vo.UserBehaviorVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐效果评估服务实现类
 */
@Service
public class RecommendationEvaluationServiceImpl implements RecommendationEvaluationService {

    private static final int DEFAULT_TOP_N = 10;
    
    @Resource
    private RecommendationEvaluationMapper recommendationEvaluationMapper;
    
    @Resource
    private RecommendationResultMapper recommendationResultMapper;
    
    @Resource
    private UserBehaviorMapper userBehaviorMapper;
    
    @Resource
    private MovieRatingMapper movieRatingMapper;
    
    @Resource
    private MovieTypeRelationMapper movieTypeRelationMapper;
    
    @Resource
    private MovieMapper movieMapper;

    @Override
    public RecommendationEvaluationVO evaluateAndSave(LocalDate evaluationDate, String recommendationType, Integer topN) {
        if (evaluationDate == null) {
            evaluationDate = LocalDate.now();
        }
        if (topN == null || topN <= 0) {
            topN = DEFAULT_TOP_N;
        }
        
        RecommendationEvaluationVO vo = new RecommendationEvaluationVO();
        vo.setEvaluationDate(evaluationDate);
        vo.setRecommendationType(recommendationType);
        
        // 计算准确率和召回率
        Map<String, BigDecimal> precisionRecall = calculatePrecisionAndRecall(evaluationDate, recommendationType, topN);
        vo.setPrecisionAtN(precisionRecall.get("precision"));
        vo.setRecallAtN(precisionRecall.get("recall"));
        
        // 计算多样性
        BigDecimal diversity = calculateDiversity(evaluationDate, recommendationType);
        vo.setDiversity(diversity);
        
        // 计算用户满意度
        BigDecimal satisfaction = calculateUserSatisfaction(evaluationDate, recommendationType);
        vo.setUserSatisfaction(satisfaction);
        
        // 计算点击率
        BigDecimal ctr = calculateClickThroughRate(evaluationDate, recommendationType);
        vo.setClickThroughRate(ctr);
        
        // 保存评估结果
        RecommendationEvaluation evaluation = new RecommendationEvaluation();
        evaluation.setEvaluationDate(evaluationDate);
        evaluation.setRecommendationType(recommendationType != null ? recommendationType : "all");
        evaluation.setPrecisionAtN(vo.getPrecisionAtN());
        evaluation.setRecallAtN(vo.getRecallAtN());
        evaluation.setDiversity(vo.getDiversity());
        evaluation.setUserSatisfaction(vo.getUserSatisfaction());
        evaluation.setClickThroughRate(vo.getClickThroughRate());
        evaluation.setCreateTime(LocalDateTime.now());
        
        recommendationEvaluationMapper.insertSelective(evaluation);
        
        return vo;
    }

    @Override
    public Map<String, BigDecimal> calculatePrecisionAndRecall(LocalDate evaluationDate, String recommendationType, Integer topN) {
        if (topN == null || topN <= 0) {
            topN = DEFAULT_TOP_N;
        }
        
        // 获取评估日期范围内的推荐结果
        // 这里简化处理，实际应该根据evaluationDate查询对应时间段的推荐结果
        List<RecommendationResult> recommendationResults = getAllRecommendationResults(recommendationType);
        
        int totalRecommendations = 0;
        int totalRelevantItems = 0; // 用户实际交互的所有电影
        
        // 获取评估日期范围内的用户行为（作为真实标签）
        LocalDateTime startDateTime = evaluationDate.atStartOfDay();
        LocalDateTime endDateTime = evaluationDate.plusDays(1).atStartOfDay();
        
        // 统计每个用户的推荐结果和实际行为
        Map<Integer, Set<Integer>> userRecommendedMovies = new HashMap<>();
        Map<Integer, Set<Integer>> userInteractedMovies = new HashMap<>();
        
        for (RecommendationResult result : recommendationResults) {
            Integer userId = result.getUserId();
            if (userId == null) continue;
            
            // 解析推荐电影ID列表
            List<Integer> movieIds = parseMovieIds(result.getMovieIds());
            if (movieIds.isEmpty()) continue;
            
            // 只取Top-N
            movieIds = movieIds.stream().limit(topN).collect(Collectors.toList());
            
            userRecommendedMovies.putIfAbsent(userId, new HashSet<>());
            userRecommendedMovies.get(userId).addAll(movieIds);
            totalRecommendations += movieIds.size();
            
            // 获取用户在该日期范围内的实际交互（浏览、评分、收藏、订单）
            Set<Integer> interactedMovies = getUserInteractedMovies(userId, startDateTime, endDateTime);
            userInteractedMovies.put(userId, interactedMovies);
            totalRelevantItems += interactedMovies.size();
        }
        
        // 计算准确率和召回率
        int totalIntersection = 0;
        for (Integer userId : userRecommendedMovies.keySet()) {
            Set<Integer> recommended = userRecommendedMovies.get(userId);
            Set<Integer> interacted = userInteractedMovies.getOrDefault(userId, new HashSet<>());
            
            // 计算交集（推荐的电影中用户实际交互的）
            Set<Integer> intersection = new HashSet<>(recommended);
            intersection.retainAll(interacted);
            totalIntersection += intersection.size();
        }
        
        BigDecimal precision = totalRecommendations > 0 
            ? BigDecimal.valueOf(totalIntersection).divide(BigDecimal.valueOf(totalRecommendations), 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        BigDecimal recall = totalRelevantItems > 0
            ? BigDecimal.valueOf(totalIntersection).divide(BigDecimal.valueOf(totalRelevantItems), 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("precision", precision);
        result.put("recall", recall);
        return result;
    }

    @Override
    public BigDecimal calculateDiversity(LocalDate evaluationDate, String recommendationType) {
        List<RecommendationResult> recommendationResults = getAllRecommendationResults(recommendationType);
        
        if (recommendationResults.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // 收集所有推荐的电影ID
        Set<Integer> allRecommendedMovies = new HashSet<>();
        Map<Integer, Set<Integer>> movieTypeMap = new HashMap<>();
        
        for (RecommendationResult result : recommendationResults) {
            List<Integer> movieIds = parseMovieIds(result.getMovieIds());
            allRecommendedMovies.addAll(movieIds);
            
            // 获取每个电影的类型
            for (Integer movieId : movieIds) {
                List<Integer> typeIds = movieTypeRelationMapper.listTypeIdsByMovieId(movieId);
                movieTypeMap.put(movieId, new HashSet<>(typeIds));
            }
        }
        
        if (allRecommendedMovies.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // 计算多样性：基于电影类型的分布
        // 使用信息熵或类型覆盖率来衡量多样性
        Map<Integer, Integer> typeCount = new HashMap<>();
        int totalTypes = 0;
        
        for (Set<Integer> types : movieTypeMap.values()) {
            for (Integer typeId : types) {
                typeCount.put(typeId, typeCount.getOrDefault(typeId, 0) + 1);
                totalTypes++;
            }
        }
        
        if (totalTypes == 0) {
            return BigDecimal.ZERO;
        }
        
        // 计算类型分布的熵（多样性指标）
        double entropy = 0.0;
        for (Integer count : typeCount.values()) {
            double probability = (double) count / totalTypes;
            if (probability > 0) {
                entropy -= probability * Math.log(probability) / Math.log(2);
            }
        }
        
        // 归一化到0-1之间（假设最大熵为log2(类型总数)）
        int uniqueTypes = typeCount.size();
        double maxEntropy = uniqueTypes > 1 ? Math.log(uniqueTypes) / Math.log(2) : 1.0;
        double normalizedDiversity = maxEntropy > 0 ? entropy / maxEntropy : 0.0;
        
        return BigDecimal.valueOf(normalizedDiversity).setScale(4, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateUserSatisfaction(LocalDate evaluationDate, String recommendationType) {
        // 用户满意度基于推荐电影的评分
        // 获取评估日期范围内的推荐结果
        List<RecommendationResult> recommendationResults = getAllRecommendationResults(recommendationType);
        
        if (recommendationResults.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        LocalDateTime startDateTime = evaluationDate.atStartOfDay();
        LocalDateTime endDateTime = evaluationDate.plusDays(1).atStartOfDay();
        
        int totalRatings = 0;
        BigDecimal totalRatingScore = BigDecimal.ZERO;
        
        for (RecommendationResult result : recommendationResults) {
            Integer userId = result.getUserId();
            if (userId == null) continue;
            
            List<Integer> movieIds = parseMovieIds(result.getMovieIds());
            
            // 获取用户对这些推荐电影的评分
            for (Integer movieId : movieIds) {
                MovieRating rating = movieRatingMapper.selectByUserIdAndMovieId(userId, movieId);
                if (rating != null && rating.getRating() != null) {
                    // 检查评分时间是否在评估日期范围内
                    if (rating.getCreateTime() != null && 
                        rating.getCreateTime().isAfter(startDateTime) && 
                        rating.getCreateTime().isBefore(endDateTime)) {
                        totalRatings++;
                        totalRatingScore = totalRatingScore.add(rating.getRating());
                    }
                }
            }
        }
        
        if (totalRatings == 0) {
            return BigDecimal.ZERO;
        }
        
        // 满意度 = 平均评分 / 10（假设评分范围是0-10）
        BigDecimal avgRating = totalRatingScore.divide(BigDecimal.valueOf(totalRatings), 4, RoundingMode.HALF_UP);
        BigDecimal satisfaction = avgRating.divide(BigDecimal.valueOf(10), 4, RoundingMode.HALF_UP);
        
        return satisfaction.min(BigDecimal.ONE); // 确保不超过1
    }

    @Override
    public BigDecimal calculateClickThroughRate(LocalDate evaluationDate, String recommendationType) {
        // 点击率 = 用户点击推荐电影的数量 / 推荐总数
        List<RecommendationResult> recommendationResults = getAllRecommendationResults(recommendationType);
        
        if (recommendationResults.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        LocalDateTime startDateTime = evaluationDate.atStartOfDay();
        LocalDateTime endDateTime = evaluationDate.plusDays(1).atStartOfDay();
        
        int totalRecommendations = 0;
        int totalClicks = 0;
        
        for (RecommendationResult result : recommendationResults) {
            Integer userId = result.getUserId();
            if (userId == null) continue;
            
            List<Integer> movieIds = parseMovieIds(result.getMovieIds());
            totalRecommendations += movieIds.size();
            
            // 统计用户对这些推荐电影的浏览行为（视为点击）
            for (Integer movieId : movieIds) {
                List<UserBehaviorVO> behaviors = userBehaviorMapper.selectByUserId(userId, "browse", null);
                for (UserBehaviorVO behavior : behaviors) {
                    if (behavior.getMovieId() != null && behavior.getMovieId().equals(movieId)) {
                        if (behavior.getCreateTime() != null &&
                            behavior.getCreateTime().isAfter(startDateTime) &&
                            behavior.getCreateTime().isBefore(endDateTime)) {
                            totalClicks++;
                            break; // 每个电影只统计一次
                        }
                    }
                }
            }
        }
        
        if (totalRecommendations == 0) {
            return BigDecimal.ZERO;
        }
        
        return BigDecimal.valueOf(totalClicks)
                .divide(BigDecimal.valueOf(totalRecommendations), 4, RoundingMode.HALF_UP);
    }

    @Override
    public List<RecommendationEvaluationVO> getEvaluationList(LocalDate startDate, LocalDate endDate, String recommendationType) {
        List<RecommendationEvaluation> evaluations = recommendationEvaluationMapper.selectByDateRange(
            startDate, endDate, recommendationType);
        
        return evaluations.stream().map(eval -> {
            RecommendationEvaluationVO vo = new RecommendationEvaluationVO();
            vo.setEvaluationDate(eval.getEvaluationDate());
            vo.setRecommendationType(eval.getRecommendationType());
            vo.setPrecisionAtN(eval.getPrecisionAtN());
            vo.setRecallAtN(eval.getRecallAtN());
            vo.setDiversity(eval.getDiversity());
            vo.setUserSatisfaction(eval.getUserSatisfaction());
            vo.setClickThroughRate(eval.getClickThroughRate());
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getVisualizationData(LocalDate startDate, LocalDate endDate, String recommendationType) {
        Map<String, Object> chartData = new HashMap<>();
        
        // 从数据库获取历史评估数据
        List<RecommendationEvaluation> evaluations = recommendationEvaluationMapper.selectByDateRange(
            startDate, endDate, recommendationType);
        
        // 转换为时间序列数据
        List<RecommendationEvaluationVO.TimeSeriesData> timeSeries = evaluations.stream()
            .map(eval -> {
                RecommendationEvaluationVO.TimeSeriesData data = new RecommendationEvaluationVO.TimeSeriesData();
                data.setDate(eval.getEvaluationDate());
                data.setPrecision(eval.getPrecisionAtN());
                data.setRecall(eval.getRecallAtN());
                data.setDiversity(eval.getDiversity());
                data.setSatisfaction(eval.getUserSatisfaction());
                return data;
            })
            .collect(Collectors.toList());
        
        chartData.put("timeSeries", timeSeries);
        
        // 计算平均值
        if (!evaluations.isEmpty()) {
            BigDecimal avgPrecision = evaluations.stream()
                .map(RecommendationEvaluation::getPrecisionAtN)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(evaluations.size()), 4, RoundingMode.HALF_UP);
            
            BigDecimal avgRecall = evaluations.stream()
                .map(RecommendationEvaluation::getRecallAtN)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(evaluations.size()), 4, RoundingMode.HALF_UP);
            
            BigDecimal avgDiversity = evaluations.stream()
                .map(RecommendationEvaluation::getDiversity)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(evaluations.size()), 4, RoundingMode.HALF_UP);
            
            BigDecimal avgSatisfaction = evaluations.stream()
                .map(RecommendationEvaluation::getUserSatisfaction)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(evaluations.size()), 4, RoundingMode.HALF_UP);
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("avgPrecision", avgPrecision);
            summary.put("avgRecall", avgRecall);
            summary.put("avgDiversity", avgDiversity);
            summary.put("avgSatisfaction", avgSatisfaction);
            chartData.put("summary", summary);
        }
        
        return chartData;
    }
    
    /**
     * 获取所有推荐结果
     */
    private List<RecommendationResult> getAllRecommendationResults(String recommendationType) {
        if (recommendationType != null && !recommendationType.isEmpty()) {
            return recommendationResultMapper.selectByType(recommendationType);
        } else {
            return recommendationResultMapper.selectAll();
        }
    }
    
    /**
     * 解析JSON格式的电影ID列表
     */
    private List<Integer> parseMovieIds(String movieIdsJson) {
        if (movieIdsJson == null || movieIdsJson.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<Integer> ids = JSON.parseArray(movieIdsJson, Integer.class);
            return ids != null ? ids : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * 获取用户在指定时间范围内的交互电影
     */
    private Set<Integer> getUserInteractedMovies(Integer userId, LocalDateTime start, LocalDateTime end) {
        Set<Integer> movieIds = new HashSet<>();
        
        // 获取用户行为（浏览、评分、收藏、订单）
        List<UserBehaviorVO> behaviors = userBehaviorMapper.selectByUserId(userId, null, null);
        for (UserBehaviorVO behavior : behaviors) {
            if (behavior.getCreateTime() != null &&
                behavior.getCreateTime().isAfter(start) &&
                behavior.getCreateTime().isBefore(end)) {
                if (behavior.getMovieId() != null) {
                    movieIds.add(behavior.getMovieId());
                }
            }
        }
        
        return movieIds;
    }
}
