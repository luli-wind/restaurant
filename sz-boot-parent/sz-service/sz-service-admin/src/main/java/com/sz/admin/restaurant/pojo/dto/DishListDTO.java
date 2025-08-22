package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.sz.core.common.entity.PageQuery;
import java.math.BigDecimal;
/**
 * <p>
 * Dish查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "Dish查询DTO")
public class DishListDTO extends PageQuery {

    @Schema(description =  "")
    private String dishName;

    @Schema(description =  "")
    private String category;

    @Schema(description =  "开始")
    private BigDecimal priceStart;

    @Schema(description =  "结束")
    private BigDecimal priceEnd;

}