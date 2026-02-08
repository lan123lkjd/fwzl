package com.rental.service.algorithm;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rental.entity.House;
import com.rental.entity.UserBrowseHistory;
import com.rental.entity.UserCollect;
import com.rental.mapper.HouseMapper;
import com.rental.mapper.UserBrowseHistoryMapper;
import com.rental.mapper.UserCollectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 协同过滤推荐算法服务
 * 
 * 基于用户的协同过滤算法:
 * 1. 收集用户行为数据（浏览、收藏）
 * 2. 计算用户相似度（余弦相似度）
 * 3. 找到相似用户喜欢的房源
 * 4. 推荐给目标用户
 */
@Service
public class CollaborativeFilteringService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private UserBrowseHistoryMapper browseHistoryMapper;

    @Autowired
    private UserCollectMapper collectMapper;

    /**
     * 为用户推荐房源
     */
    public List<House> recommendHouses(Long userId, Integer limit) {
        // 1. 获取目标用户的行为数据
        Set<Long> targetUserHouses = getUserInteractedHouses(userId);

        if (targetUserHouses.isEmpty()) {
            // 如果用户没有行为数据，返回热门房源
            return getHotHouses(limit);
        }

        // 2. 获取所有用户的行为数据
        Map<Long, Set<Long>> allUserBehaviors = getAllUserBehaviors();

        // 3. 计算与目标用户的相似度
        Map<Long, Double> similarityScores = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> entry : allUserBehaviors.entrySet()) {
            Long otherUserId = entry.getKey();
            if (!otherUserId.equals(userId)) {
                double similarity = calculateCosineSimilarity(targetUserHouses, entry.getValue());
                if (similarity > 0) {
                    similarityScores.put(otherUserId, similarity);
                }
            }
        }

        // 4. 获取相似用户喜欢但目标用户未浏览的房源
        Map<Long, Double> houseScores = new HashMap<>();
        for (Map.Entry<Long, Double> entry : similarityScores.entrySet()) {
            Long similarUserId = entry.getKey();
            Double similarity = entry.getValue();
            Set<Long> similarUserHouses = allUserBehaviors.get(similarUserId);

            for (Long houseId : similarUserHouses) {
                if (!targetUserHouses.contains(houseId)) {
                    houseScores.merge(houseId, similarity, Double::sum);
                }
            }
        }

        // 5. 按得分排序，返回top N
        List<Long> recommendedHouseIds = houseScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (recommendedHouseIds.isEmpty()) {
            return getHotHouses(limit);
        }

        return houseMapper.selectList(new LambdaQueryWrapper<House>()
                .in(House::getId, recommendedHouseIds)
                .eq(House::getStatus, 1));
    }

    /**
     * 获取用户交互过的房源ID（浏览+收藏）
     */
    private Set<Long> getUserInteractedHouses(Long userId) {
        Set<Long> houseIds = new HashSet<>();

        // 浏览记录
        List<UserBrowseHistory> browseList = browseHistoryMapper.selectList(
                new LambdaQueryWrapper<UserBrowseHistory>()
                        .eq(UserBrowseHistory::getUserId, userId));
        houseIds.addAll(browseList.stream()
                .map(UserBrowseHistory::getHouseId)
                .collect(Collectors.toSet()));

        // 收藏记录
        List<UserCollect> collectList = collectMapper.selectList(
                new LambdaQueryWrapper<UserCollect>()
                        .eq(UserCollect::getUserId, userId));
        houseIds.addAll(collectList.stream()
                .map(UserCollect::getHouseId)
                .collect(Collectors.toSet()));

        return houseIds;
    }

    /**
     * 获取所有用户的行为数据
     */
    private Map<Long, Set<Long>> getAllUserBehaviors() {
        Map<Long, Set<Long>> behaviors = new HashMap<>();

        // 浏览记录
        List<UserBrowseHistory> allBrowse = browseHistoryMapper.selectList(null);
        for (UserBrowseHistory browse : allBrowse) {
            behaviors.computeIfAbsent(browse.getUserId(), k -> new HashSet<>())
                    .add(browse.getHouseId());
        }

        // 收藏记录（权重更高，这里简化处理，同等对待）
        List<UserCollect> allCollect = collectMapper.selectList(null);
        for (UserCollect collect : allCollect) {
            behaviors.computeIfAbsent(collect.getUserId(), k -> new HashSet<>())
                    .add(collect.getHouseId());
        }

        return behaviors;
    }

    /**
     * 计算余弦相似度
     */
    private double calculateCosineSimilarity(Set<Long> set1, Set<Long> set2) {
        if (set1.isEmpty() || set2.isEmpty()) {
            return 0.0;
        }

        // 计算交集大小
        Set<Long> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        if (intersection.isEmpty()) {
            return 0.0;
        }

        // 余弦相似度 = 交集大小 / sqrt(|A| * |B|)
        return intersection.size() / Math.sqrt(set1.size() * set2.size());
    }

    /**
     * 获取热门房源（降级方案）
     */
    private List<House> getHotHouses(Integer limit) {
        return houseMapper.selectList(new LambdaQueryWrapper<House>()
                .eq(House::getStatus, 1)
                .orderByDesc(House::getViewCount)
                .last("LIMIT " + limit));
    }
}
