package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.sz.core.common.entity.PageQuery;

/**
 * <p>
 * 菜品配方表（Bill of Materials）查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "菜品配方表（Bill of Materials）查询DTO")
public class DishRecipeListDTO extends PageQuery {

    @Schema(description = "外键，关联菜品ID")
    private Long dishId;

    @Schema(description = "外键，关联原材料ID")
    private Long materialId;

}