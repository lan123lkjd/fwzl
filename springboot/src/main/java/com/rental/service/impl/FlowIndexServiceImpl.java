package com.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.entity.FlowIndex;
import com.rental.mapper.FlowIndexMapper;
import com.rental.mapper.HouseMapper;
import com.rental.mapper.HouseOrderInfoMapper;
import com.rental.mapper.UserMapper;
import com.rental.service.FlowIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FlowIndexServiceImpl extends ServiceImpl<FlowIndexMapper, FlowIndex> implements FlowIndexService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private HouseOrderInfoMapper orderMapper;

    private final Set<String> todayUvSet = new HashSet<>();

    private FlowIndex getOrCreateToday() {
        LocalDate today = LocalDate.now();
        FlowIndex index = getOne(new LambdaQueryWrapper<FlowIndex>().eq(FlowIndex::getDate, today));
        if (index == null) {
            index = new FlowIndex();
            index.setDate(today);

            index.setNewUsers(0);
            index.setHouseViews(0);
            index.setOrderCount(0);
            save(index);
            todayUvSet.clear();
        }
        return index;
    }





    @Override
    public void recordHouseView() {
        FlowIndex index = getOrCreateToday();
        index.setHouseViews(index.getHouseViews() + 1);
        updateById(index);
    }

    @Override
    public void recordOrder() {
        FlowIndex index = getOrCreateToday();
        index.setOrderCount(index.getOrderCount() + 1);
        updateById(index);
    }

    @Override
    public void recordNewUser() {
        FlowIndex index = getOrCreateToday();
        index.setNewUsers(index.getNewUsers() + 1);
        updateById(index);
    }

    @Override
    public List<FlowIndex> getRecentDays(Integer days) {
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        return list(new LambdaQueryWrapper<FlowIndex>()
                .ge(FlowIndex::getDate, startDate)
                .orderByAsc(FlowIndex::getDate));
    }

    @Override
    public Map<String, Object> getTodayStats() {
        FlowIndex today = getOrCreateToday();
        Map<String, Object> stats = new HashMap<>();

        stats.put("newUsers", today.getNewUsers());
        stats.put("houseViews", today.getHouseViews());
        stats.put("orderCount", today.getOrderCount());
        return stats;
    }

    @Override
    public Map<String, Object> getTotalStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userMapper.selectCount(null));
        stats.put("totalHouses", houseMapper.selectCount(
                new LambdaQueryWrapper<com.rental.entity.House>().eq(com.rental.entity.House::getStatus, 1)));
        stats.put("totalOrders", orderMapper.selectCount(null));
        return stats;
    }
}
