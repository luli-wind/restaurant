package com.sz.admin.system.controller;

import com.sz.admin.system.pojo.dto.ThirdPartyLoginDTO;
import com.sz.admin.system.pojo.vo.ThirdPartyLoginVO;
import com.sz.admin.system.service.AlipayService;
import com.sz.admin.system.service.ThirdPartyUserService;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.enums.CommonResponseEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 第三方登录 Controller
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-30
 */
@Tag(name = "第三方登录")
@RestController
@RequestMapping("/third-party-login")
@RequiredArgsConstructor
public class ThirdPartyLoginController {

    private final ThirdPartyUserService thirdPartyUserService;
    private final AlipayService alipayService;

    @Operation(summary = "第三方登录")
    @PostMapping
    public ApiResult<ThirdPartyLoginVO> thirdPartyLogin(@RequestBody ThirdPartyLoginDTO dto) {
        ThirdPartyLoginVO result = thirdPartyUserService.thirdPartyLogin(dto);
        return ApiResult.success(result);
    }

    @Operation(summary = "支付宝登录")
    @PostMapping("/alipay")
    public ApiResult<ThirdPartyLoginVO> alipayLogin(@RequestParam String authCode) {
        try {
            // 使用授权码获取支付宝用户信息
            Map<String, String> userInfo = alipayService.getUserInfo(authCode);
            
            // 构造第三方登录DTO
            ThirdPartyLoginDTO dto = new ThirdPartyLoginDTO();
            dto.setOpenId(userInfo.get("user_id"));
            dto.setProvider("alipay");
            dto.setNickname(userInfo.get("nick_name"));
            dto.setAvatarUrl(userInfo.get("avatar"));
            dto.setPhone(userInfo.get("mobile"));
            dto.setEmail(userInfo.get("email"));
            
            // 调用第三方登录服务
            ThirdPartyLoginVO result = thirdPartyUserService.thirdPartyLogin(dto);
            return ApiResult.success(result);
        } catch (Exception e) {
            return ApiResult.error(CommonResponseEnum.BACKGROUND_NOT_EXISTS);
        }
    }
}