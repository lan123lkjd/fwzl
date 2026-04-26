package com.rental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rental.common.PageResult;
import com.rental.common.Result;
import com.rental.entity.Area;
import com.rental.entity.Community;
import com.rental.entity.Notice;
import com.rental.mapper.AreaMapper;
import com.rental.mapper.CommunityMapper;
import com.rental.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "公共接口")
public class CommonController {

    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private CommunityMapper communityMapper;
    @Autowired
    private NoticeService noticeService;

    @GetMapping("/area/list")
    @Operation(summary = "地区列表")
    public Result<List<Area>> areaList() {
        return Result.success(areaMapper.selectList(null));
    }

    @GetMapping("/community/list")
    @Operation(summary = "小区列表")
    public Result<List<Community>> communityList(@RequestParam(required = false) Long areaId) {
        LambdaQueryWrapper<Community> wrapper = new LambdaQueryWrapper<>();
        if (areaId != null) {
            wrapper.eq(Community::getAreaId, areaId);
        }
        return Result.success(communityMapper.selectList(wrapper));
    }

    @GetMapping("/community/{id}")
    @Operation(summary = "小区详情")
    public Result<Community> communityDetail(@PathVariable Long id) {
        return Result.success(communityMapper.selectById(id));
    }

    @GetMapping("/notice/list")
    @Operation(summary = "公告列表")
    public Result<PageResult<Notice>> noticeList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer type) {
        return Result.success(noticeService.listPage(page, size, type));
    }

    @GetMapping("/notice/top")
    @Operation(summary = "置顶公告")
    public Result<List<Notice>> topNotice() {
        return Result.success(noticeService.topList());
    }

    @GetMapping("/notice/{id}")
    @Operation(summary = "公告详情")
    public Result<Notice> noticeDetail(@PathVariable Long id) {
        return Result.success(noticeService.getById(id));
    }
}
