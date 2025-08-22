package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;

/**
 * <p>
 * TakeawayOrders返回vo
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "TakeawayOrders返回vo")
public class TakeawayOrdersVO {

    @ExcelIgnore
    @Schema(description =  "")
    private Long id;

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