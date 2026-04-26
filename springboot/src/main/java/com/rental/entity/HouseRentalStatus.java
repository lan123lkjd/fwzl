package com.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("house_rental_status")
public class HouseRentalStatus implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long rentalId;

    private Integer status;

    private Long operatorId;

    private Integer operatorType;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
