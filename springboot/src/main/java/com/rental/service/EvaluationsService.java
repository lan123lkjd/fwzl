package com.rental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rental.common.PageResult;
import com.rental.entity.Evaluations;

import java.util.List;

/**
 * 评论服务接口
 */
public interface EvaluationsService extends IService<Evaluations> {

    /**
     * 发表评论（带敏感词过滤）
     */
    boolean publish(Evaluations evaluations);

    /**
     * 获取房源评论列表
     */
    PageResult<Evaluations> listByHouse(Long houseId, Integer page, Integer size);

    /**
     * 获取评论及其回复
     */
    List<Evaluations> getWithReplies(Long houseId);

    /**
     * 点赞
     */
    boolean upvote(Long evaluationId, Long userId);

    /**
     * 取消点赞
     */
    boolean cancelUpvote(Long evaluationId, Long userId);

    /**
     * 检查是否已点赞
     */
    boolean hasUpvoted(Long evaluationId, Long userId);

    /**
     * 删除评论
     */
    boolean deleteByUser(Long id, Long userId);

    /**
     * 管理员获取所有评论列表（分页）
     */
    PageResult<Evaluations> listAll(Integer page, Integer size, String keyword);
}
