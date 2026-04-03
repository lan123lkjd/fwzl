package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.common.PageResult;
import com.rental.entity.House;

import java.math.BigDecimal;
import java.util.List;

/**
 * 房源服务接口
 */
public interface HouseService extends IService<House> {

    /**
     * 分页查询房源
     */
    PageResult<House> listPage(Integer page, Integer size, Long areaId, Long communityId,
            Integer houseType, BigDecimal minPrice, BigDecimal maxPrice, String keyword);

    /**
     * 获取房源详情
     */
    House getDetail(Long id);

    /**
     * 发布房源
     */
    boolean publish(House house);

    /**
     * 审核房源
     */
    boolean audit(Long id, Integer status, String remark);

    /**
     * 上下架房源
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 增加浏览次数
     */
    void increaseViewCount(Long id);

    /**
     * 获取房东的房源列表
     */
    PageResult<House> listByLandlord(Long landlordId, Integer page, Integer size);

    /**
     * 智能推荐房源（协同过滤）
     */
    List<House> recommend(Long userId, Integer limit);

    /**
     * 热门房源
     */
    List<House> hotList(Integer limit);

    /**
     * 获取用户收藏列表
     */
    PageResult<House> getCollectList(Long userId, Integer page, Integer size);
}
