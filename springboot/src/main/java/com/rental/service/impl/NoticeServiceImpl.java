package com.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.common.PageResult;
import com.rental.entity.Notice;
import com.rental.mapper.NoticeMapper;
import com.rental.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public PageResult<Notice> listPage(Integer page, Integer size, Integer type) {
        Page<Notice> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getStatus, 1);
        if (type != null) {
            wrapper.eq(Notice::getType, type);
        }
        wrapper.orderByDesc(Notice::getTop).orderByDesc(Notice::getCreateTime);
        Page<Notice> result = page(pageParam, wrapper);
        return new PageResult<>(result.getTotal(), result.getPages(), result.getCurrent(), result.getSize(),
                result.getRecords());
    }

    @Override
    public List<Notice> topList() {
        return list(new LambdaQueryWrapper<Notice>()
                .eq(Notice::getStatus, 1)
                .eq(Notice::getTop, 1)
                .orderByDesc(Notice::getCreateTime)
                .last("LIMIT 5"));
    }
}
