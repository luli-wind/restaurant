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
    private Integer orderId;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private String customerPhone;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private String deliveryAddress;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private BigDecimal packagingFee;

    @ExcelProperty(value = "配送费")
    @Schema(description =  "配送费")
    private BigDecimal deliveryFee;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private String remark;

}