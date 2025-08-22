package com.sz.admin.restaurant.pojo.dto;

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
    private Integer orderId;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private Integer tableId;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private Integer numberOfGuests;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private String remark;

}