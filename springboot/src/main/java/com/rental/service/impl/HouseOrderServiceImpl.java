package com.rental.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.common.PageResult;
import com.rental.entity.HouseOrderInfo;
import com.rental.entity.HouseOrderStatus;
import com.rental.mapper.HouseOrderInfoMapper;
import com.rental.mapper.HouseOrderStatusMapper;
import com.rental.service.HouseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 预约看房服务实现
 */
@Service
public class HouseOrderServiceImpl extends ServiceImpl<HouseOrderInfoMapper, HouseOrderInfo>
        implements HouseOrderService {

    @Autowired
    private HouseOrderStatusMapper statusMapper;

    @Override
    @Transactional
    public boolean createOrder(HouseOrderInfo order) {
        order.setOrderNo("ORD" + IdUtil.getSnowflakeNextIdStr());
        order.setStatus(0); // 待确认
        boolean saved = save(order);

        if (saved) {
            // 记录状态流转
            saveStatusLog(order.getId(), 0, order.getUserId(), 1, "用户创建预约");
        }
        return saved;
    }

    @Override
    @Transactional
    public boolean confirm(Long id, Long landlordId) {
        HouseOrderInfo order = getById(id);
        if (order == null || !order.getLandlordId().equals(landlordId)) {
            throw new RuntimeException("无权操作");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("当前状态不可确认");
        }

        order.setStatus(1);
        boolean updated = updateById(order);

        if (updated) {
            saveStatusLog(id, 1, landlordId, 2, "房东确认预约");
        }
        return updated;
    }

    @Override
    @Transactional
    public boolean reject(Long id, Long landlordId, String remark) {
        HouseOrderInfo order = getById(id);
        if (order == null || !order.getLandlordId().equals(landlordId)) {
            throw new RuntimeException("无权操作");
        }
        if (order.getStatus() != 0) {
            throw new RuntimeException("当前状态不可拒绝");
        }

        order.setStatus(4);
        boolean updated = updateById(order);

        if (updated) {
            saveStatusLog(id, 4, landlordId, 2, remark);
        }
        return updated;
    }

    @Override
    @Transactional
    public boolean complete(Long id, Long operatorId, Integer operatorType) {
        HouseOrderInfo order = getById(id);
        if (order == null) {
            throw new RuntimeException("预约不存在");
        }
        if (order.getStatus() != 1) {
            throw new RuntimeException("当前状态不可完成");
        }

        order.setStatus(2);
        boolean updated = updateById(order);

        if (updated) {
            saveStatusLog(id, 2, operatorId, operatorType, "看房完成");
        }
        return updated;
    }

    @Override
    @Transactional
    public boolean cancel(Long id, Long userId) {
        HouseOrderInfo order = getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        if (order.getStatus() >= 2) {
            throw new RuntimeException("当前状态不可取消");
        }

        order.setStatus(3);
        boolean updated = updateById(order);

        if (updated) {
            saveStatusLog(id, 3, userId, 1, "用户取消预约");
        }
        return updated;
    }

    @Override
    public PageResult<HouseOrderInfo> listByUser(Long userId, Integer status, Integer page, Integer size) {
        Page<HouseOrderInfo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HouseOrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HouseOrderInfo::getUserId, userId);
        if (status != null) {
            wrapper.eq(HouseOrderInfo::getStatus, status);
        }
        wrapper.orderByDesc(HouseOrderInfo::getCreateTime);
        Page<HouseOrderInfo> result = page(pageParam, wrapper);

        return new PageResult<>(result.getTotal(), result.getPages(),
                result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public PageResult<HouseOrderInfo> listByLandlord(Long landlordId, Integer status, Integer page, Integer size) {
        Page<HouseOrderInfo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HouseOrderInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HouseOrderInfo::getLandlordId, landlordId);
        if (status != null) {
            wrapper.eq(HouseOrderInfo::getStatus, status);
        }
        wrapper.orderByDesc(HouseOrderInfo::getCreateTime);
        Page<HouseOrderInfo> result = page(pageParam, wrapper);

        return new PageResult<>(result.getTotal(), result.getPages(),
                result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public PageResult<HouseOrderInfo> listAll(Integer status, Integer page, Integer size) {
        Page<HouseOrderInfo> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<HouseOrderInfo> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(HouseOrderInfo::getStatus, status);
        }
        wrapper.orderByDesc(HouseOrderInfo::getCreateTime);
        Page<HouseOrderInfo> result = page(pageParam, wrapper);

        return new PageResult<>(result.getTotal(), result.getPages(),
                result.getCurrent(), result.getSize(), result.getRecords());
    }

    private void saveStatusLog(Long orderId, Integer status, Long operatorId, Integer operatorType, String remark) {
        HouseOrderStatus statusLog = new HouseOrderStatus();
        statusLog.setOrderId(orderId);
        statusLog.setStatus(status);
        statusLog.setOperatorId(operatorId);
        statusLog.setOperatorType(operatorType);
        statusLog.setRemark(remark);
        statusMapper.insert(statusLog);
    }

    @Override
    @Transactional
    public boolean evaluate(Long id, Long userId, Integer rating, String content) {
        HouseOrderInfo order = getById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        if (order.getStatus() != 2) {
            throw new RuntimeException("只有已完成的预约才能评价");
        }
        if (order.getRating() != null) {
            throw new RuntimeException("已评价过，不能重复评价");
        }

        order.setRating(rating);
        order.setEvaluationContent(content);
        order.setEvaluationTime(java.time.LocalDateTime.now());
        return updateById(order);
    }
}
