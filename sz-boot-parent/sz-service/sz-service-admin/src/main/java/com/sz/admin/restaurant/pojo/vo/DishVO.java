package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import com.sz.excel.annotation.DictFormat;

/**
 * <p>
 * Dish返回vo
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Dish返回vo")
public class DishVO {

    @ExcelProperty(value = "")
    @Schema(description =  "")
    private Long dishId;

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