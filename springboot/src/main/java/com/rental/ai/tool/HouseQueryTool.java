package com.rental.ai.tool;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 房源查询工具 - MCP Tool
 * 供 AI 调用以查询数据库中的房源信息
 */
@Component
@Slf4j
public class HouseQueryTool {

    private final JdbcTemplate jdbcTemplate;

    public HouseQueryTool(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Tool("根据用户的租房需求查询房源信息。可根据区域、价格范围、房间数、面积等条件筛选房源。返回符合条件的房源列表。所有参数都是可选的，不需要的参数传空字符串即可。")
    public String searchHouses(
            @P("区域名称，如朝阳区、海淀区、浦东新区等，不限制则传空字符串") String areaName,
            @P("最低月租金（元），不限制则传空字符串") String minPrice,
            @P("最高月租金（元），不限制则传空字符串") String maxPrice,
            @P("房间数量，不限制则传空字符串") String rooms,
            @P("最小面积（平方米），不限制则传空字符串") String minArea,
            @P("房屋类型：1-整租，2-合租，不限制则传空字符串") String houseType) {

        log.info("AI调用房源查询工具: areaName={}, minPrice={}, maxPrice={}, rooms={}, minArea={}, houseType={}",
                areaName, minPrice, maxPrice, rooms, minArea, houseType);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT h.id, h.title, h.price, h.area AS house_area, h.rooms, h.halls, h.bathrooms, ");
        sql.append("h.floor, h.total_floor, h.orientation, h.decoration, h.house_type, h.address, h.description, ");
        sql.append("c.name AS community_name, a.name AS area_name ");
        sql.append("FROM house h ");
        sql.append("LEFT JOIN community c ON h.community_id = c.id ");
        sql.append("LEFT JOIN area a ON h.area_id = a.id ");
        sql.append("WHERE h.status = 1 AND h.deleted = 0 ");

        List<Object> params = new ArrayList<>();

        // 区域筛选
        if (isNotEmpty(areaName)) {
            sql.append("AND a.name LIKE ? ");
            params.add("%" + areaName.trim() + "%");
        }

        // 价格范围
        Double minPriceVal = parseDouble(minPrice);
        Double maxPriceVal = parseDouble(maxPrice);
        if (minPriceVal != null) {
            sql.append("AND h.price >= ? ");
            params.add(minPriceVal);
        }
        if (maxPriceVal != null) {
            sql.append("AND h.price <= ? ");
            params.add(maxPriceVal);
        }

        // 房间数
        Integer roomsVal = parseInteger(rooms);
        if (roomsVal != null) {
            sql.append("AND h.rooms = ? ");
            params.add(roomsVal);
        }

        // 面积
        Double minAreaVal = parseDouble(minArea);
        if (minAreaVal != null) {
            sql.append("AND h.area >= ? ");
            params.add(minAreaVal);
        }

        // 房屋类型
        Integer houseTypeVal = parseInteger(houseType);
        if (houseTypeVal != null) {
            sql.append("AND h.house_type = ? ");
            params.add(houseTypeVal);
        }

        sql.append("ORDER BY h.view_count DESC LIMIT 10");

        log.info("执行SQL: {}", sql);
        log.info("参数: {}", params);

        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), params.toArray());

            if (results.isEmpty()) {
                return "抱歉，未找到符合条件的房源。您可以尝试放宽筛选条件。";
            }

            StringBuilder response = new StringBuilder();
            response.append("为您找到 ").append(results.size()).append(" 套符合条件的房源：\n\n");

            for (int i = 0; i < results.size(); i++) {
                Map<String, Object> house = results.get(i);
                response.append("【").append(i + 1).append("】").append(house.get("title")).append("\n");
                response.append("   📍 位置：").append(house.get("area_name"));
                if (house.get("community_name") != null) {
                    response.append(" - ").append(house.get("community_name"));
                }
                response.append("\n");
                response.append("   💰 租金：").append(house.get("price")).append("元/月\n");
                response.append("   🏠 户型：").append(house.get("rooms")).append("室");
                response.append(house.get("halls")).append("厅");
                response.append(house.get("bathrooms")).append("卫\n");
                response.append("   📐 面积：").append(house.get("house_area")).append("㎡\n");
                response.append("   🧭 朝向：").append(house.get("orientation"));
                response.append(" | 装修：").append(house.get("decoration")).append("\n");
                response.append("   🏢 楼层：").append(house.get("floor")).append("/").append(house.get("total_floor"))
                        .append("层\n");

                Object typeObj = house.get("house_type");
                int type = typeObj instanceof Number ? ((Number) typeObj).intValue() : 1;
                response.append("   📋 类型：").append(type == 1 ? "整租" : "合租").append("\n");
                response.append("\n");
            }

