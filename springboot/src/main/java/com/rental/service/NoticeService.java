package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.common.PageResult;
import com.rental.entity.Notice;

import java.util.List;

public interface NoticeService extends IService<Notice> {
    PageResult<Notice> listPage(Integer page, Integer size, Integer type);

    List<Notice> topList();
}
