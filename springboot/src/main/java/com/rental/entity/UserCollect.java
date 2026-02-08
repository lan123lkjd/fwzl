package com.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户收藏实体
 */
@Data
@TableName("user_collect")
public class UserCollect implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long houseId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
