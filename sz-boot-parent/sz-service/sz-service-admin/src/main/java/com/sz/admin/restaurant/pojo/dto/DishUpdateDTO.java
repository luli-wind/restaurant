package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * <p>
 * Dish修改DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Dish修改DTO")
public class DishUpdateDTO {

    @Schema(description =  "")
    private Long dishId;

    @Schema(description =  "")
    private String imageUrl;

    @Schema(description =  "")
    private String dishName;

    @Schema(description =  "")
    private String category;

    @Schema(description =  "")
    private BigDecimal price;

    @Schema(description =  "")
    private String description;

}