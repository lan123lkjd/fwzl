package com.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 小区信息实体
 */
@Data
@TableName("community")
public class Community implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long areaId;

    private String address;

    private String description;

    private Integer buildYear;

    private String propertyCompany;

    private BigDecimal propertyFee;

    private String coverImage;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
