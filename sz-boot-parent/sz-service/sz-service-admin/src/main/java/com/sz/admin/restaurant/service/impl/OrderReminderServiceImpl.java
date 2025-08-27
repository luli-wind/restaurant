package com.sz.admin.restaurant.service.impl;

import com.sz.admin.restaurant.service.OrderReminderService;
import com.sz.redis.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 催单服务实现类
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-26
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderReminderServiceImpl implements OrderReminderService {

    private final RedisCache redisCache;
    private final RedisTemplate<Object, Object> redisTemplate;

    // 催单冷却时间（分钟）
    private static final int REMINDER_COOLDOWN_MINUTES = 5;

    /**
     * 检查是否可以催单（去重机制）
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 是否可以催单
     */
    @Override
    public boolean canRemind(Long orderId, Long userId) {
        String key = buildReminderKey(orderId, userId);
        Boolean hasKey = redisTemplate.hasKey(key);
        return !Boolean.TRUE.equals(hasKey);
    }

    /**
     * 记录催单操作
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     */
    @Override
    public void recordReminder(Long orderId, Long userId) {
        String key = buildReminderKey(orderId, userId);
        // 记录催单时间，设置过期时间
        redisTemplate.opsForValue().set(key, System.currentTimeMillis(), REMINDER_COOLDOWN_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 构建催单记录的Redis键
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return Redis键
     */
    private String buildReminderKey(Long orderId, Long userId) {
        return "order:reminder:" + orderId + ":" + userId;
    }
}