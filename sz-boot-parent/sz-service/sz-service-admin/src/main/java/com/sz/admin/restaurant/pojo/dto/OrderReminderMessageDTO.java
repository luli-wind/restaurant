package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author sz-admin
 * @since 2025-08-26
 */
@Data
@Schema(description = "催单消息DTO")
public class OrderReminderMessageDTO {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单号")
    private String orderNumber;

    @Schema(description = "订单类型")
    private String orderType;

    @Schema(description = "总金额")
    private Double totalAmount;

    @Schema(description = "催单时间")
    private LocalDateTime reminderTime;

    @Schema(description = "桌号(堂食订单)")
    private String tableNumber;

    @Schema(description = "客户姓名(外卖订单)")
    private String customerName;

    @Schema(description = "客户电话(外卖订单)")
    private String customerPhone;

}