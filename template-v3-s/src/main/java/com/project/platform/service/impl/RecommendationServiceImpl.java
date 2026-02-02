package com.project.platform.service.impl;

import com.project.platform.entity.*;
import com.project.platform.exception.CustomException;
import com.project.platform.mapper.*;
import com.project.platform.service.BehaviorService;
import com.project.platform.service.RecommendationService;
import com.project.platform.vo.UserBehaviorVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务实现类
 */
@Service
public class RecommendationServiceImpl implements RecommendationService {

    // 用户画像阈值
    private static final int NEW_USER_THRESHOLD = 5;  // 新用户：行为数据 < 5条
    private static final int LIGHT_USER_THRESHOLD = 20;  // 轻度用户：行为数据 5-20条
    
    // 推荐数量默认值
    private static final int DEFAULT_RECOMMEND_LIMIT = 20;
    
    @Resource
    private UserBehaviorMapper userBehaviorMapper;
    
    @Resource
    private MovieMapper movieMapper;
    
    @Resource
    private MovieRatingMapper movieRatingMapper;
    
    @Resource
    private UserInterestTagMapper userInterestTagMapper;
    
    @Resource
    private MovieTagRelationMapper movieTagRelationMapper;
    
    @Resource
    private MovieTypeRelationMapper movieTypeRelationMapper;
    
    @Resource
    private RecommendationResultMapper recommendationResultMapper;
    
    @Resource
    private BehaviorService behaviorService;
    

    @Override
    public List<Movie> getRecommendations(Integer userId, Integer limit) {
        if (userId == null) {
            throw new CustomException("用户ID不能为空");
        }
        if (limit == null || limit <= 0) {
            limit = DEFAULT_RECOMMEND_LIMIT;
        }
        
        // 1. 计算用户画像
        String userProfile = getUserProfile(userId);
        
        // 2. 根据用户画像选择推荐策略
        List<Movie> recommendations = new ArrayList<>();
        Map<Integer, Double> movieScores = new HashMap<>();
        
        switch (userProfile) {
            case "NEW_USER":
                // 新用户：内容推荐(70%) + 热门推荐(30%)
                recommendations = recommendForNewUser(userId, limit, movieScores);
                break;
            case "LIGHT_USER":
                // 轻度用户：协同过滤(60%) + 热门推荐(40%)
                recommendations = recommendForLightUser(userId, limit, movieScores);
                break;
            case "HEAVY_USER":
                // 重度用户：协同过滤(50%) + 实时推荐(30%) + 热门推荐(20%)
                recommendations = recommendForHeavyUser(userId, limit, movieScores);
                break;
            default:
                // 默认使用热门推荐
                recommendations = popularRecommend(limit);
        }
        
        // 3. 过滤已观看/已评分的电影
        Set<Integer> watchedMovieIds = getWatchedMovieIds(userId);
        recommendations = recommendations.stream()
                .filter(movie -> !watchedMovieIds.contains(movie.getId()))
                .collect(Collectors.toList());
        
        // 4. 限制推荐数量
        if (recommendations.size() > limit) {
            recommendations = recommendations.subList(0, limit);
        }
        
        // 5. 缓存推荐结果（可选，先实现基础版本）
        // cacheRecommendationResult(userId, recommendations, userProfile);
        
        return recommendations;
    }

    @Override
    public String getUserProfile(Integer userId) {
        Long behaviorCount = userBehaviorMapper.countByUserId(userId);
        if (behaviorCount == null) {
            behaviorCount = 0L;
        }
        
        if (behaviorCount < NEW_USER_THRESHOLD) {
            return "NEW_USER";
        } else if (behaviorCount < LIGHT_USER_THRESHOLD) {
            return "LIGHT_USER";
        } else {
            return "HEAVY_USER";
        }
    }

    @Override
    public List<Movie> collaborativeFilteringRecommend(Integer userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = DEFAULT_RECOMMEND_LIMIT;
        }
        
