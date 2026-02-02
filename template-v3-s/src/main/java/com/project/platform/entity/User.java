package com.project.platform.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * 用户表
 */
@Data
public class User {
    /**
     * 用户ID
     */
    private Integer id;

    /**
     * 用户名（邮箱）
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
     * 性别：male/female/unknown
     */
    private String gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 状态：active/inactive/banned
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
