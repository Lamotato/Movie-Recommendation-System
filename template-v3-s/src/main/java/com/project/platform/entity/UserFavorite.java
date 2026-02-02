package com.project.platform.entity;


import java.time.LocalDateTime;
import lombok.Data;

/**
 * 用户收藏表
 */
@Data
public class UserFavorite {
    /**
     * ID
     */
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 电影ID
     */
    private Integer movieId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}