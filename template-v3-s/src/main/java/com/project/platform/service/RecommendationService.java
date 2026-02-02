package com.project.platform.service;

import com.project.platform.entity.Movie;

import java.util.List;

/**
 * 推荐服务接口
 */
public interface RecommendationService {
    
    /**
     * 获取用户推荐电影列表（混合推荐）
     * @param userId 用户ID
     * @param limit 推荐数量限制
     * @return 推荐电影列表
     */
    List<Movie> getRecommendations(Integer userId, Integer limit);
    
    /**
     * 获取用户画像类型
     * @param userId 用户ID
     * @return 用户画像类型：NEW_USER(新用户), LIGHT_USER(轻度用户), HEAVY_USER(重度用户)
     */
    String getUserProfile(Integer userId);
    
    /**
     * 协同过滤推荐
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐电影列表
     */
    List<Movie> collaborativeFilteringRecommend(Integer userId, Integer limit);
    
    /**
     * 内容推荐
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐电影列表
     */
    List<Movie> contentBasedRecommend(Integer userId, Integer limit);
    
    /**
     * 热门推荐
     * @param limit 推荐数量
     * @return 推荐电影列表
     */
    List<Movie> popularRecommend(Integer limit);
    
    /**
     * 实时推荐
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐电影列表
     */
    List<Movie> realTimeRecommend(Integer userId, Integer limit);
}
