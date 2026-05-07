package com.rental.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.common.PageResult;
import com.rental.entity.House;
import com.rental.entity.HouseRental;
import com.rental.entity.HouseRentalStatus;
import com.rental.entity.Landlord;
import com.rental.mapper.HouseMapper;
import com.rental.mapper.HouseRentalMapper;
import com.rental.mapper.HouseRentalStatusMapper;
import com.rental.mapper.LandlordMapper;
import com.rental.service.HouseRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 房屋租赁服务实现
 */
@Service
public class HouseRentalServiceImpl implements HouseRentalService {

    @Autowired
    private HouseRentalMapper rentalMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private HouseRentalStatusMapper rentalStatusMapper;

    @Autowired
    private LandlordMapper landlordMapper;

    private void saveStatus(Long rentalId, Integer status, Long operatorId, Integer operatorType, String remark) {
        HouseRentalStatus rs = new HouseRentalStatus();
        rs.setRentalId(rentalId);
        rs.setStatus(status);
        rs.setOperatorId(operatorId);
        rs.setOperatorType(operatorType);
        rs.setRemark(remark);
        rs.setCreateTime(LocalDateTime.now());
        rentalStatusMapper.insert(rs);
    }

    @Override
    @Transactional
    public void createRental(HouseRental rental) {
        // 检查房源是否存在
        House house = houseMapper.selectById(rental.getHouseId());
        if (house == null) {
            throw new RuntimeException("房源不存在");
        }

        // 检查房源状态：状态0待审核，状态2已下架，状态3已出租
        if (house.getStatus() == 0) {
            throw new RuntimeException("该房源正在审核中，无法租赁");
        }
        if (house.getStatus() == 2) {
            throw new RuntimeException("该房源已下架，无法租赁");
        }
        if (house.getStatus() == 3) {
            throw new RuntimeException("该房源已被租出，无法租赁");
        }

        // 检查是否已有待确认或租赁中的租赁记录
        LambdaQueryWrapper<HouseRental> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HouseRental::getHouseId, rental.getHouseId())
                .in(HouseRental::getStatus, 0, 1, 2);
        Long count = rentalMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该房源已有租赁申请或正在租赁中");
        }

        // 校验租赁日期
        if (rental.getStartDate() == null || rental.getEndDate() == null) {
            throw new RuntimeException("请选择租赁日期");
        }
        if (!rental.getStartDate().isBefore(rental.getEndDate())) {
            throw new RuntimeException("起租日期必须早于到期日期");
        }
        if (rental.getStartDate().isBefore(java.time.LocalDate.now())) {
            throw new RuntimeException("起租日期不能早于今天");
        }

        // 生成租赁单号
        rental.setRentalNo("R" + IdUtil.getSnowflakeNextIdStr());
        rental.setStatus(0); // 待确认
        rental.setCreateTime(LocalDateTime.now());
        rental.setUpdateTime(LocalDateTime.now());

        // 计算租赁月数和总金额
        if (rental.getStartDate() != null && rental.getEndDate() != null && rental.getMonthlyRent() != null) {
            Period period = Period.between(rental.getStartDate(), rental.getEndDate());
            long months = period.getYears() * 12L + period.getMonths();
            if (months < 1)
                months = 1;
            BigDecimal deposit = rental.getDeposit() != null ? rental.getDeposit() : rental.getMonthlyRent();
            rental.setDeposit(deposit);
            rental.setTotalAmount(rental.getMonthlyRent().multiply(java.math.BigDecimal.valueOf(months)).add(deposit));
        }

        rentalMapper.insert(rental);
        saveStatus(rental.getId(), 0, rental.getUserId(), 1, "用户提交租赁申请");
    }

    @Override
    @Transactional
    public void confirm(Long id, Long landlordId) {
        Landlord landlord = landlordMapper.selectOne(
                new LambdaQueryWrapper<Landlord>()
                        .eq(Landlord::getUserId, landlordId));
        
        if (landlord == null) {
            throw new RuntimeException("您还不是房东");
        }
        
        HouseRental rental = rentalMapper.selectById(id);
        if (rental == null) {
            throw new RuntimeException("租赁记录不存在");
        }
        if (!rental.getLandlordId().equals(landlord.getId())) {
            throw new RuntimeException("无权操作");
        }
        if (rental.getStatus() != 0) {
            throw new RuntimeException("当前状态不允许确认");
        }

        rental.setStatus(1); // 待支付
        rental.setUpdateTime(LocalDateTime.now());
        rentalMapper.updateById(rental);
        saveStatus(id, 1, landlordId, 2, "房东确认租赁");
    }

    @Override
    @Transactional
    public void reject(Long id, Long landlordId, String remark) {
        Landlord landlord = landlordMapper.selectOne(
                new LambdaQueryWrapper<Landlord>()
                        .eq(Landlord::getUserId, landlordId));
        
        if (landlord == null) {
            throw new RuntimeException("您还不是房东");
        }
        
        HouseRental rental = rentalMapper.selectById(id);
        if (rental == null) {
            throw new RuntimeException("租赁记录不存在");
        }
        if (!rental.getLandlordId().equals(landlord.getId())) {
            throw new RuntimeException("无权操作");
        }
        if (rental.getStatus() != 0) {
            throw new RuntimeException("当前状态不允许拒绝");
        }

        rental.setStatus(5); // 已拒绝
        if (remark != null) {
            rental.setRemark(
                    rental.getRemark() != null ? rental.getRemark() + " | 拒绝原因: " + remark : "拒绝原因: " + remark);
        }
        rental.setUpdateTime(LocalDateTime.now());
        rentalMapper.updateById(rental);
        saveStatus(id, 5, landlordId, 2, remark != null ? "拒绝原因: " + remark : "房东拒绝租赁");
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
        if (operatorType == 2) {
            Landlord landlord = landlordMapper.selectOne(
                    new LambdaQueryWrapper<Landlord>()
                            .eq(Landlord::getUserId, operatorId));
            
            if (landlord == null || !rental.getLandlordId().equals(landlord.getId())) {
                throw new RuntimeException("无权操作");
            }
        }

        if (rental.getStatus() != 2) {
            throw new RuntimeException("当前状态不允许完成");
        }

        rental.setStatus(3); // 已完成
        rental.setUpdateTime(LocalDateTime.now());
        rentalMapper.updateById(rental);

        House house = houseMapper.selectById(rental.getHouseId());
        if (house != null) {
            house.setStatus(1); // 可出租
            houseMapper.updateById(house);
        }

        saveStatus(id, 3, operatorId, operatorType, "完成租赁");
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
        if (rental.getStatus() == 2) {
            throw new RuntimeException("租赁中的订单请联系房东退租，无法直接取消");
        }
        if (rental.getStatus() != 0 && rental.getStatus() != 1) {
            throw new RuntimeException("当前状态不允许取消");
        }

        rental.setStatus(4);
        rental.setUpdateTime(LocalDateTime.now());
        rentalMapper.updateById(rental);
        saveStatus(id, 4, userId, 1, "用户取消租赁");
    }

    @Override
    @Transactional
    public void pay(Long id, Long userId) {
        HouseRental rental = rentalMapper.selectById(id);
        if (rental == null) {
            throw new RuntimeException("租赁记录不存在");
        }
        if (!rental.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }
        if (rental.getStatus() != 1) {
            throw new RuntimeException("当前状态不允许支付");
        }

        rental.setStatus(2); // 租赁中
        rental.setPayTime(LocalDateTime.now());
        rental.setUpdateTime(LocalDateTime.now());
        rentalMapper.updateById(rental);

        House house = houseMapper.selectById(rental.getHouseId());
        if (house != null) {
            house.setStatus(3); // 已出租
            houseMapper.updateById(house);
        }

        saveStatus(id, 2, userId, 1, "用户支付完成");
    }

    @Override
    public PageResult<HouseRental> listByUser(Long userId, Integer status, String statusList, Integer page, Integer size) {
        List<Integer> statuses = null;
        if (statusList != null && !statusList.isEmpty()) {
            statuses = Arrays.stream(statusList.split(","))
                    .map(Integer::parseInt).collect(Collectors.toList());
        }
        Page<HouseRental> pageResult = rentalMapper.selectRentalWithDetails(
                new Page<>(page, size), userId, null, status, statuses);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal());
    }

    @Override
    public PageResult<HouseRental> listByLandlord(Long landlordId, Integer status, String statusList, Integer page, Integer size) {
        Landlord landlord = landlordMapper.selectOne(
                new LambdaQueryWrapper<Landlord>()
                        .eq(Landlord::getUserId, landlordId));
        
        if (landlord == null) {
            return new PageResult<>(java.util.List.of(), 0L);
        }
        
        List<Integer> statuses = null;
        if (statusList != null && !statusList.isEmpty()) {
            statuses = Arrays.stream(statusList.split(","))
                    .map(Integer::parseInt).collect(Collectors.toList());
        }
        Page<HouseRental> pageResult = rentalMapper.selectRentalWithDetails(
                new Page<>(page, size), null, landlord.getId(), status, statuses);
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

    @Override
    public PageResult<HouseRental> listAll(Integer status, String statusList, Integer page, Integer size) {
        List<Integer> statuses = null;
        if (statusList != null && !statusList.isEmpty()) {
            statuses = Arrays.stream(statusList.split(","))
                    .map(Integer::parseInt).collect(Collectors.toList());
        }
        Page<HouseRental> pageResult = rentalMapper.selectRentalWithDetails(
                new Page<>(page, size), null, null, status, statuses);
        return new PageResult<>(pageResult.getRecords(), pageResult.getTotal());
    }
}
