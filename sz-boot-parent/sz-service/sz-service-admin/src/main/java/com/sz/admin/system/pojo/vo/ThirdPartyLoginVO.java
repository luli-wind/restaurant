package com.sz.admin.system.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * 第三方登录返回VO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-30
 */
@Data
@Schema(description = "第三方登录返回VO")
public class ThirdPartyLoginVO {

    @Schema(description = "第三方用户ID")
    private Long id;

    @Schema(description = "第三方用户唯一标识")
    private String openId;

    @Schema(description = "第三方平台（如：wechat、alipay）")
    private String provider;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像URL")
    private String avatarUrl;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "是否为新用户")
    private Boolean isNewUser;
}