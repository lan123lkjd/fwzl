package com.rental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rental.entity.Evaluations;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EvaluationsMapper extends BaseMapper<Evaluations> {

    @Select("SELECT e.*, u.username, h.title as houseTitle " +
            "FROM evaluations e " +
            "LEFT JOIN user u ON e.user_id = u.id " +
            "LEFT JOIN house h ON e.house_id = h.id " +
            "WHERE e.house_id = #{houseId} AND e.deleted = 0 " +
            "ORDER BY e.create_time DESC")
    Page<Evaluations> selectEvaluationsWithDetails(Page<Evaluations> page, @Param("houseId") Long houseId);
}
