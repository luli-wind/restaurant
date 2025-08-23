package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;

/**
 * <p>
 * Inventory返回vo
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Inventory返回vo")
public class InventoryVO {
    @ExcelProperty(value = "材料i=Id")
    @Schema(description =  "材料Id")
    private Long materialId;

    @ExcelProperty(value = "材料名")
    @Schema(description =  "材料名")
    private String materialName;

    @ExcelProperty(value = "当前容量")
    @Schema(description =  "当前容量")
    private Integer currentStock;

    @ExcelProperty(value = "最小容量")
    @Schema(description =  "最小容量")
    private Integer minStock;

    @ExcelProperty(value = "计量单位")
    @Schema(description =  "计量单位")
    private String unit;

}