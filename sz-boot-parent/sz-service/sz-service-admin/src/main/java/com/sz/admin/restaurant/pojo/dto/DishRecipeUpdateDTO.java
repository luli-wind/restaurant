package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * <p>
 * 菜品配方表（Bill of Materials）修改DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "菜品配方表（Bill of Materials）修改DTO")
public class DishRecipeUpdateDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "外键，关联菜品ID")
    private Long dishId;

    @Schema(description = "外键，关联原材料ID")
    private Long materialId;

    @Schema(description = "制作一份该菜品所需原材料的数量")
    private BigDecimal materialQuantity;

}