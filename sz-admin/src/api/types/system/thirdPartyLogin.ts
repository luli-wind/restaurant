export interface ThirdPartyLoginDTO {
    provider: string; // 第三方平台类型 (wechat, alipay)
    code?: string;    // 授权码
    token?: string;   // 第三方平台token
    openId?: string;  // 第三方用户唯一标识
    nickname?: string; // 用户昵称
    avatarUrl?: string; // 用户头像URL
    phone?: string;    // 手机号
    email?: string;    // 邮箱
}

export interface ThirdPartyLoginVO {
    userId: string;        // 用户ID
    token: string;         // 系统token
    nickname?: string;     // 昵称
    avatar?: string;       // 头像
    provider: string;      // 第三方平台类型
}