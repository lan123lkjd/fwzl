package com.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 房东信息实体
 */
@Data
@TableName("landlord")
public class Landlord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String realName;

    private String idCard;

    private String contact;

    private String idCardFront;

    private String idCardBack;

    /**
     * 认证状态: 0-待审核, 1-已认证, 2-认证失败
     */
    private Integer verifyStatus;

    private String verifyRemark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
