package com.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.entity.UserCollect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserCollectMapper extends BaseMapper<UserCollect> {

    @Select("SELECT user_id FROM user_collect WHERE house_id = #{houseId}")
    List<Long> findUserIdsByHouseId(@Param("houseId") Long houseId);
}
