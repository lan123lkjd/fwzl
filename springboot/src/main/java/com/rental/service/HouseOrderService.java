package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.common.PageResult;
import com.rental.entity.HouseOrderInfo;

/**
 * 预约看房服务接口
 */
public interface HouseOrderService extends IService<HouseOrderInfo> {

    /**
     * 创建预约
     */
    boolean createOrder(HouseOrderInfo order);

    /**
     * 确认预约（房东）
     */
    boolean confirm(Long id, Long landlordId);

    /**
     * 拒绝预约（房东）
     */
    boolean reject(Long id, Long landlordId, String remark);

    /**
     * 完成预约
     */
    boolean complete(Long id, Long operatorId, Integer operatorType);

    /**
     * 取消预约（用户）
     */
    boolean cancel(Long id, Long userId);

    /**
     * 用户预约列表
     */
    PageResult<HouseOrderInfo> listByUser(Long userId, Integer status, Integer page, Integer size);

    /**
     * 房东预约列表
     */
    PageResult<HouseOrderInfo> listByLandlord(Long landlordId, Integer status, Integer page, Integer size);

    /**
     * 管理员查询所有预约
     */
    PageResult<HouseOrderInfo> listAll(Integer status, Integer page, Integer size);
}
