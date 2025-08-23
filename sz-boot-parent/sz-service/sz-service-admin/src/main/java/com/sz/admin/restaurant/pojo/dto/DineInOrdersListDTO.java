package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.sz.core.common.entity.PageQuery;
/**
 * <p>
 * DineInOrders查询DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "DineInOrders查询DTO")
public class DineInOrdersListDTO extends PageQuery {

    @Schema(description =  "")
    private Long orderId;

    @Schema(description =  "")
    private Long tableId;
}