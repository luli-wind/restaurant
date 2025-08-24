package com.sz.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RestaurantOrderNumberGenerator {
    // 业务前缀（餐饮标识）
    private static final String BUSINESS_PREFIX = "REST";
    // 每日最大订单量（6位流水号）
    private static final int MAX_ORDERS_PER_DAY = 999999;
    // 日期格式
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    // 线程安全的计数器存储
    private static final ConcurrentHashMap<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();

    static {
        // 每日凌晨1点清理过期计数器（防内存泄漏）
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                counterMap.keySet().removeIf(key -> !key.equals(getCurrentDate()))
        ));
    }

    public static String generateOrderNo() {
        String date = getCurrentDate();
        AtomicInteger counter = counterMap.computeIfAbsent(date, k -> new AtomicInteger(0));

        int serial = counter.incrementAndGet();
        if (serial > MAX_ORDERS_PER_DAY) {
            throw new IllegalStateException("当日订单量已达上限：" + MAX_ORDERS_PER_DAY);
        }

        return BUSINESS_PREFIX + date + String.format("%06d", serial);
    }

    private static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMAT);
    }
}
