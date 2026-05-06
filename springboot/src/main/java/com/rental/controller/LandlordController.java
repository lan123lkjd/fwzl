package com.rental.controller;

import com.rental.common.PageResult;
import com.rental.common.Result;
import com.rental.entity.Landlord;
import com.rental.mapper.HouseOrderInfoMapper;
import com.rental.service.LandlordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 房东控制器
 */
@RestController
@RequestMapping("/api/landlord")
@Tag(name = "房东接口")
public class LandlordController {

    @Autowired
    private LandlordService landlordService;

    @Autowired
    private HouseOrderInfoMapper orderInfoMapper;

    @PostMapping("/apply")
    @Operation(summary = "申请成为房东")
    public Result<Void> apply(@RequestBody Landlord landlord, @RequestAttribute("userId") Long userId) {
        landlord.setUserId(userId);
        try {
            landlordService.apply(landlord);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    @Operation(summary = "获取房东信息")
    public Result<Landlord> getInfo(@RequestAttribute("userId") Long userId) {
        return Result.success(landlordService.getByUserId(userId));
    }

    @PutMapping
    @Operation(summary = "更新房东信息")
    public Result<Void> update(@RequestBody Landlord landlord, @RequestAttribute("userId") Long userId) {
        Landlord existing = landlordService.getByUserId(userId);
        if (existing != null) {
            landlord.setId(existing.getId());
            landlordService.updateById(landlord);
        }
        return Result.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取房东详情")
    public Result<Landlord> detail(@PathVariable Long id) {
        return Result.success(landlordService.getById(id));
    }

    @GetMapping("/rating/{landlordId}")
    @Operation(summary = "获取房东服务评分")
    public Result<Map<String, Object>> getRating(@PathVariable Long landlordId) {
        Double avgRating = orderInfoMapper.getAvgRatingByLandlordId(landlordId);
        Integer ratingCount = orderInfoMapper.getRatingCountByLandlordId(landlordId);
        Map<String, Object> data = new HashMap<>();
        data.put("avgRating", avgRating != null ? Math.round(avgRating * 10) / 10.0 : 0.0);
        data.put("ratingCount", ratingCount != null ? ratingCount : 0);
        return Result.success(data);
    }

    @GetMapping("/evaluations/{landlordId}")
    @Operation(summary = "获取房东服务评价列表")
    public Result<List<Map<String, Object>>> getEvaluations(@PathVariable Long landlordId, 
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(orderInfoMapper.getEvaluationsByLandlordId(landlordId, limit));
    }
}
