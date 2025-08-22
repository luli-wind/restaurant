package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

import cn.idev.excel.annotation.ExcelProperty;
import com.sz.excel.annotation.DictFormat;
/**
 * <p>
 * Dish导入DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Dish导入DTO")
public class DishImportDTO {

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private String imageUrl;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private String dishName;

    @ExcelProperty(value = "")
    @DictFormat(dictType = "dish_category")
    @Schema(description =  "")
    private String category;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private BigDecimal price;

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private String description;

}