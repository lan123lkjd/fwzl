package com.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.common.PageResult;
import com.rental.entity.House;
import com.rental.mapper.HouseMapper;
import com.rental.mapper.UserBrowseHistoryMapper;
import com.rental.mapper.UserCollectMapper;
import com.rental.service.HouseService;
import com.rental.service.algorithm.CollaborativeFilteringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 房源服务实现
 */
@Service
public class HouseServiceImpl extends ServiceImpl<HouseMapper, House> implements HouseService {

    @Autowired
    private UserBrowseHistoryMapper browseHistoryMapper;

    @Autowired
    private UserCollectMapper collectMapper;

    @Autowired
    private CollaborativeFilteringService cfService;

    @Override
    public PageResult<House> listPage(Integer page, Integer size, Long areaId, Long communityId,
            Integer houseType, BigDecimal minPrice, BigDecimal maxPrice, String keyword) {
        Page<House> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(House::getStatus, 1); // 只查询已上架的

        if (areaId != null) {
            wrapper.eq(House::getAreaId, areaId);
        }
        if (communityId != null) {
            wrapper.eq(House::getCommunityId, communityId);
        }
        if (houseType != null) {
            wrapper.eq(House::getHouseType, houseType);
        }
        if (minPrice != null) {
            wrapper.ge(House::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(House::getPrice, maxPrice);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(House::getTitle, keyword)
                    .or().like(House::getAddress, keyword)
                    .or().like(House::getDescription, keyword));
        }

        wrapper.orderByDesc(House::getCreateTime);
        Page<House> result = page(pageParam, wrapper);

        return new PageResult<>(result.getTotal(), result.getPages(),
                result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public House getDetail(Long id) {
        return getById(id);
    }

    @Override
    public boolean publish(House house) {
        house.setStatus(0); // 待审核
        house.setViewCount(0);
        house.setCollectCount(0);
        return save(house);
    }

    @Override
    public boolean audit(Long id, Integer status, String remark) {
        House house = new House();
        house.setId(id);
        house.setStatus(status);
        return updateById(house);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        House house = new House();
        house.setId(id);
        house.setStatus(status);
        return updateById(house);
    }

    @Override
    public void increaseViewCount(Long id) {
        House house = getById(id);
        if (house != null) {
            house.setViewCount(house.getViewCount() + 1);
            updateById(house);
        }
    }

    @Override
    public PageResult<House> listByLandlord(Long landlordId, Integer page, Integer size) {
        Page<House> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(House::getLandlordId, landlordId)
                .orderByDesc(House::getCreateTime);
        Page<House> result = page(pageParam, wrapper);

        return new PageResult<>(result.getTotal(), result.getPages(),
                result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public List<House> recommend(Long userId, Integer limit) {
        return cfService.recommendHouses(userId, limit);
    }

    @Override
    public List<House> hotList(Integer limit) {
        return list(new LambdaQueryWrapper<House>()
                .eq(House::getStatus, 1)
                .orderByDesc(House::getViewCount)
                .last("LIMIT " + limit));
    }
}
