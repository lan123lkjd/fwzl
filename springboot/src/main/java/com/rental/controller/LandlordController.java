package com.rental.controller;

import com.rental.common.PageResult;
import com.rental.common.Result;
import com.rental.entity.Landlord;
import com.rental.service.LandlordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 房东控制器
 */
@RestController
@RequestMapping("/api/landlord")
@Tag(name = "房东接口")
public class LandlordController {

    @Autowired
    private LandlordService landlordService;

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
}
