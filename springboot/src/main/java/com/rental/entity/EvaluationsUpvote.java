package com.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评论点赞实体
 */
@Data
@TableName("evaluations_upvote")
public class EvaluationsUpvote implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long evaluationId;

    private Long userId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
