package com.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 预约看房信息实体
 */
@Data
@TableName("house_order_info")
public class HouseOrderInfo implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long userId;

    private Long houseId;

    private Long landlordId;

    private LocalDateTime orderTime;

    private String contactName;

    private String contactPhone;

    private String remark;

    /**
     * 状态: 0-待确认, 1-已确认, 2-已完成, 3-已取消, 4-已拒绝
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
