package com.sz.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RestaurantOrderNumberGenerator {
    // 业务前缀（餐饮标识）
    private static final String BUSINESS_PREFIX = "REST";
    // 日期时间格式（精确到毫秒）
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    // 随机数生成器
    private static final Random random = new Random();
    // 机器标识（可以使用机器IP后两位或配置参数）
    private static final String MACHINE_ID = getMachineId();
    // 已生成的订单号缓存（用于短期防重复，可设置大小限制）
    private static final Set<String> generatedOrderNumbers = ConcurrentHashMap.newKeySet();
    // 计数器（用于同一毫秒内的多个订单）
    private static final AtomicInteger microCounter = new AtomicInteger(0);

    /**
     * 生成订单号（格式：REST + 日期时间(17位) + 机器标识(2位) + 随机数(4位) + 毫秒内计数(2位)）
     * 总长度：4 + 17 + 2 + 4 + 2 = 29位
     */
    public static String generateOrderNo() {
        String orderNumber;
        int retryCount = 0;
        final int maxRetry = 3; // 最大重试次数

        do {
            // 获取当前时间（精确到毫秒）
            String timestamp = LocalDateTime.now().format(DATE_TIME_FORMAT);

            // 生成随机数（4位）
            String randomPart = String.format("%04d", random.nextInt(10000));

            // 获取毫秒内计数（2位）
            String counterPart = String.format("%02d", microCounter.incrementAndGet() % 100);

            // 组合订单号
            orderNumber = BUSINESS_PREFIX + timestamp + MACHINE_ID + randomPart + counterPart;

            // 如果订单号已存在且重试次数未超过上限，则重新生成
            if (generatedOrderNumbers.contains(orderNumber) && retryCount < maxRetry) {
                retryCount++;
                continue;
            }

            break;
        } while (true);

        // 将新生成的订单号添加到缓存中
        generatedOrderNumbers.add(orderNumber);

        // 清理缓存防止内存溢出（简单示例，实际应使用LRU缓存等机制）
        if (generatedOrderNumbers.size() > 10000) {
            generatedOrderNumbers.clear();
        }

        return orderNumber;
    }

    /**
     * 获取机器标识（示例实现，实际应用中可从配置读取或使用网络信息）
     */
    private static String getMachineId() {
        try {
            // 示例：获取本机IP地址最后两位作为机器标识
            // 实际应用中可根据需要修改为更稳定的机器标识方案
            String ip = java.net.InetAddress.getLocalHost().getHostAddress();
            String[] parts = ip.split("\\.");
            if (parts.length >= 4) {
                return String.format("%02d", Integer.parseInt(parts[2]) % 100) +
                        String.format("%02d", Integer.parseInt(parts[3]) % 100);
            }
        } catch (Exception e) {
            // 异常处理
        }

        // 默认返回随机机器标识
        return String.format("%02d", random.nextInt(100));
    }

    /**
     * 检查订单号是否唯一（可选方法）
     */
    public static boolean isOrderNumberUnique(String orderNumber) {
        return !generatedOrderNumbers.contains(orderNumber);
    }

    /**
     * 清空已生成订单号缓存（主要用于测试）
     */
    public static void clearCache() {
        generatedOrderNumbers.clear();
    }
}