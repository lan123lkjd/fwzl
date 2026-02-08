package com.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.common.PageResult;
import com.rental.entity.HouseNews;
import com.rental.mapper.HouseNewsMapper;
import com.rental.service.HouseNewsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class HouseNewsServiceImpl extends ServiceImpl<HouseNewsMapper, HouseNews> implements HouseNewsService {

    @Override
    public PageResult<HouseNews> listPage(Integer page, Integer size, String category, String keyword) {
        Page<HouseNews> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HouseNews> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HouseNews::getStatus, 1);
        if (StringUtils.hasText(category)) {
            wrapper.eq(HouseNews::getCategory, category);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(HouseNews::getTitle, keyword)
                    .or().like(HouseNews::getSummary, keyword));
        }
        wrapper.orderByDesc(HouseNews::getCreateTime);
        Page<HouseNews> result = page(pageParam, wrapper);
        return new PageResult<>(result.getTotal(), result.getPages(), result.getCurrent(), result.getSize(),
                result.getRecords());
    }

    @Override
    public HouseNews getDetail(Long id) {
        HouseNews news = getById(id);
        if (news != null) {
            news.setViewCount(news.getViewCount() + 1);
            updateById(news);
        }
        return news;
    }

    @Override
    public boolean publish(HouseNews news) {
        news.setViewCount(0);
        news.setStatus(1);
        return save(news);
    }

    @Override
    public List<HouseNews> recommend(Long userId, Integer limit) {
        return hotList(limit);
    }

    @Override
    public List<HouseNews> hotList(Integer limit) {
        return list(new LambdaQueryWrapper<HouseNews>()
                .eq(HouseNews::getStatus, 1)
                .orderByDesc(HouseNews::getViewCount)
                .last("LIMIT " + limit));
    }
}
