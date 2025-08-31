package com.sz.admin.system.service;

import com.mybatisflex.core.service.IService;
import com.sz.admin.system.pojo.dto.ThirdPartyLoginDTO;
import com.sz.admin.system.pojo.po.ThirdPartyUser;
import com.sz.admin.system.pojo.vo.ThirdPartyLoginVO;

/**
 * <p>
 * 第三方用户表 服务类
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-30
 */
public interface ThirdPartyUserService extends IService<ThirdPartyUser> {

    /**
     * 根据openId和provider获取第三方用户
     * @param openId 第三方用户唯一标识
     * @param provider 第三方平台
     * @return 第三方用户信息
     */
    ThirdPartyUser getByOpenIdAndProvider(String openId, String provider);

    /**
     * 创建或更新第三方用户
     * @param thirdPartyUser 第三方用户信息
     * @return 第三方用户信息
     */
    ThirdPartyUser createOrUpdate(ThirdPartyUser thirdPartyUser);

    /**
     * 第三方登录
     * @param dto 第三方登录信息
     * @return 登录结果
     */
    ThirdPartyLoginVO thirdPartyLogin(ThirdPartyLoginDTO dto);
}