        // 获取用户已评分的电影
        List<MovieRating> userRatings = getUserRatings(userId);
        if (userRatings.isEmpty()) {
            // 如果没有评分数据，使用浏览行为
            return collaborativeFilteringByBrowse(userId, limit);
        }
        
        // 基于用户相似度的协同过滤
        Map<Integer, Double> movieScores = new HashMap<>();
        
        // 找到相似用户（基于共同评分的电影）
        List<Integer> similarUsers = findSimilarUsers(userId, userRatings, 20);
        
        // 基于相似用户的评分预测
        for (Integer similarUserId : similarUsers) {
            List<MovieRating> similarUserRatings = getUserRatings(similarUserId);
            double similarity = calculateUserSimilarity(userRatings, similarUserRatings);
            
            if (similarity > 0.3) {  // 相似度阈值
                for (MovieRating rating : similarUserRatings) {
                    if (!hasUserRated(userId, rating.getMovieId())) {
                        double score = similarity * rating.getRating().doubleValue();
                        movieScores.merge(rating.getMovieId(), score, Double::sum);
                    }
                }
            }
        }
        
        // 按分数排序并获取Top-N
        List<Integer> recommendedMovieIds = movieScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        return getMoviesByIds(recommendedMovieIds);
    }

    @Override
    public List<Movie> contentBasedRecommend(Integer userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = DEFAULT_RECOMMEND_LIMIT;
        }
        
        // 获取用户兴趣标签
        List<UserInterestTag> interestTags = getUserInterestTags(userId);
        
        // 获取用户历史行为电影
        List<Integer> userMovieIds = getUserBehaviorMovieIds(userId);
        
        Map<Integer, Double> movieScores = new HashMap<>();
        
        // 基于兴趣标签推荐
        if (!interestTags.isEmpty()) {
            for (UserInterestTag tag : interestTags) {
                List<Integer> movieIds = getMoviesByTag(tag.getTagName(), limit * 2);
                double weight = tag.getWeight() != null ? tag.getWeight().doubleValue() : 1.0;
                
                for (Integer movieId : movieIds) {
                    if (!userMovieIds.contains(movieId)) {
                        movieScores.merge(movieId, weight, Double::sum);
                    }
                }
            }
        }
        
        // 基于用户历史行为电影的内容相似度推荐
        if (!userMovieIds.isEmpty()) {
            for (Integer movieId : userMovieIds) {
                List<Integer> similarMovies = getSimilarMoviesByContent(movieId, limit);
                for (Integer similarMovieId : similarMovies) {
                    if (!userMovieIds.contains(similarMovieId)) {
                        movieScores.merge(similarMovieId, 0.5, Double::sum);
                    }
                }
            }
        }
        
        // 按分数排序并获取Top-N
        List<Integer> recommendedMovieIds = movieScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        return getMoviesByIds(recommendedMovieIds);
    }

    @Override
    public List<Movie> popularRecommend(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = DEFAULT_RECOMMEND_LIMIT;
        }
        
        // 基于评分、票房、观看量计算热门度
        // 热门度 = (评分 * 0.4) + (票房归一化 * 0.3) + (观看量归一化 * 0.3)
        List<Movie> allMovies = movieMapper.list();
        
        if (allMovies.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 计算最大票房和最大观看量用于归一化
        BigDecimal maxBoxOffice = allMovies.stream()
                .filter(m -> m.getBoxOffice() != null)
                .map(Movie::getBoxOffice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ONE);
        
        Integer maxViewCount = allMovies.stream()
                .filter(m -> m.getViewCount() != null)
                .map(Movie::getViewCount)
                .max(Integer::compareTo)
                .orElse(1);
        
        // 计算每个电影的热门度分数
        Map<Movie, Double> movieScores = new HashMap<>();
        for (Movie movie : allMovies) {
            if (!"active".equals(movie.getStatus())) {
                continue;
            }
            
            double score = 0.0;
            
            // 评分部分（0-10分，归一化到0-1）
            if (movie.getRating() != null) {
                score += movie.getRating().doubleValue() / 10.0 * 0.4;
            }
            
            // 票房部分（归一化）
            if (movie.getBoxOffice() != null && maxBoxOffice.compareTo(BigDecimal.ZERO) > 0) {
                score += movie.getBoxOffice().divide(maxBoxOffice, 4, RoundingMode.HALF_UP).doubleValue() * 0.3;
            }
            
            // 观看量部分（归一化）
            if (movie.getViewCount() != null && maxViewCount > 0) {
                score += (double) movie.getViewCount() / maxViewCount * 0.3;
            }
            
            movieScores.put(movie, score);
        }
        
        // 按分数排序并获取Top-N
        return movieScores.entrySet().stream()
                .sorted(Map.Entry.<Movie, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> realTimeRecommend(Integer userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = DEFAULT_RECOMMEND_LIMIT;
        }
        
        // 获取用户最近7天的行为
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        
        // 获取最近浏览/评分/收藏的电影
        List<UserBehaviorVO> recentBehaviors = userBehaviorMapper.selectByUserId(userId, null, 50);
        recentBehaviors = recentBehaviors.stream()
                .filter(b -> b.getCreateTime() != null && b.getCreateTime().isAfter(sevenDaysAgo))
                .filter(b -> Arrays.asList("browse", "rate", "favorite").contains(b.getBehaviorType()))
                .collect(Collectors.toList());
        
        if (recentBehaviors.isEmpty()) {
            return new ArrayList<>();
        }
        
        Map<Integer, Double> movieScores = new HashMap<>();
        
        // 基于最近行为的电影，推荐相似电影
        for (UserBehaviorVO behavior : recentBehaviors) {
            if (behavior.getMovieId() == null) {
                continue;
            }
            
            // 时间衰减权重
            long hoursAgo = java.time.Duration.between(behavior.getCreateTime(), LocalDateTime.now()).toHours();
            double timeWeight = Math.exp(-0.1 * hoursAgo);  // 衰减系数0.1
            
            // 行为类型权重
            double behaviorWeight = 1.0;
            if ("rate".equals(behavior.getBehaviorType())) {
                behaviorWeight = 2.0;  // 评分权重更高
            } else if ("favorite".equals(behavior.getBehaviorType())) {
                behaviorWeight = 1.5;  // 收藏权重较高
            }
            
            // 获取相似电影
            List<Integer> similarMovies = getSimilarMoviesByContent(behavior.getMovieId(), limit);
            for (Integer similarMovieId : similarMovies) {
                double score = timeWeight * behaviorWeight;
                movieScores.merge(similarMovieId, score, Double::sum);
            }
        }
        
        // 按分数排序并获取Top-N
        List<Integer> recommendedMovieIds = movieScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        return getMoviesByIds(recommendedMovieIds);
    }
    
    // ========== 私有辅助方法 ==========
    
    /**
     * 新用户推荐策略
     */
    private List<Movie> recommendForNewUser(Integer userId, Integer limit, Map<Integer, Double> movieScores) {
        // 内容推荐（70%）
        List<Movie> contentMovies = contentBasedRecommend(userId, (int) (limit * 0.7));
        for (Movie movie : contentMovies) {
            movieScores.merge(movie.getId(), 0.7, Double::sum);
        }
        
        // 热门推荐（30%）
        List<Movie> popularMovies = popularRecommend((int) (limit * 0.3));
        for (Movie movie : popularMovies) {
            movieScores.merge(movie.getId(), 0.3, Double::sum);
        }
        
        return mergeRecommendations(movieScores, limit);
    }
    
    /**
     * 轻度用户推荐策略
     */
    private List<Movie> recommendForLightUser(Integer userId, Integer limit, Map<Integer, Double> movieScores) {
        // 协同过滤（60%）
        List<Movie> cfMovies = collaborativeFilteringRecommend(userId, (int) (limit * 0.6));
        for (Movie movie : cfMovies) {
            movieScores.merge(movie.getId(), 0.6, Double::sum);
        }
        
        // 热门推荐（40%）
        List<Movie> popularMovies = popularRecommend((int) (limit * 0.4));
        for (Movie movie : popularMovies) {
            movieScores.merge(movie.getId(), 0.4, Double::sum);
        }
        
        return mergeRecommendations(movieScores, limit);
    }
    
    /**
     * 重度用户推荐策略
     */
    private List<Movie> recommendForHeavyUser(Integer userId, Integer limit, Map<Integer, Double> movieScores) {
        // 协同过滤（50%）
        List<Movie> cfMovies = collaborativeFilteringRecommend(userId, (int) (limit * 0.5));
        for (Movie movie : cfMovies) {
            movieScores.merge(movie.getId(), 0.5, Double::sum);
        }
        
        // 实时推荐（30%）
        List<Movie> realtimeMovies = realTimeRecommend(userId, (int) (limit * 0.3));
        for (Movie movie : realtimeMovies) {
            movieScores.merge(movie.getId(), 0.3, Double::sum);
        }
        
        // 热门推荐（20%）
        List<Movie> popularMovies = popularRecommend((int) (limit * 0.2));
        for (Movie movie : popularMovies) {
            movieScores.merge(movie.getId(), 0.2, Double::sum);
        }
        
        return mergeRecommendations(movieScores, limit);
    }
    
    /**
     * 合并推荐结果
     */
    private List<Movie> mergeRecommendations(Map<Integer, Double> movieScores, Integer limit) {
        List<Integer> movieIds = movieScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        return getMoviesByIds(movieIds);
    }
    
    /**
     * 基于浏览行为的协同过滤
     */
    private List<Movie> collaborativeFilteringByBrowse(Integer userId, Integer limit) {
        // 获取用户浏览过的电影
        List<Integer> userBrowsedMovies = getUserBehaviorMovieIds(userId);
        if (userBrowsedMovies.isEmpty()) {
            return popularRecommend(limit);
        }
        
        // 找到浏览过相似电影的其他用户
        Map<Integer, Double> movieScores = new HashMap<>();
        
        // 简化版：基于电影类型相似度推荐
        for (Integer movieId : userBrowsedMovies) {
            List<Integer> similarMovies = getSimilarMoviesByContent(movieId, limit);
            for (Integer similarMovieId : similarMovies) {
                if (!userBrowsedMovies.contains(similarMovieId)) {
                    movieScores.merge(similarMovieId, 1.0, Double::sum);
                }
            }
        }
        
        List<Integer> recommendedMovieIds = movieScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        return getMoviesByIds(recommendedMovieIds);
    }
    
    /**
     * 获取用户已观看的电影ID集合
     */
    private Set<Integer> getWatchedMovieIds(Integer userId) {
        List<Integer> movieIds = getUserBehaviorMovieIds(userId);
        return new HashSet<>(movieIds);
    }
    
    /**
     * 获取用户行为相关的电影ID列表
     */
    private List<Integer> getUserBehaviorMovieIds(Integer userId) {
        List<UserBehaviorVO> behaviors = userBehaviorMapper.selectByUserId(userId, null, 1000);
        return behaviors.stream()
                .filter(b -> b.getMovieId() != null)
                .map(UserBehaviorVO::getMovieId)
                .distinct()
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户评分列表
     */
    private List<MovieRating> getUserRatings(Integer userId) {
        // 优先使用movie_rating表的数据
        List<MovieRating> ratings = movieRatingMapper.selectByUserId(userId);
        if (ratings != null && !ratings.isEmpty()) {
            return ratings;
        }
        
        // 如果没有，使用行为数据中的评分
        List<UserBehaviorVO> behaviors = userBehaviorMapper.selectByUserId(userId, "rate", 1000);
        ratings = new ArrayList<>();
        for (UserBehaviorVO behavior : behaviors) {
            if (behavior.getMovieId() != null && behavior.getRating() != null) {
                MovieRating rating = new MovieRating();
                rating.setUserId(userId);
                rating.setMovieId(behavior.getMovieId());
                rating.setRating(behavior.getRating());
                ratings.add(rating);
            }
        }
        return ratings;
    }
    
    /**
     * 检查用户是否已评分某电影
     */
    private boolean hasUserRated(Integer userId, Integer movieId) {
        List<MovieRating> ratings = getUserRatings(userId);
        return ratings.stream().anyMatch(r -> r.getMovieId().equals(movieId));
    }
    
    /**
     * 找到相似用户
     */
    private List<Integer> findSimilarUsers(Integer userId, List<MovieRating> userRatings, int topK) {
        // 获取所有其他用户的评分
        List<MovieRating> allRatings = movieRatingMapper.selectAll();
        
        // 按用户分组
        Map<Integer, List<MovieRating>> userRatingsMap = allRatings.stream()
                .filter(r -> !r.getUserId().equals(userId))
                .collect(Collectors.groupingBy(MovieRating::getUserId));
        
        // 计算相似度
        Map<Integer, Double> userSimilarities = new HashMap<>();
        for (Map.Entry<Integer, List<MovieRating>> entry : userRatingsMap.entrySet()) {
            double similarity = calculateUserSimilarity(userRatings, entry.getValue());
            if (similarity > 0.3) {  // 相似度阈值
                userSimilarities.put(entry.getKey(), similarity);
            }
        }
        
        return userSimilarities.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(topK)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    
    /**
     * 计算用户相似度（余弦相似度）
     */
    private double calculateUserSimilarity(List<MovieRating> ratings1, List<MovieRating> ratings2) {
        Map<Integer, BigDecimal> movieRatings1 = new HashMap<>();
        for (MovieRating rating : ratings1) {
            movieRatings1.put(rating.getMovieId(), rating.getRating());
        }
        
        Map<Integer, BigDecimal> movieRatings2 = new HashMap<>();
        for (MovieRating rating : ratings2) {
            movieRatings2.put(rating.getMovieId(), rating.getRating());
        }
        
        // 找到共同评分的电影
        Set<Integer> commonMovies = new HashSet<>(movieRatings1.keySet());
        commonMovies.retainAll(movieRatings2.keySet());
        
        if (commonMovies.isEmpty()) {
            return 0.0;
        }
        
        // 计算余弦相似度
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        
        for (Integer movieId : commonMovies) {
            double r1 = movieRatings1.get(movieId).doubleValue();
            double r2 = movieRatings2.get(movieId).doubleValue();
            dotProduct += r1 * r2;
            norm1 += r1 * r1;
            norm2 += r2 * r2;
        }
        
        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
    
    /**
     * 获取用户兴趣标签
     */
    private List<UserInterestTag> getUserInterestTags(Integer userId) {
        List<UserInterestTag> tags = userInterestTagMapper.selectByUserId(userId);
        return tags != null ? tags : new ArrayList<>();
    }
    
    /**
     * 根据标签获取电影
     */
    private List<Integer> getMoviesByTag(String tagName, Integer limit) {
        List<Integer> movieIds = movieTagRelationMapper.listMovieIdsByTagName(tagName, limit);
        return movieIds != null ? movieIds : new ArrayList<>();
    }
    
    /**
     * 根据内容相似度获取相似电影
     */
    private List<Integer> getSimilarMoviesByContent(Integer movieId, Integer limit) {
        // 获取电影的类型
        List<Integer> typeIds = movieTypeRelationMapper.listTypeIdsByMovieId(movieId);
        
        if (typeIds == null || typeIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 根据类型找到相似电影（有共同类型的电影）
        List<Integer> similarMovieIds = movieTypeRelationMapper.listSimilarMovieIdsByTypeIds(typeIds, movieId, limit);
        return similarMovieIds != null ? similarMovieIds : new ArrayList<>();
    }
    
    /**
     * 根据ID列表获取电影
     */
    private List<Movie> getMoviesByIds(List<Integer> movieIds) {
        if (movieIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Movie> movies = new ArrayList<>();
        for (Integer id : movieIds) {
            Movie movie = movieMapper.selectById(id);
            if (movie != null && "active".equals(movie.getStatus())) {
                movies.add(movie);
            }
        }
        return movies;
    }
}
