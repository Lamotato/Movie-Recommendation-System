package com.project.platform.entity;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * 影院表
 */
@Data
public class Cinema {
    /**
     * 影院ID
     */
    private Integer id;

    /**
     * 影院名称
     */
    private String name;

    /**
     * 地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String tel;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 影院描述
     */
    private String description;

    /**
     * 状态：pending/active/inactive（pending需要管理员认证）
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}