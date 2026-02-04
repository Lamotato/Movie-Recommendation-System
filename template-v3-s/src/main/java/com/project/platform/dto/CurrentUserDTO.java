package com.project.platform.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CurrentUserDTO {
    private Integer id;
    private String type;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String tel;
    private String email;
    private String role;
    private String gender;
    private LocalDate birthday;

}
