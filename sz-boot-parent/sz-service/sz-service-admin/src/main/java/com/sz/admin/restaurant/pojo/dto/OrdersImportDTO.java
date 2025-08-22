package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import cn.idev.excel.annotation.ExcelProperty;
import org.springframework.format.annotation.DateTimeFormat;
/**
 * <p>
 * Orders导入DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Orders导入DTO")
public class OrdersImportDTO {

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

    @Schema(description =  "")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

}