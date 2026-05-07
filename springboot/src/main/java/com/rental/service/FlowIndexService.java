package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.entity.FlowIndex;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FlowIndexService extends IService<FlowIndex> {


    void recordHouseView();

    void recordOrder();

    void recordNewUser();

    List<FlowIndex> getRecentDays(Integer days);

    Map<String, Object> getTodayStats();

    Map<String, Object> getTotalStats();
}
