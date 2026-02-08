package com.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 预约看房状态流转实体
 */
@Data
@TableName("house_order_status")
public class HouseOrderStatus implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Integer status;

    private Long operatorId;

    /**
     * 操作人类型: 1-用户, 2-房东, 3-管理员
     */
    private Integer operatorType;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
