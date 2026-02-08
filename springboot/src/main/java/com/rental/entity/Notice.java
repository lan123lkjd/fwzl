package com.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告实体
 */
@Data
@TableName("notice")
public class Notice implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    /**
     * 类型: 1-系统公告, 2-活动公告, 3-政策公告
     */
    private Integer type;

    /**
     * 状态: 0-草稿, 1-已发布
     */
    private Integer status;

    private Integer top;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
