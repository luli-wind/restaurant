package com.sz.admin.restaurant.service;

/**
 * <p>
 * 催单服务接口
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-26
 */
public interface OrderReminderService {

    /**
     * 检查是否可以催单（去重机制）
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 是否可以催单
     */
    boolean canRemind(Long orderId, Long userId);

    /**
     * 记录催单操作
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     */
    void recordReminder(Long orderId, Long userId);
}