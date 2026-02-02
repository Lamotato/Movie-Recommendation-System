package com.project.platform.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员表
 */
@Data
public class Admin {
    /**
     * 管理员ID
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密）
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 手机号
     */
    private String tel;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 角色：admin/super_admin
     */
    private String role;

    /**
     * 状态：active/inactive
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