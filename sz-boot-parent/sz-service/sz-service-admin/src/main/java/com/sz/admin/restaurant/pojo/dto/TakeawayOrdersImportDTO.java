package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

import cn.idev.excel.annotation.ExcelProperty;
/**
 * <p>
 * TakeawayOrders导入DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "TakeawayOrders导入DTO")
public class TakeawayOrdersImportDTO {

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private Long orderId;


    @ExcelProperty(value = "")
    @Schema(description = "")
    private String customerName;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private String customerPhone;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private String deliveryAddress;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private Double packagingFee;

    @ExcelProperty(value = "配送费")
    @Schema(description =  "配送费")
    private Double deliveryFee;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private String remark;

    @Schema(description = "订单号")
    @ExcelProperty(value = "")
    private String orderNumber;


    @Schema(description = "订单类型")
    @ExcelProperty(value = "")
    private String orderType;

    @Schema(description = "总金额")
    @ExcelProperty(value = "")
    private Double totalAmount;


    @Schema(description = "状态")
    @ExcelProperty(value = "")
    private String status;


    @Schema(description = "创建时间")
    @ExcelProperty(value = "")
    private java.time.LocalDateTime createTime;


    @Schema(description ="支付状态")
    @ExcelProperty(value = "")
    private String payStatus;

    @Schema(description = "支付时间")
    @ExcelProperty(value = "")
    private java.time.LocalDateTime payTime;

    @Schema(description ="退款原因")
    @ExcelProperty(value = "")
    private String refundReason;

}