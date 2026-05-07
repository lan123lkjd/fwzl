package com.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房源信息实体
 */
@Data
@TableName("house")
public class House implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private Long landlordId;

    private Long areaId;

    private String address;

    private BigDecimal price;

    private BigDecimal area;

    private Integer rooms;

    private Integer halls;

    private Integer bathrooms;

    private Integer floor;

    private Integer totalFloor;

    private String orientation;

    private String decoration;

    /**
     * 房屋类型: 1-整租, 2-合租
     */
    private Integer houseType;

    private String description;

    private String coverImage;

    /**
     * 状态: 0-待审核, 1-已上架, 2-已下架, 3-已出租
     */
    private Integer status;

    private Integer viewCount;

    private Integer collectCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String landlordName;

    @TableField(exist = false)
    private String landlordContact;

    @TableField(exist = false)
    private String landlordAvatar;

    @TableField(exist = false)
    private String areaName;
}
