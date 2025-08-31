package com.sz.admin.system.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.mybatisflex.core.query.QueryWrapper;
import com.sz.admin.system.pojo.dto.ThirdPartyLoginDTO;
import com.sz.admin.system.pojo.po.ThirdPartyUser;
import com.sz.admin.system.mapper.ThirdPartyUserMapper;
import com.sz.admin.system.pojo.vo.ThirdPartyLoginVO;
import com.sz.admin.system.service.ThirdPartyUserService;
import com.sz.core.util.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.sz.admin.system.pojo.po.table.ThirdPartyUserTableDef.THIRD_PARTY_USER;

/**
 * <p>
 * 第三方用户表 服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-30
 */
@Service
@RequiredArgsConstructor
public class ThirdPartyUserServiceImpl extends ServiceImpl<ThirdPartyUserMapper, ThirdPartyUser> implements ThirdPartyUserService {

    @Override
    public ThirdPartyUser getByOpenIdAndProvider(String openId, String provider) {
        QueryWrapper wrapper = QueryWrapper.create()
                .eq(ThirdPartyUser::getOpenId, openId)
                .eq(ThirdPartyUser::getProvider, provider);
        return getOne(wrapper);
    }

    @Override
    public ThirdPartyUser createOrUpdate(ThirdPartyUser thirdPartyUser) {
        ThirdPartyUser existingUser = getByOpenIdAndProvider(thirdPartyUser.getOpenId(), thirdPartyUser.getProvider());
        if (existingUser != null) {
            // 更新现有用户信息
            thirdPartyUser.setId(existingUser.getId());
            updateById(thirdPartyUser);
            return thirdPartyUser;
        } else {
            // 创建新用户
            save(thirdPartyUser);
            return thirdPartyUser;
        }
    }

    @Override
    public ThirdPartyLoginVO thirdPartyLogin(ThirdPartyLoginDTO dto) {
        // 创建或更新第三方用户
        ThirdPartyUser thirdPartyUser = new ThirdPartyUser();
        thirdPartyUser.setOpenId(dto.getOpenId());
        thirdPartyUser.setProvider(dto.getProvider());
        thirdPartyUser.setNickname(dto.getNickname());
        thirdPartyUser.setAvatarUrl(dto.getAvatarUrl());
        thirdPartyUser.setPhone(dto.getPhone());
        thirdPartyUser.setEmail(dto.getEmail());
        
        // 判断是否为新用户
        ThirdPartyUser existingUser = getByOpenIdAndProvider(dto.getOpenId(), dto.getProvider());
        boolean isNewUser = existingUser == null;
        
        // 创建或更新用户
        thirdPartyUser = createOrUpdate(thirdPartyUser);
        
        // 构造返回结果
        ThirdPartyLoginVO result = BeanCopyUtils.copy(thirdPartyUser, ThirdPartyLoginVO.class);
        result.setIsNewUser(isNewUser);
        
        return result;
    }
}