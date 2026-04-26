package com.rental.service;

import com.rental.common.PageResult;
import com.rental.entity.HouseRental;

/**
 * 房屋租赁服务接口
 */
public interface HouseRentalService {

    /**
     * 创建租赁申请
     */
    void createRental(HouseRental rental);

    /**
     * 房东确认租赁
     */
    void confirm(Long id, Long landlordId);

    /**
     * 房东拒绝租赁
     */
    void reject(Long id, Long landlordId, String remark);

    /**
     * 完成租赁
     */
    void complete(Long id, Long operatorId, int operatorType);

    /**
     * 取消租赁
     */
    void cancel(Long id, Long userId);

    void pay(Long id, Long userId);

    /**
     * 用户租赁列表
     */
    PageResult<HouseRental> listByUser(Long userId, Integer status, String statusList, Integer page, Integer size);

    /**
     * 房东租赁列表
     */
    PageResult<HouseRental> listByLandlord(Long landlordId, Integer status, String statusList, Integer page, Integer size);

    /**
     * 根据ID获取租赁详情
     */
    HouseRental getById(Long id);

    PageResult<HouseRental> listAll(Integer status, String statusList, Integer page, Integer size);
}
