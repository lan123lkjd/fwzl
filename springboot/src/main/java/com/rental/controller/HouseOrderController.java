package com.rental.controller;

import com.rental.common.PageResult;
import com.rental.common.Result;
import com.rental.entity.HouseOrderInfo;
import com.rental.service.FlowIndexService;
import com.rental.service.HouseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 预约看房控制器
 */
@RestController
@RequestMapping("/api/order")
@Tag(name = "预约看房接口")
public class HouseOrderController {

    @Autowired
    private HouseOrderService orderService;

    @Autowired
    private FlowIndexService flowIndexService;

    @PostMapping
    @Operation(summary = "创建预约")
    public Result<Void> create(@RequestBody HouseOrderInfo order, @RequestAttribute("userId") Long userId) {
        order.setUserId(userId);
        try {
            orderService.createOrder(order);
            flowIndexService.recordOrder();
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/confirm/{id}")
    @Operation(summary = "确认预约(房东)")
    public Result<Void> confirm(@PathVariable Long id, @RequestAttribute("userId") Long landlordId) {
        try {
            orderService.confirm(id, landlordId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/reject/{id}")
    @Operation(summary = "拒绝预约(房东)")
    public Result<Void> reject(@PathVariable Long id,
            @RequestAttribute("userId") Long landlordId,
            @RequestParam(required = false) String remark) {
        try {
            orderService.reject(id, landlordId, remark);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/complete/{id}")
    @Operation(summary = "完成预约")
    public Result<Void> complete(@PathVariable Long id,
            @RequestAttribute("userId") Long userId,
            @RequestAttribute("role") Integer role) {
        try {
            int operatorType = role == 2 ? 2 : 1;
            orderService.complete(id, userId, operatorType);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/cancel/{id}")
    @Operation(summary = "取消预约(用户)")
    public Result<Void> cancel(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        try {
            orderService.cancel(id, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user")
    @Operation(summary = "用户预约列表")
    public Result<PageResult<HouseOrderInfo>> listByUser(
            @RequestAttribute("userId") Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.listByUser(userId, status, page, size));
    }

    @GetMapping("/landlord")
    @Operation(summary = "房东预约列表")
    public Result<PageResult<HouseOrderInfo>> listByLandlord(
            @RequestAttribute("userId") Long landlordId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.listByLandlord(landlordId, status, page, size));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "预约详情")
    public Result<HouseOrderInfo> detail(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }
}
