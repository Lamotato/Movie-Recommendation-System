package com.project.platform.entity;


import java.time.LocalDateTime;
import lombok.Data;

/**
 * 公告表
 */
@Data
public class Announcement {
    /**
     * 公告ID
     */
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 类型：general/important/activity
     */
    private String type;

    /**
     * 状态：active/inactive
     */
    private String status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}