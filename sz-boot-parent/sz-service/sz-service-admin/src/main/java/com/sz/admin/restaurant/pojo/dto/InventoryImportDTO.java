package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import cn.idev.excel.annotation.ExcelProperty;
/**
 * <p>
 * Inventory导入DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Inventory导入DTO")
public class InventoryImportDTO {

    @ExcelProperty(value = "材料名")
    @Schema(description =  "材料名")
    private String materialName;

    @ExcelProperty(value = "当前容量")
    @Schema(description =  "当前容量")
    private Double   currentStock;

    @ExcelProperty(value = "最小容量")
    @Schema(description =  "最小容量")
    private Double minStock;

    @ExcelProperty(value = "计量单位")
    @Schema(description =  "计量单位")
    private String unit;

    @ExcelProperty(value = "库存状态")
    @Schema(description = "库存状态")
    private String status;
}