            return response.toString();
        } catch (Exception e) {
            log.error("查询房源失败", e);
            return "查询房源时发生错误，请稍后重试。错误信息：" + e.getMessage();
        }
    }

    @Tool("获取所有可用的地区列表，帮助用户了解有哪些区域可以选择")
    public String getAvailableAreas() {
        log.info("AI调用获取地区列表工具");

        String sql = "SELECT DISTINCT a.name FROM area a " +
                "INNER JOIN house h ON h.area_id = a.id " +
                "WHERE h.status = 1 AND h.deleted = 0 " +
                "ORDER BY a.name";

        try {
            List<String> areas = jdbcTemplate.queryForList(sql, String.class);

            if (areas.isEmpty()) {
                return "当前暂无可用房源区域。";
            }

            return "当前有房源的区域：" + String.join("、", areas);
        } catch (Exception e) {
            log.error("获取地区列表失败", e);
            return "获取地区列表失败：" + e.getMessage();
        }
    }

    @Tool("获取指定房源的详细信息，包括描述、配套设施等")
    public String getHouseDetail(@P("房源ID") String houseIdStr) {
        log.info("AI调用获取房源详情工具: houseId={}", houseIdStr);

        Long houseId = parseLong(houseIdStr);
        if (houseId == null) {
            return "请提供有效的房源ID。";
        }

        String sql = "SELECT h.*, c.name AS community_name, c.property_company, c.property_fee, " +
                "a.name AS area_name, l.real_name AS landlord_name, l.contact AS landlord_contact " +
                "FROM house h " +
                "LEFT JOIN community c ON h.community_id = c.id " +
                "LEFT JOIN area a ON h.area_id = a.id " +
                "LEFT JOIN landlord l ON h.landlord_id = l.id " +
                "WHERE h.id = ? AND h.deleted = 0";

        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, houseId);

            if (results.isEmpty()) {
                return "未找到该房源信息。";
            }

            Map<String, Object> house = results.get(0);
            StringBuilder response = new StringBuilder();
            response.append("🏠 房源详情：").append(house.get("title")).append("\n\n");
            response.append("📍 地址：").append(house.get("area_name")).append(" ").append(house.get("address"))
                    .append("\n");
            response.append("💰 月租金：").append(house.get("price")).append("元\n");
            response.append("📐 面积：").append(house.get("area")).append("㎡\n");
            response.append("🏠 户型：").append(house.get("rooms")).append("室")
                    .append(house.get("halls")).append("厅")
                    .append(house.get("bathrooms")).append("卫\n");
            response.append("🧭 朝向：").append(house.get("orientation")).append("\n");
            response.append("🎨 装修：").append(house.get("decoration")).append("\n");
            response.append("🏢 楼层：").append(house.get("floor")).append("/").append(house.get("total_floor"))
                    .append("层\n");

            if (house.get("community_name") != null) {
                response.append("\n📌 小区信息：\n");
                response.append("   小区名称：").append(house.get("community_name")).append("\n");
                if (house.get("property_company") != null) {
                    response.append("   物业公司：").append(house.get("property_company")).append("\n");
                }
                if (house.get("property_fee") != null) {
                    response.append("   物业费：").append(house.get("property_fee")).append("元/月/㎡\n");
                }
            }

            response.append("\n📝 房源描述：\n").append(house.get("description")).append("\n");

            response.append("\n👤 联系房东：").append(house.get("landlord_name"));
            response.append(" 电话：").append(house.get("landlord_contact")).append("\n");

            return response.toString();
        } catch (Exception e) {
            log.error("获取房源详情失败", e);
            return "获取房源详情失败：" + e.getMessage();
        }
    }

    // ========== 辅助方法 ==========

    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private Double parseDouble(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long parseLong(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
