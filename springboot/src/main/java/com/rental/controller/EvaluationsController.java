package com.rental.controller;

import com.rental.common.PageResult;
import com.rental.common.Result;
import com.rental.entity.Evaluations;
import com.rental.service.EvaluationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/api/evaluations")
@Tag(name = "评论接口")
public class EvaluationsController {

    @Autowired
    private EvaluationsService evaluationsService;

    @GetMapping("/list/{houseId}")
    @Operation(summary = "获取房源评论列表")
    public Result<PageResult<Evaluations>> list(
            @PathVariable Long houseId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(evaluationsService.listByHouse(houseId, page, size));
    }

    @GetMapping("/all/{houseId}")
    @Operation(summary = "获取房源所有评论(含回复)")
    public Result<List<Evaluations>> getAll(@PathVariable Long houseId) {
        return Result.success(evaluationsService.getWithReplies(houseId));
    }

    @PostMapping
    @Operation(summary = "发表评论")
    public Result<Void> publish(@RequestBody Evaluations evaluations,
            @RequestAttribute("userId") Long userId) {
        evaluations.setUserId(userId);
        evaluationsService.publish(evaluations);
        return Result.success();
    }

    @PostMapping("/upvote/{id}")
    @Operation(summary = "点赞评论")
    public Result<Void> upvote(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        try {
            evaluationsService.upvote(id, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/upvote/{id}")
    @Operation(summary = "取消点赞")
    public Result<Void> cancelUpvote(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        evaluationsService.cancelUpvote(id, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论")
    public Result<Void> delete(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        try {
            evaluationsService.deleteByUser(id, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
