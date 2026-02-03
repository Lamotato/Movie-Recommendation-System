package com.project.platform.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 电影新增/修改 DTO（包含多类型、多标签）
 */
@Data
public class MovieDTO {
    private Integer id;
    private String name;
    private String englishName;
    private String director;
    private String cast;
    private String description;
    private String posterUrl;
    private String trailerUrl;
    private Integer duration;
    private LocalDate releaseDate;
    private String country;
    private String language;
    private BigDecimal boxOffice;
    private String status;

    /**
     * 关联的类型ID列表（1~3个）
     */
    private List<Integer> typeIds;

    /**
     * 关联的标签ID列表（可选）
     */
    private List<Integer> tagIds;
}

