package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.common.PageResult;
import com.rental.entity.Landlord;

/**
 * 房东服务接口
 */
public interface LandlordService extends IService<Landlord> {

    /**
     * 申请成为房东
     */
    boolean apply(Landlord landlord);

    /**
     * 审核房东
     */
    boolean audit(Long id, Integer status, String remark);

    /**
     * 根据用户ID获取房东信息
     */
    Landlord getByUserId(Long userId);

    /**
     * 分页查询房东列表
     */
    PageResult<Landlord> listPage(Integer page, Integer size, Integer status, String keyword);
}
