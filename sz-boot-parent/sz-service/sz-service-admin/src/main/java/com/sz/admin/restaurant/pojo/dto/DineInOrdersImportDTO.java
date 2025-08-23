package com.sz.admin.restaurant.pojo.dto;

import cn.idev.excel.annotation.ExcelIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import cn.idev.excel.annotation.ExcelProperty;
/**
 * <p>
 * DineInOrders导入DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "DineInOrders导入DTO")
public class DineInOrdersImportDTO {

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private Long orderId;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private Long tableId;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private Integer numberOfGuests;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private String remark;

    // Orders实体的字段
    @ExcelProperty(value = "订单号")
    @Schema(description = "订单号")
    private String orderNumber;

    @ExcelProperty(value = "订单类型")
    @Schema(description = "订单类型")
    private String orderType;

    @ExcelProperty(value = "总金额")
    @Schema(description = "总金额")
    private Double totalAmount;

    @ExcelProperty(value = "状态")
    @Schema(description = "状态")
    private String status;

    @ExcelProperty(value = "创建时间")
    @Schema(description = "创建时间")
    private java.time.LocalDateTime createTime;

    @ExcelProperty(value = "支付状态")
    @Schema(description ="支付状态")
    private String payStatus;

    @ExcelProperty(value = "支付时间")
    @Schema(description = "支付时间")
    private java.time.LocalDateTime payTime;

    //table字段
    @ExcelProperty(value = "桌子编号")
    @Schema(description = "桌子编号")
    private String tableName;

}