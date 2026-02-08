package com.rental.controller;

import com.rental.common.PageResult;
import com.rental.common.Result;
import com.rental.entity.House;
import com.rental.entity.UserBrowseHistory;
import com.rental.entity.UserCollect;
import com.rental.mapper.UserBrowseHistoryMapper;
import com.rental.mapper.UserCollectMapper;
import com.rental.service.FlowIndexService;
import com.rental.service.HouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 房源控制器
 */
@RestController
@RequestMapping("/api/house")
@Tag(name = "房源接口")
public class HouseController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private UserBrowseHistoryMapper browseHistoryMapper;

    @Autowired
    private UserCollectMapper collectMapper;

    @Autowired
    private FlowIndexService flowIndexService;

    @GetMapping("/list")
    @Operation(summary = "房源列表")
    public Result<PageResult<House>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) Long communityId,
            @RequestParam(required = false) Integer houseType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String keyword) {
        return Result.success(
                houseService.listPage(page, size, areaId, communityId, houseType, minPrice, maxPrice, keyword));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "房源详情")
    public Result<House> detail(@PathVariable Long id,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        House house = houseService.getDetail(id);
        houseService.increaseViewCount(id);
        flowIndexService.recordHouseView();

        // 记录浏览历史
        if (userId != null) {
            UserBrowseHistory history = new UserBrowseHistory();
            history.setUserId(userId);
            history.setHouseId(id);
            history.setBrowseTime(LocalDateTime.now());
            browseHistoryMapper.insert(history);
        }

        return Result.success(house);
    }

    @GetMapping("/recommend")
    @Operation(summary = "智能推荐房源")
    public Result<List<House>> recommend(
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestParam(defaultValue = "6") Integer limit) {
        if (userId == null) {
            return Result.success(houseService.hotList(limit));
        }
        return Result.success(houseService.recommend(userId, limit));
    }

    @GetMapping("/hot")
    @Operation(summary = "热门房源")
    public Result<List<House>> hot(@RequestParam(defaultValue = "6") Integer limit) {
        return Result.success(houseService.hotList(limit));
    }

    @PostMapping("/publish")
    @Operation(summary = "发布房源")
    public Result<Void> publish(@RequestBody House house, @RequestAttribute("userId") Long userId) {
        house.setLandlordId(userId);
        houseService.publish(house);
        return Result.success();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新房源")
    public Result<Void> update(@PathVariable Long id, @RequestBody House house) {
        house.setId(id);
        houseService.updateById(house);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除房源")
    public Result<Void> delete(@PathVariable Long id) {
        houseService.removeById(id);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    @Operation(summary = "更新房源状态")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        houseService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/landlord")
    @Operation(summary = "房东的房源列表")
    public Result<PageResult<House>> listByLandlord(
            @RequestAttribute("userId") Long landlordId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(houseService.listByLandlord(landlordId, page, size));
    }

    @PostMapping("/collect/{id}")
    @Operation(summary = "收藏房源")
    public Result<Void> collect(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        UserCollect collect = new UserCollect();
        collect.setUserId(userId);
        collect.setHouseId(id);
        collectMapper.insert(collect);

        House house = houseService.getById(id);
        house.setCollectCount(house.getCollectCount() + 1);
        houseService.updateById(house);

        return Result.success();
    }

    @DeleteMapping("/collect/{id}")
    @Operation(summary = "取消收藏")
    public Result<Void> cancelCollect(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserCollect> wrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        wrapper.eq(UserCollect::getUserId, userId).eq(UserCollect::getHouseId, id);
        collectMapper.delete(wrapper);

        House house = houseService.getById(id);
        house.setCollectCount(Math.max(0, house.getCollectCount() - 1));
        houseService.updateById(house);

        return Result.success();
    }
}
