package com.rental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户浏览历史实体
 */
@Data
@TableName("user_browse_history")
public class UserBrowseHistory implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long houseId;

    private LocalDateTime browseTime;
}
