package com.rental.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.common.PageResult;
import com.rental.entity.House;
import com.rental.entity.HouseRental;
import com.rental.mapper.HouseMapper;
import com.rental.mapper.HouseRentalMapper;
import com.rental.service.HouseRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Period;

/**
 * 房屋租赁服务实现
 */
@Service
public class HouseRentalServiceImpl implements HouseRentalService {

    @Autowired
    private HouseRentalMapper rentalMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Override
    @Transactional
    public void createRental(HouseRental rental) {
        // 检查房源是否存在
        House house = houseMapper.selectById(rental.getHouseId());
        if (house == null) {
            throw new RuntimeException("房源不存在");
        }

        // 检查房源状态：状态2表示已下架，状态3表示已出租
        if (house.getStatus() == 2) {
            throw new RuntimeException("该房源已下架，无法租赁");
        }
        if (house.getStatus() == 3) {
            throw new RuntimeException("该房源已被租出，无法租赁");
        }

        // 检查是否已有待确认或租赁中的租赁记录
        LambdaQueryWrapper<HouseRental> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HouseRental::getHouseId, rental.getHouseId())
                .in(HouseRental::getStatus, 0, 1); // 待确认或租赁中
        Long count = rentalMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该房源已有租赁申请或正在租赁中");
        }

        // 生成租赁单号
        rental.setRentalNo("R" + IdUtil.getSnowflakeNextIdStr());
        rental.setStatus(0); // 待确认
        rental.setCreateTime(LocalDateTime.now());
        rental.setUpdateTime(LocalDateTime.now());

        // 计算租赁月数和总金额
        if (rental.getStartDate() != null && rental.getEndDate() != null && rental.getMonthlyRent() != null) {
            Period period = Period.between(rental.getStartDate(), rental.getEndDate());
            // TODO 测试
            long months = period.getMonths();;
            if (months < 1)
                months = 1;
            rental.setTotalAmount(rental.getMonthlyRent().multiply(java.math.BigDecimal.valueOf(months)));
        }

        rentalMapper.insert(rental);
    }

    @Override
    @Transactional
    public void confirm(Long id, Long landlordId) {
        HouseRental rental = rentalMapper.selectById(id);
        if (rental == null) {
            throw new RuntimeException("租赁记录不存在");
        }
        if (!rental.getLandlordId().equals(landlordId)) {
            throw new RuntimeException("无权操作");
        }
        if (rental.getStatus() != 0) {
            throw new RuntimeException("当前状态不允许确认");
        }

        rental.setStatus(1); // 租赁中
        rental.setUpdateTime(LocalDateTime.now());
        rentalMapper.updateById(rental);

        // 更新房源状态为已出租
        House house = houseMapper.selectById(rental.getHouseId());
        if (house != null) {
            house.setStatus(2); // 已出租
            houseMapper.updateById(house);
        }
    }

    @Override
    @Transactional
    public void reject(Long id, Long landlordId, String remark) {
        HouseRental rental = rentalMapper.selectById(id);
        if (rental == null) {
            throw new RuntimeException("租赁记录不存在");
        }
        if (!rental.getLandlordId().equals(landlordId)) {
            throw new RuntimeException("无权操作");
        }
        if (rental.getStatus() != 0) {
            throw new RuntimeException("当前状态不允许拒绝");
        }

        rental.setStatus(4); // 已拒绝
        if (remark != null) {
            rental.setRemark(
                    rental.getRemark() != null ? rental.getRemark() + " | 拒绝原因: " + remark : "拒绝原因: " + remark);
        }
        rental.setUpdateTime(LocalDateTime.now());
        rentalMapper.updateById(rental);
    }

    @Override
    @Transactional
    public void complete(Long id, Long operatorId, int operatorType) {
        HouseRental rental = rentalMapper.selectById(id);
        if (rental == null) {
            throw new RuntimeException("租赁记录不存在");
        }

        // 验证操作权限
        if (operatorType == 1 && !rental.getUserId().equals(operatorId)) {
            throw new RuntimeException("无权操作");
        }
        if (operatorType == 2 && !rental.getLandlordId().equals(operatorId)) {
            throw new RuntimeException("无权操作");
        }

        if (rental.getStatus() != 1) {
            throw new RuntimeException("当前状态不允许完成");
        }

        rental.setStatus(2); // 已完成
        rental.setUpdateTime(LocalDateTime.now());
        rentalMapper.updateById(rental);

        // 更新房源状态为可出租
        House house = houseMapper.selectById(rental.getHouseId());
        if (house != null) {
            house.setStatus(1); // 可出租
            houseMapper.updateById(house);
        }
    }

    @Override
    @Transactional
    public void cancel(Long id, Long userId) {
        HouseRental rental = rentalMapper.selectById(id);
        if (rental == null) {
            throw new RuntimeException("租赁记录不存在");
        }
        if (!rental.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        if (rental.getStatus() > 1) {
            throw new RuntimeException("当前状态不允许取消");
        }

        rental.setStatus(3); // 已取消
        rental.setUpdateTime(LocalDateTime.now());
        rentalMapper.updateById(rental);
    }

    @Override
    public PageResult<HouseRental> listByUser(Long userId, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<HouseRental> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HouseRental::getUserId, userId);
        if (status != null) {
            wrapper.eq(HouseRental::getStatus, status);
        }
        wrapper.orderByDesc(HouseRental::getCreateTime);

        Page<HouseRental> pageResult = rentalMapper.selectPage(new Page<>(page, size), wrapper);

        // 填充房源信息
        for (HouseRental rental : pageResult.getRecords()) {
            House house = houseMapper.selectById(rental.getHouseId());
            if (house != null) {
                rental.setHouseTitle(house.getTitle());
            }
        }

        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal());
    }

    @Override
    public PageResult<HouseRental> listByLandlord(Long landlordId, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<HouseRental> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HouseRental::getLandlordId, landlordId);
        if (status != null) {
            wrapper.eq(HouseRental::getStatus, status);
        }
        wrapper.orderByDesc(HouseRental::getCreateTime);

        Page<HouseRental> pageResult = rentalMapper.selectPage(new Page<>(page, size), wrapper);

        // 填充房源信息
        for (HouseRental rental : pageResult.getRecords()) {
            House house = houseMapper.selectById(rental.getHouseId());
            if (house != null) {
                rental.setHouseTitle(house.getTitle());
            }
        }

        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal());
    }

    @Override
    public HouseRental getById(Long id) {
        HouseRental rental = rentalMapper.selectById(id);
        if (rental != null) {
            House house = houseMapper.selectById(rental.getHouseId());
            if (house != null) {
                rental.setHouseTitle(house.getTitle());
            }
        }
        return rental;
    }
}
