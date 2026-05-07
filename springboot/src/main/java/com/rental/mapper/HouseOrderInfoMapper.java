package com.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.entity.HouseOrderInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface HouseOrderInfoMapper extends BaseMapper<HouseOrderInfo> {

    @Select("SELECT AVG(rating) FROM house_order_info WHERE landlord_id = #{landlordId} AND rating IS NOT NULL AND deleted = 0")
    Double getAvgRatingByLandlordId(Long landlordId);

    @Select("SELECT COUNT(*) FROM house_order_info WHERE landlord_id = #{landlordId} AND rating IS NOT NULL AND deleted = 0")
    Integer getRatingCountByLandlordId(Long landlordId);

    @Select("SELECT o.id, o.rating, o.evaluation_content as evaluationContent, o.evaluation_time as evaluationTime, " +
            "u.username " +
            "FROM house_order_info o " +
            "LEFT JOIN user u ON o.user_id = u.id " +
            "WHERE o.landlord_id = #{landlordId} AND o.rating IS NOT NULL AND o.deleted = 0 " +
            "ORDER BY o.evaluation_time DESC LIMIT #{limit}")
    List<Map<String, Object>> getEvaluationsByLandlordId(Long landlordId, Integer limit);

    @Select("<script>" +
            "SELECT o.*, h.title as houseTitle, u.username, l.real_name as landlordName " +
            "FROM house_order_info o " +
            "LEFT JOIN house h ON o.house_id = h.id " +
            "LEFT JOIN user u ON o.user_id = u.id " +
            "LEFT JOIN landlord l ON o.landlord_id = l.id " +
            "WHERE o.deleted = 0 " +
            "<if test='userId != null'> AND o.user_id = #{userId} </if>" +
            "<if test='landlordId != null'> AND o.landlord_id = #{landlordId} </if>" +
            "<if test='status != null'> AND o.status = #{status} </if>" +
            "ORDER BY o.create_time DESC " +
            "</script>")
    Page<HouseOrderInfo> selectOrderWithDetails(Page<HouseOrderInfo> page,
            @Param("userId") Long userId,
            @Param("landlordId") Long landlordId,
            @Param("status") Integer status);
}
