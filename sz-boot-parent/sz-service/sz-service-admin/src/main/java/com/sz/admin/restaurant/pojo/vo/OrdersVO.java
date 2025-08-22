package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * Orders返回vo
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Orders返回vo")
public class OrdersVO {

    @ExcelIgnore
    @Schema(description =  "")
    private Long orderId;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private String orderNumber;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private String orderType;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private Double totalAmount;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private String status;

    @ExcelProperty(value = "")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description =  "")
    private LocalDateTime createTime;

    @ExcelProperty(value = "")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description =  "")
    private LocalDateTime payTime;

}