package com.sz.admin.system.service;

import java.util.Map;

/**
 * <p>
 * 支付宝服务接口
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-30
 */
public interface AlipayService {

    /**
     * 根据授权码获取用户信息
     * @param authCode 授权码
     * @return 用户信息
     * @throws Exception 异常
     */
    Map<String, String> getUserInfo(String authCode) throws Exception;
}