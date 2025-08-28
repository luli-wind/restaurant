package com.sz.admin.restaurant.pojo.po;

import com.mybatisflex.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.io.Serial;
import com.sz.mysql.EntityChangeListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 菜品配方表（Bill of Materials）
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Table(value = "dish_recipe", onInsert = EntityChangeListener.class, onUpdate = EntityChangeListener.class)
@Schema(description = "菜品配方表（Bill of Materials）")
public class DishRecipe implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "外键，关联菜品ID")
    private Long dishId;

    @Schema(description = "外键，关联原材料ID")
    private Long materialId;

    @Schema(description = "制作一份该菜品所需原材料的数量")
    private Double materialQuantity;


}