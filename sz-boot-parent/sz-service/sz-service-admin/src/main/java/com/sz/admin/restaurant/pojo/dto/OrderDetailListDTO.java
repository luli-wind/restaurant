package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.sz.core.common.entity.PageQuery;
/**
 * <p>
 * OrderDetail查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-24
 */
@Data
@Schema(description = "OrderDetail查询DTO")
public class OrderDetailListDTO extends PageQuery {

    @Schema(description =  "菜品名")
    private String dishName;

    @Schema(description =  "订单ID")
    private Integer orderId;

    @Schema(description =  "菜品ID")
    private Integer dishId;

}