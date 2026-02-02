package com.project.platform.entity;


import java.time.LocalDateTime;
import lombok.Data;

/**
 * 电影类型关联表
 */
@Data
public class MovieTypeRelation {
    /**
     * ID
     */
    private Integer id;

    /**
     * 电影ID
     */
    private Integer movieId;

    /**
     * 类型ID
     */
    private Integer typeId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}