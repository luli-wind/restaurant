package com.sz.admin.system.service.impl;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.base.oauth.Client;
import com.alipay.easysdk.base.oauth.models.AlipaySystemOauthTokenResponse;
import com.sz.admin.system.service.AlipayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付宝服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-30
 */
@Slf4j
@Service
public class AlipayServiceImpl implements AlipayService {

    @PostConstruct
    public void init() {
        // 初始化支付宝SDK配置
        Config config = new Config();
        config.protocol = "https";
        config.gatewayHost = "openapi.alipay.com";
        config.signType = "RSA2";
        config.appId = "2021005187631856";
        config.merchantPrivateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCWOURkVHxMd0CLfHhAYYt/PdWEkt+8gz5oS5585r+RPxYdJ0GWnKLMQoBd/ctuIQ4xf8YG1OcgqrhF2RRW5x5Vac40SQC/ci9p0SuXBpcQoFv4ZlJbSDSCo5opThVwrrT4tH+mH5x38Op0qjnMlxmBCMUnRcrGojgmlISRFGn6pvd0v75+cDE/cBO4pZ4Utsfy0hxEF6SZGXtysUXC8FPph5ztEX8l8EMPLssZbyKsHzGddTd98PLeRlyQ+v2SMErbDn4TKuLjVxGYhn/l7uFvnok8CaGUd2hEN1tzo6jMgNBz2FPhG5WAvxbFfaf28/rGediNrtwaMErniFcizicJAgMBAAECggEAVIivSsIcHC9zG8sUJC6AAD88etjnnD5CjRAYhHKY1FQqzob7qpRELCsz7zBVHIjvYr1jIw8FbOWEwpFwl6KA+FGM3vFBwXmw4pv5Kohj09BZUMPLjmBoZCdKJfoxqHI3lkiEGGhrdTcVyEbONanFiU31G69zdylr7ZHfs4BITuposqYC2ca4hnleZBnwsHHYqpDOlIho/fqE1d99x7Fg0fHlluU1YLqYi7ythz5QuC5z8oKxLR7/Id5jETBRmXjGXMF4A04YJmccpcwKIdL7wfUoHfUepxr7W8a5V4NUz4O/EeWT3wZ0NZhrIJ8Pa2g3VEvh89oq05BzAGJ8klXgrQKBgQD6imkRUeQ6i8eB+qz8bkmRuj2ZXD/eIZw1tJGH4UEayP1trDPuJNFwyL/nSobMNwbGNNUPFKA3TKRJ5hGjMNdWF7NiXUust1jogdDCW0LfKrEp+WTGrwEfkkjt/6O+yPYwNyxAb0tKczwdG+GoZpOiakaJExs+p7/YucJaGOLdLwKBgQCZf0JZEiFBR/zcwcQXfszU88HzPxeXxlQmzRhEnDYttnzYlO4OzQRLqTJtMLylvbs2qqEI4HzmhIKESn8nmMmgbsCqboStBBATk8d9mK+tTBr/tUh+GWUUVEK0dtkWXzHdxuVqVCLOfC1xSYWse8Fc8ew5/xBEltbUuun5XixhRwKBgGuLpnZM1M9c2RMua7sgDGyFqZN/SdssSkWzcRUG2E+LH15IoTIEk/L5+YZrM1nN9RZ370096VhTieI+5CtM9+J44gPDUIyfV4bW0qnbSuVJCiueZmfzD2RiAvRG3B81XJht1o0COSuIM7G7Gi6TeaQqQoTssrE+iNebRZYvQJL5AoGAHHkz2Iv5aT+uU1EKTAV0f6g7+SBiAwCCSbRP5SJJFo+8dvyhcFNtkY9wUDrzM5ePa+OY/amO4cAA6gNbtP+tnzoMOVFJAUM4rDkEj3LMW8w7UeXR7bpOOsgqm4lUWccM3APyOJgk4MK+Nz6ZMla+lrw1wshivyZv8YQ+tMhYWS8CgYEA6dEIrmTz7k2bd3ip0BxrEoNU9qeAv5aMA4EiyjK4WGGeahcaqgYl2a78znUwg3jX3rGgfaIzXYIum8FH6OyknpYWzFEcxqlu3jTLn+DAtiVpRqDkAK3CyHVNFNi8jWe0619LrRdwINQ7j3mpCMc/KpnOvmXvhXWCVcyDhT16NWc=";
        config.alipayPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAt4Bo07gVRPdmvaRwOptrYmZksO22bOAHLSH1kgp9sTXH4VR6FcsNQnTsi1Tx04oz5hWLkBwccM54iiqTPfnMrCOKW6UBnmhGoL19qI5O8NvxdNwus9EX2MJiFCj9kHGp7Fww2nHjldO2pyxyFCmtrYhS/GDveat1cBCfIoJ7qZYUZXeEZjUW87q3guyvoD3vwl3a2OyV1QnUM6n41Vx62gHwbYIC8box0py7/FTyJfc8qmGyXOgwlAvuouVdRp9tsOhCbav6utOVvDGFmf/rMBkKrfmUec1ldybJvGJxF9/qWX7oBhnPFOfKRhrw8PM2LfV7xX9N58prwF2i2Df3IQIDAQAB";
        config.notifyUrl = "https://your-domain.com/alipay/notify";
        
        // 设置配置
        Factory.setOptions(config);
        log.info("支付宝SDK初始化完成");
    }

    @Override
    public Map<String, String> getUserInfo(String authCode) throws Exception {
        return null;
    }
//       try {
//            // 创建OAuth客户端
//            Client oauthClient = new Client(Factory.access$000());
//
//            // 使用授权码换取访问令牌
//           AlipaySystemOauthTokenResponse tokenResponse = oauthClient.getToken(authCode);
//
//            // 检查响应是否成功
//            if (ResponseChecker.success(tokenResponse)) {
//                // 使用访问令牌获取用户信息
//                // 注意：这里需要根据实际的API调整，可能需要使用其他方法或参数
//                // 由于JAR包中没有user.info.models包，我们需要找到替代方案
//
//                // 暂时返回空的用户信息，需要根据实际API进行调整
//                Map<String, String> userInfo = new HashMap<>();
//                userInfo.put("access_token", tokenResponse.getAccessToken());
//                userInfo.put("user_id", tokenResponse.getUserId());
//                return userInfo;
//            } else {
//                log.error("获取支付宝访问令牌失败: {}", tokenResponse.getMsg());
//                throw new Exception("获取支付宝访问令牌失败: " + tokenResponse.getMsg());
//            }
//        } catch (Exception e) {
//            log.error("获取支付宝用户信息异常: ", e);
//            throw new Exception("获取支付宝用户信息异常: " + e.getMessage());
//        }
//    }
}