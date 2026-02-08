package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.common.PageResult;
import com.rental.entity.HouseNews;

import java.util.List;

/**
 * 房屋资讯服务接口
 */
public interface HouseNewsService extends IService<HouseNews> {

    PageResult<HouseNews> listPage(Integer page, Integer size, String category, String keyword);

    HouseNews getDetail(Long id);

    boolean publish(HouseNews news);

    List<HouseNews> recommend(Long userId, Integer limit);

    List<HouseNews> hotList(Integer limit);
}
