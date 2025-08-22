package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.sz.excel.annotation.DictFormat;

/**
 * <p>
 * DiningTable返回vo
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "DiningTable返回vo")
public class DiningTableVO {

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