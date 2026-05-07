package com.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论实体
 */
@Data
@TableName("evaluations")
public class Evaluations implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long houseId;

    private Long userId;

    private String content;

    private Integer upvoteCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String houseTitle;
}
