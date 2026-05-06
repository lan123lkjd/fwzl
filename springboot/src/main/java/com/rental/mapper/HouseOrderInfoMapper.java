package com.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rental.entity.HouseOrderInfo;
import org.apache.ibatis.annotations.Mapper;
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
            "o.house_id as houseId, h.title as houseTitle, u.username " +
            "FROM house_order_info o " +
            "LEFT JOIN house h ON o.house_id = h.id " +
            "LEFT JOIN user u ON o.user_id = u.id " +
            "WHERE o.landlord_id = #{landlordId} AND o.rating IS NOT NULL AND o.deleted = 0 " +
            "ORDER BY o.evaluation_time DESC LIMIT #{limit}")
    List<Map<String, Object>> getEvaluationsByLandlordId(Long landlordId, Integer limit);
}
