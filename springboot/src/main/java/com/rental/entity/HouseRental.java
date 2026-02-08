package com.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 房屋租赁实体
 */
@Data
@TableName("house_rental")
public class HouseRental implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String rentalNo;

    private Long userId;

    private Long houseId;

    private Long landlordId;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal monthlyRent;

    private BigDecimal deposit;

    private BigDecimal totalAmount;

    private String contactName;

    private String contactPhone;

    private String remark;

    /**
     * 状态: 0-待确认, 1-租赁中, 2-已完成, 3-已取消, 4-已拒绝
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    // 非数据库字段，用于关联查询
    @TableField(exist = false)
    private String houseTitle;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String landlordName;
}
