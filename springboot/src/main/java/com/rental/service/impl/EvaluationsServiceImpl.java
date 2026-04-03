package com.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.common.PageResult;
import com.rental.entity.Evaluations;
import com.rental.entity.EvaluationsUpvote;
import com.rental.mapper.EvaluationsMapper;
import com.rental.mapper.EvaluationsUpvoteMapper;
import com.rental.service.EvaluationsService;
import com.rental.utils.TrieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论服务实现
 */
@Service
public class EvaluationsServiceImpl extends ServiceImpl<EvaluationsMapper, Evaluations> implements EvaluationsService {

    @Autowired
    private EvaluationsUpvoteMapper upvoteMapper;

    private final TrieUtil trieUtil = new TrieUtil();

    @Override
    public boolean publish(Evaluations evaluations) {
        // 敏感词过滤
        String filteredContent = trieUtil.filter(evaluations.getContent());
        evaluations.setContent(filteredContent);
        evaluations.setUpvoteCount(0);
        return save(evaluations);
    }

    @Override
    public PageResult<Evaluations> listByHouse(Long houseId, Integer page, Integer size) {
        Page<Evaluations> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Evaluations> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Evaluations::getHouseId, houseId)
                .eq(Evaluations::getParentId, 0) // 只查询顶级评论
                .orderByDesc(Evaluations::getCreateTime);
        Page<Evaluations> result = page(pageParam, wrapper);

        return new PageResult<>(result.getTotal(), result.getPages(),
                result.getCurrent(), result.getSize(), result.getRecords());
    }

    @Override
    public List<Evaluations> getWithReplies(Long houseId) {
        return list(new LambdaQueryWrapper<Evaluations>()
                .eq(Evaluations::getHouseId, houseId)
                .orderByAsc(Evaluations::getCreateTime));
    }

    @Override
    @Transactional
    public boolean upvote(Long evaluationId, Long userId) {
        // 检查是否已点赞
        Long count = upvoteMapper.selectCount(new LambdaQueryWrapper<EvaluationsUpvote>()
                .eq(EvaluationsUpvote::getEvaluationId, evaluationId)
                .eq(EvaluationsUpvote::getUserId, userId));

        if (count > 0) {
            throw new RuntimeException("已点赞");
        }

        // 添加点赞记录
        EvaluationsUpvote upvote = new EvaluationsUpvote();
        upvote.setEvaluationId(evaluationId);
        upvote.setUserId(userId);
        upvoteMapper.insert(upvote);

        // 增加点赞数
        Evaluations evaluations = getById(evaluationId);
        evaluations.setUpvoteCount(evaluations.getUpvoteCount() + 1);
        return updateById(evaluations);
    }

    @Override
    @Transactional
    public boolean cancelUpvote(Long evaluationId, Long userId) {
        // 删除点赞记录
        int deleted = upvoteMapper.delete(new LambdaQueryWrapper<EvaluationsUpvote>()
                .eq(EvaluationsUpvote::getEvaluationId, evaluationId)
                .eq(EvaluationsUpvote::getUserId, userId));

        if (deleted > 0) {
            // 减少点赞数
            Evaluations evaluations = getById(evaluationId);
            evaluations.setUpvoteCount(Math.max(0, evaluations.getUpvoteCount() - 1));
            return updateById(evaluations);
        }
        return false;
    }

    @Override
    public boolean hasUpvoted(Long evaluationId, Long userId) {
        Long count = upvoteMapper.selectCount(new LambdaQueryWrapper<EvaluationsUpvote>()
                .eq(EvaluationsUpvote::getEvaluationId, evaluationId)
                .eq(EvaluationsUpvote::getUserId, userId));
        return count > 0;
    }

    @Override
    public boolean deleteByUser(Long id, Long userId) {
        Evaluations evaluations = getById(id);
        if (evaluations == null || !evaluations.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除");
        }
        return removeById(id);
    }

    @Override
    public PageResult<Evaluations> listAll(Integer page, Integer size, String keyword) {
        Page<Evaluations> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Evaluations> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Evaluations::getContent, keyword.trim());
        }
        wrapper.orderByDesc(Evaluations::getCreateTime);
        Page<Evaluations> result = page(pageParam, wrapper);
        return new PageResult<>(result.getTotal(), result.getPages(),
                result.getCurrent(), result.getSize(), result.getRecords());
    }
}
