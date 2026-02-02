package com.project.platform.entity;


import java.time.LocalDateTime;
import lombok.Data;

/**
 * 电影标签关联表
 */

@Data
public class MovieTagRelation {
    /**
     * ID
     */
    private Integer id;

    /**
     * 电影ID
     */
    private Integer movieId;

    /**
     * 标签ID
     */
    private Integer tagId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}