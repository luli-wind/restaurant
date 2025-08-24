package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import cn.idev.excel.annotation.ExcelProperty;
/**
 * <p>
 * OrderDetail导入DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-24
 */
@Data
@Schema(description = "OrderDetail导入DTO")
public class OrderDetailImportDTO {

    @ExcelProperty(value = "菜品名")
    @Schema(description =  "菜品名")
    private String dishName;

    @ExcelProperty(value = "菜品图片")
    @Schema(description =  "菜品图片")
    private String imageUrl;

    @ExcelProperty(value = "订单ID")
    @Schema(description =  "订单ID")
    private Integer orderId;

    @ExcelProperty(value = "菜品ID")
    @Schema(description =  "菜品ID")
    private Integer dishId;

    @ExcelProperty(value = "数量")
    @Schema(description =  "数量")
    private Integer number;

    @ExcelProperty(value = "总价")
    @Schema(description =  "总价")
    private Double amount;

}