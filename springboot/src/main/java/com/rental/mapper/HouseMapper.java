package com.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.entity.House;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HouseMapper extends BaseMapper<House> {

    @Select("SELECT DISTINCT h.id FROM house h " +
            "INNER JOIN user_browse_history ubh ON h.id = ubh.house_id " +
            "WHERE ubh.user_id = #{userId} AND h.deleted = 0 AND h.status = 1")
    List<Long> findBrowsedHouseIdsByUserId(@Param("userId") Long userId);

    @Select("SELECT DISTINCT h.id FROM house h " +
            "INNER JOIN user_collect uc ON h.id = uc.house_id " +
            "WHERE uc.user_id = #{userId} AND h.deleted = 0 AND h.status = 1")
    List<Long> findCollectedHouseIdsByUserId(@Param("userId") Long userId);
}
