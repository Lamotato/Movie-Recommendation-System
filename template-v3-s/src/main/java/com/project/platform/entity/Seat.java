package com.project.platform.entity;


import java.time.LocalDateTime;
import lombok.Data;

/**
 * 座位表
 */
@Data
public class Seat {
    /**
     * 座位ID
     */
    private Integer id;

    /**
     * 房间ID
     */
    private Integer roomId;

    /**
     * 行号
     */
    private Integer rowNum;

    /**
     * 列号
     */
    private Integer colNum;

    /**
     * 座位类型：normal/vip/love_seat
     */
    private String seatType;

    /**
     * 状态：active/inactive
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}