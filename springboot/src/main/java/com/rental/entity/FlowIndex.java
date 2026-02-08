package com.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 流量指标信息实体
 */
@Data
@TableName("flow_index")
public class FlowIndex implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private LocalDate date;

    private Integer pv;

    private Integer uv;

    private Integer newUsers;

    private Integer houseViews;

    private Integer orderCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
