package com.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.common.PageResult;
import com.rental.entity.Area;
import com.rental.entity.House;
import com.rental.mapper.AreaMapper;
import com.rental.mapper.HouseMapper;
import com.rental.mapper.UserBrowseHistoryMapper;
import com.rental.mapper.UserCollectMapper;
import com.rental.service.HouseService;
import com.rental.service.algorithm.CollaborativeFilteringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Autowired
    private AreaMapper areaMapper;

    private void findAllChildAreaIds(Long parentId, List<Long> result) {
        List<Area> children = areaMapper.selectList(new LambdaQueryWrapper<Area>()
                .eq(Area::getParentId, parentId));
        for (Area child : children) {
            result.add(child.getId());
            findAllChildAreaIds(child.getId(), result);
        }
    }

    @Override
    public PageResult<House> listPage(Integer page, Integer size, Long areaId,
            Integer houseType, BigDecimal minPrice, BigDecimal maxPrice, String keyword) {
        Page<House> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(House::getStatus, 1);

        if (areaId != null) {
            List<Long> allAreaIds = new ArrayList<>();
            allAreaIds.add(areaId);
            findAllChildAreaIds(areaId, allAreaIds);
            wrapper.in(House::getAreaId, allAreaIds);
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
        House house = getById(id);
        if (house == null) {
            return false;
        }
        if (house.getStatus() == 0) {
            throw new RuntimeException("待审核的房源不能直接上架，请等待管理员审核");
        }
        if (house.getStatus() == 3) {
            throw new RuntimeException("已出租的房源不能上架或下架");
        }
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

    @Override
    public PageResult<House> getCollectList(Long userId, Integer page, Integer size) {
        List<Long> houseIds = collectMapper.selectList(new LambdaQueryWrapper<com.rental.entity.UserCollect>()
                .eq(com.rental.entity.UserCollect::getUserId, userId)
                .select(com.rental.entity.UserCollect::getHouseId))
                .stream()
                .map(com.rental.entity.UserCollect::getHouseId)
                .toList();

        if (houseIds.isEmpty()) {
            return new PageResult<>(0L, 0L, (long) page, (long) size, List.of());
        }

        Page<House> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(House::getId, houseIds)
                .orderByDesc(House::getCreateTime);
        Page<House> result = page(pageParam, wrapper);

        return new PageResult<>(result.getTotal(), result.getPages(),
                result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public PageResult<House> getBrowseHistory(Long userId, Integer page, Integer size) {
        List<Long> houseIds = browseHistoryMapper.selectList(
                new LambdaQueryWrapper<com.rental.entity.UserBrowseHistory>()
                        .eq(com.rental.entity.UserBrowseHistory::getUserId, userId)
                        .orderByDesc(com.rental.entity.UserBrowseHistory::getBrowseTime)
                        .select(com.rental.entity.UserBrowseHistory::getHouseId))
                .stream()
                .map(com.rental.entity.UserBrowseHistory::getHouseId)
                .distinct()
                .toList();

        if (houseIds.isEmpty()) {
            return new PageResult<>(0L, 0L, (long) page, (long) size, List.of());
        }

        int start = (page - 1) * size;
        int end = Math.min(start + size, houseIds.size());
        List<Long> pageHouseIds = houseIds.subList(start, end);

        if (pageHouseIds.isEmpty()) {
            return new PageResult<>((long) houseIds.size(), (long) ((houseIds.size() + size - 1) / size),
                    (long) page, (long) size, List.of());
        }

        List<House> houses = list(new LambdaQueryWrapper<House>()
                .in(House::getId, pageHouseIds));

        java.util.Map<Long, House> houseMap = houses.stream()
                .collect(java.util.stream.Collectors.toMap(House::getId, h -> h));

        List<House> orderedHouses = pageHouseIds.stream()
                .map(houseMap::get)
                .filter(java.util.Objects::nonNull)
                .toList();

        return new PageResult<>((long) houseIds.size(), (long) ((houseIds.size() + size - 1) / size),
                (long) page, (long) size, orderedHouses);
    }

    @Override
    public void clearBrowseHistory(Long userId) {
        browseHistoryMapper.delete(new LambdaQueryWrapper<com.rental.entity.UserBrowseHistory>()
                .eq(com.rental.entity.UserBrowseHistory::getUserId, userId));
    }
}
