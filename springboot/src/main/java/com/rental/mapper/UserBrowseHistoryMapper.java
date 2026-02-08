package com.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.entity.UserBrowseHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserBrowseHistoryMapper extends BaseMapper<UserBrowseHistory> {

    @Select("SELECT user_id FROM user_browse_history WHERE house_id = #{houseId}")
    List<Long> findUserIdsByHouseId(@Param("houseId") Long houseId);
}
