package com.rental.controller;

import com.rental.common.PageResult;
import com.rental.common.Result;
import com.rental.entity.HouseRental;
import com.rental.service.HouseRentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 房屋租赁控制器
 */
@RestController
@RequestMapping("/api/rental")
@Tag(name = "房屋租赁接口")
public class HouseRentalController {

    @Autowired
    private HouseRentalService rentalService;

    @PostMapping
    @Operation(summary = "创建租赁申请")
    public Result<Void> create(@RequestBody HouseRental rental, @RequestAttribute("userId") Long userId) {
        rental.setUserId(userId);
        try {
            rentalService.createRental(rental);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/confirm/{id}")
    @Operation(summary = "确认租赁(房东)")
    public Result<Void> confirm(@PathVariable Long id, @RequestAttribute("userId") Long landlordId) {
        try {
            rentalService.confirm(id, landlordId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/reject/{id}")
    @Operation(summary = "拒绝租赁(房东)")
    public Result<Void> reject(@PathVariable Long id,
            @RequestAttribute("userId") Long landlordId,
            @RequestParam(required = false) String remark) {
        try {
            rentalService.reject(id, landlordId, remark);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/complete/{id}")
    @Operation(summary = "完成租赁")
    public Result<Void> complete(@PathVariable Long id,
            @RequestAttribute("userId") Long userId,
            @RequestAttribute("role") Integer role) {
        try {
            int operatorType = role == 2 ? 2 : 1;
            rentalService.complete(id, userId, operatorType);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/cancel/{id}")
    @Operation(summary = "取消租赁(用户)")
    public Result<Void> cancel(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        try {
            rentalService.cancel(id, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user")
    @Operation(summary = "用户租赁列表")
    public Result<PageResult<HouseRental>> listByUser(
            @RequestAttribute("userId") Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size)     {
        return Result.success(rentalService.listByUser(userId, status, page, size));
    }

    @GetMapping("/landlord")
    @Operation(summary = "房东租赁列表")
    public Result<PageResult<HouseRental>> listByLandlord(
            @RequestAttribute("userId") Long landlordId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(rentalService.listByLandlord(landlordId, status, page, size));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "租赁详情")
    public Result<HouseRental> detail(@PathVariable Long id) {
        return Result.success(rentalService.getById(id));
    }
}
