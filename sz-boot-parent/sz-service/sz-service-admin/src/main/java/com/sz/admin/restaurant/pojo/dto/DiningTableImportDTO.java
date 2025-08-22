package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import cn.idev.excel.annotation.ExcelProperty;
import com.sz.excel.annotation.DictFormat;
/**
 * <p>
 * DiningTable导入DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "DiningTable导入DTO")
public class DiningTableImportDTO {

    @ExcelProperty(value = "餐桌编号")
    @Schema(description =  "餐桌编号")
    private String tableName;

    @ExcelProperty(value = "容量")
    @Schema(description =  "容量")
    private Integer capacity;

    @ExcelProperty(value = "状态")
    @DictFormat(dictType = "table_status")
    @Schema(description =  "状态")
    private String status;

}