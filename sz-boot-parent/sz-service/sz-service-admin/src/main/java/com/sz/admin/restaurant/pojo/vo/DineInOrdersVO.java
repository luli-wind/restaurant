package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;

/**
 * <p>
 * DineInOrders返回vo
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "DineInOrders返回vo")
public class DineInOrdersVO {

    @ExcelIgnore
    @Schema(description =  "")
    private Long id;

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