package com.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.entity.HouseRental;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 房屋租赁 Mapper
 */
@Mapper
public interface HouseRentalMapper extends BaseMapper<HouseRental> {

    @Select("<script>" +
            "SELECT r.*, h.title as houseTitle, h.price as housePrice, " +
            "l.real_name as landlordName, u.username " +
            "FROM house_rental r " +
            "LEFT JOIN house h ON r.house_id = h.id " +
            "LEFT JOIN landlord l ON r.landlord_id = l.id " +
            "LEFT JOIN user u ON r.user_id = u.id " +
            "WHERE r.deleted = 0 " +
            "<if test='userId != null'> AND r.user_id = #{userId} </if>" +
            "<if test='landlordId != null'> AND r.landlord_id = #{landlordId} </if>" +
            "<if test='status != null'> AND r.status = #{status} </if>" +
            "<if test='statusList != null and statusList.size() > 0'> " +
            " AND r.status IN <foreach item='s' collection='statusList' open='(' separator=',' close=')'>#{s}</foreach> " +
            "</if>" +
            "ORDER BY r.create_time DESC " +
            "</script>")
    Page<HouseRental> selectRentalWithDetails(Page<HouseRental> page, 
            @Param("userId") Long userId, 
            @Param("landlordId") Long landlordId, 
            @Param("status") Integer status, 
            @Param("statusList") List<Integer> statusList);
}
