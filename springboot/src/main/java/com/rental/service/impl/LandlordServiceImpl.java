package com.rental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rental.common.PageResult;
import com.rental.entity.Landlord;
import com.rental.entity.User;
import com.rental.mapper.LandlordMapper;
import com.rental.mapper.UserMapper;
import com.rental.service.LandlordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 房东服务实现
 */
@Service
public class LandlordServiceImpl extends ServiceImpl<LandlordMapper, Landlord> implements LandlordService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean apply(Landlord landlord) {
        // 检查是否已申请
        Landlord existing = getByUserId(landlord.getUserId());
        if (existing != null) {
            throw new RuntimeException("已提交申请，请勿重复提交");
        }
        landlord.setVerifyStatus(0); // 待审核
        return save(landlord);
    }

    @Override
    @Transactional
    public boolean audit(Long id, Integer status, String remark) {
        Landlord landlord = getById(id);
        if (landlord == null) {
            throw new RuntimeException("房东申请不存在");
        }

        landlord.setVerifyStatus(status);
        landlord.setVerifyRemark(remark);
        boolean updated = updateById(landlord);

        // 如果审核通过，更新用户角色为房东
        if (updated && status == 1) {
            User user = new User();
            user.setId(landlord.getUserId());
            user.setRole(2); // 房东角色
            userMapper.updateById(user);
        }

        return updated;
    }

    @Override
    public Landlord getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<Landlord>()
                .eq(Landlord::getUserId, userId));
    }

    @Override
    public PageResult<Landlord> listPage(Integer page, Integer size, Integer status, String keyword) {
        Page<Landlord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Landlord> wrapper = new LambdaQueryWrapper<>();

        if (status != null) {
            wrapper.eq(Landlord::getVerifyStatus, status);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Landlord::getRealName, keyword)
                    .or().like(Landlord::getContact, keyword));
        }
        wrapper.orderByDesc(Landlord::getCreateTime);

        Page<Landlord> result = page(pageParam, wrapper);
        return new PageResult<>(result.getTotal(), result.getPages(),
                result.getCurrent(), result.getSize(), result.getRecords());
    }
}
