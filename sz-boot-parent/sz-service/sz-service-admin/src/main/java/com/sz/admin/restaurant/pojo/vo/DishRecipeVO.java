package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 菜品配方表（Bill of Materials）返回VO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "菜品配方表（Bill of Materials）返回VO")
public class DishRecipeVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "外键，关联菜品ID")
    private Long dishId;

    @Schema(description = "外键，关联原材料ID")
    private Long materialId;

    @Schema(description = "制作一份该菜品所需原材料的数量")
    private BigDecimal materialQuantity;

    @Schema(description = "原材料名称")
    private String materialName;

    @Schema(description = "计量单位")
    private String unit;


}