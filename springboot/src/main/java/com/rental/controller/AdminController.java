package com.rental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rental.common.PageResult;
import com.rental.common.Result;
import com.rental.entity.*;
import com.rental.mapper.*;
import com.rental.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员接口")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private HouseService houseService;
    @Autowired
    private LandlordService landlordService;
    @Autowired
    private HouseOrderService orderService;
    @Autowired
    private HouseNewsService newsService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private FlowIndexService flowIndexService;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private CommunityMapper communityMapper;
    @Autowired
    private EvaluationsService evaluationsService;

    // ========== 用户管理 ==========
    @GetMapping("/user/list")
    @Operation(summary = "用户列表")
    public Result<PageResult<User>> userList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(userService.listPage(page, size, keyword));
    }

    @PutMapping("/user/status/{id}")
    @Operation(summary = "更新用户状态")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/user/{id}")
    @Operation(summary = "删除用户")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }

    // ========== 房源管理 ==========
    @GetMapping("/house/list")
    @Operation(summary = "房源列表(管理)")
    public Result<List<House>> houseList(@RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(House::getStatus, status);
        }
        wrapper.orderByDesc(House::getCreateTime);
        return Result.success(houseService.list(wrapper));
    }

    @PutMapping("/house/audit/{id}")
    @Operation(summary = "审核房源")
    public Result<Void> auditHouse(@PathVariable Long id, @RequestParam Integer status,
            @RequestParam(required = false) String remark) {
        houseService.audit(id, status, remark);
        return Result.success();
    }

    // ========== 房东管理 ==========
    @GetMapping("/landlord/list")
    @Operation(summary = "房东列表")
    public Result<PageResult<Landlord>> landlordList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        return Result.success(landlordService.listPage(page, size, status, keyword));
    }

    @PutMapping("/landlord/audit/{id}")
    @Operation(summary = "审核房东")
    public Result<Void> auditLandlord(@PathVariable Long id, @RequestParam Integer status,
            @RequestParam(required = false) String remark) {
        try {
            landlordService.audit(id, status, remark);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // ========== 预约管理 ==========
    @GetMapping("/order/list")
    @Operation(summary = "预约列表(管理)")
    public Result<PageResult<HouseOrderInfo>> orderList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(orderService.listAll(status, page, size));
    }

    // ========== 资讯管理 ==========
    @GetMapping("/news/list")
    @Operation(summary = "资讯列表(管理)")
    public Result<List<HouseNews>> newsList() {
        return Result.success(newsService.list(new LambdaQueryWrapper<HouseNews>()
                .orderByDesc(HouseNews::getCreateTime)));
    }

    @PostMapping("/news")
    @Operation(summary = "发布资讯")
    public Result<Void> publishNews(@RequestBody HouseNews news, @RequestAttribute("userId") Long userId) {
        news.setAuthorId(userId);
        newsService.publish(news);
        return Result.success();
    }

    @PutMapping("/news/{id}")
    @Operation(summary = "更新资讯")
    public Result<Void> updateNews(@PathVariable Long id, @RequestBody HouseNews news) {
        news.setId(id);
        newsService.updateById(news);
        return Result.success();
    }

    @DeleteMapping("/news/{id}")
    @Operation(summary = "删除资讯")
    public Result<Void> deleteNews(@PathVariable Long id) {
        newsService.removeById(id);
        return Result.success();
    }

    // ========== 公告管理 ==========
    @GetMapping("/notice/list")
    @Operation(summary = "公告列表(管理)")
    public Result<List<Notice>> noticeList() {
        return Result.success(noticeService.list(new LambdaQueryWrapper<Notice>()
                .orderByDesc(Notice::getTop)
                .orderByDesc(Notice::getCreateTime)));
    }

    @PostMapping("/notice")
    @Operation(summary = "发布公告")
    public Result<Void> publishNotice(@RequestBody Notice notice) {
        noticeService.save(notice);
        return Result.success();
    }

    @PutMapping("/notice/{id}")
    @Operation(summary = "更新公告")
    public Result<Void> updateNotice(@PathVariable Long id, @RequestBody Notice notice) {
        notice.setId(id);
        noticeService.updateById(notice);
        return Result.success();
    }

    @DeleteMapping("/notice/{id}")
    @Operation(summary = "删除公告")
    public Result<Void> deleteNotice(@PathVariable Long id) {
        noticeService.removeById(id);
        return Result.success();
    }

    // ========== 地区管理 ==========
    @GetMapping("/area/list")
    @Operation(summary = "地区列表")
    public Result<List<Area>> areaList(@RequestParam(required = false) Long parentId) {
        LambdaQueryWrapper<Area> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Area::getParentId, parentId == null ? 0 : parentId)
                .orderByAsc(Area::getSort);
        return Result.success(areaMapper.selectList(wrapper));
    }

    @PostMapping("/area")
    @Operation(summary = "添加地区")
    public Result<Void> addArea(@RequestBody Area area) {
        areaMapper.insert(area);
        return Result.success();
    }

    // ========== 小区管理 ==========
    @GetMapping("/community/list")
    @Operation(summary = "小区列表")
    public Result<List<Community>> communityList(@RequestParam(required = false) Long areaId) {
        LambdaQueryWrapper<Community> wrapper = new LambdaQueryWrapper<>();
        if (areaId != null) {
            wrapper.eq(Community::getAreaId, areaId);
        }
        return Result.success(communityMapper.selectList(wrapper));
    }

    @PostMapping("/community")
    @Operation(summary = "添加小区")
    public Result<Void> addCommunity(@RequestBody Community community) {
        communityMapper.insert(community);
        return Result.success();
    }

    // ========== 统计数据 ==========
    @GetMapping("/stats/today")
    @Operation(summary = "今日统计")
    public Result<Map<String, Object>> todayStats() {
        return Result.success(flowIndexService.getTodayStats());
    }

    @GetMapping("/stats/total")
    @Operation(summary = "总体统计")
    public Result<Map<String, Object>> totalStats() {
        return Result.success(flowIndexService.getTotalStats());
    }

    @GetMapping("/stats/trend")
    @Operation(summary = "趋势数据")
    public Result<List<FlowIndex>> trendStats(@RequestParam(defaultValue = "7") Integer days) {
        return Result.success(flowIndexService.getRecentDays(days));
    }

    // ========== 评论管理 ==========
    @GetMapping("/evaluation/list")
    @Operation(summary = "评论列表(管理)")
    public Result<PageResult<Evaluations>> evaluationList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        return Result.success(evaluationsService.listAll(page, size, keyword));
    }

    @DeleteMapping("/evaluation/{id}")
    @Operation(summary = "删除评论")
    public Result<Void> deleteEvaluation(@PathVariable Long id) {
        evaluationsService.removeById(id);
        return Result.success();
    }
}
