package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;

/**
 * <p>
 * OrderDetail返回vo
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-24
 */
@Data
@Schema(description = "OrderDetail返回vo")
public class OrderDetailVO {
    @ExcelProperty(value = "菜品ID")
    @Schema(description =  "菜品ID")
    private Long dishId;

    @ExcelProperty(value = "菜品名")
    @Schema(description =  "菜品名")
    private String dishName;

    @ExcelProperty(value = "菜品图片")
    @Schema(description =  "菜品图片")
    private String imageUrl;

    @ExcelProperty(value = "数量")
    @Schema(description =  "数量")
    private Integer number;

    @ExcelProperty(value = "单价")
    @Schema(description =  "单价")
    private Double amount;

}