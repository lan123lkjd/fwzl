package com.rental.controller;

import com.rental.common.PageResult;
import com.rental.common.Result;
import com.rental.entity.HouseNews;
import com.rental.service.HouseNewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@Tag(name = "资讯接口")
public class HouseNewsController {

    @Autowired
    private HouseNewsService newsService;

    @GetMapping("/list")
    @Operation(summary = "资讯列表")
    public Result<PageResult<HouseNews>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword) {
        return Result.success(newsService.listPage(page, size, category, keyword));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "资讯详情")
    public Result<HouseNews> detail(@PathVariable Long id) {
        return Result.success(newsService.getDetail(id));
    }

    @GetMapping("/hot")
    @Operation(summary = "热门资讯")
    public Result<List<HouseNews>> hot(@RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(newsService.hotList(limit));
    }

    @GetMapping("/recommend")
    @Operation(summary = "推荐资讯")
    public Result<List<HouseNews>> recommend(
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(newsService.recommend(userId, limit));
    }
}
