package com.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.entity.HouseRental;
import org.apache.ibatis.annotations.Mapper;

/**
 * 房屋租赁 Mapper
 */
@Mapper
public interface HouseRentalMapper extends BaseMapper<HouseRental> {
}
