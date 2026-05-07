package com.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.common.PageResult;
import com.rental.entity.Area;
import com.rental.entity.House;
import com.rental.entity.Landlord;
import com.rental.entity.User;
import com.rental.mapper.AreaMapper;
import com.rental.mapper.HouseMapper;
import com.rental.mapper.LandlordMapper;
import com.rental.mapper.UserBrowseHistoryMapper;
import com.rental.mapper.UserCollectMapper;
import com.rental.mapper.UserMapper;
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

    @Autowired
    private LandlordMapper landlordMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AreaMapper areaMapper;

    @Override
    public PageResult<House> listPage(Integer page, Integer size, Long areaId,
            Integer houseType, BigDecimal minPrice, BigDecimal maxPrice, String keyword) {
        Page<House> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(House::getStatus, 1);

        if (areaId != null) {
            wrapper.eq(House::getAreaId, areaId);
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
        House house = getById(id);
        if (house != null) {
            if (house.getLandlordId() != null) {
                Landlord landlord = landlordMapper.selectById(house.getLandlordId());
                if (landlord != null) {
                    house.setLandlordName(landlord.getRealName());
                    house.setLandlordContact(landlord.getContact());
                    User user = userMapper.selectById(landlord.getUserId());
                    if (user != null) {
                        house.setLandlordAvatar(user.getAvatar());
                    }
                }
            }
            if (house.getAreaId() != null) {
                Area area = areaMapper.selectById(house.getAreaId());
                if (area != null) {
                    house.setAreaName(area.getName());
                }
            }
        }
        return house;
    }

    @Override
    public boolean publish(House house) {
        Landlord landlord = landlordMapper.selectOne(
                new LambdaQueryWrapper<Landlord>()
                        .eq(Landlord::getUserId, house.getLandlordId()));
        
        if (landlord == null) {
            throw new RuntimeException("您还不是房东，请先申请成为房东");
        }
        
        house.setLandlordId(landlord.getId());
        house.setStatus(0);
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
        Landlord landlord = landlordMapper.selectOne(
                new LambdaQueryWrapper<Landlord>()
                        .eq(Landlord::getUserId, landlordId));
        
        if (landlord == null) {
            return new PageResult<>(0L, 0L, (long) page, (long) size, List.of());
        }
        
        Page<House> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(House::getLandlordId, landlord.getId())
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

    @Override
    public PageResult<House> listAll(Integer page, Integer size, Integer status) {
        Page<House> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(House::getStatus, status);
        }
        wrapper.orderByDesc(House::getCreateTime);
        Page<House> result = page(pageParam, wrapper);
        return new PageResult<>(result.getTotal(), result.getPages(),
                result.getCurrent(), result.getSize(), result.getRecords());
    }
}
