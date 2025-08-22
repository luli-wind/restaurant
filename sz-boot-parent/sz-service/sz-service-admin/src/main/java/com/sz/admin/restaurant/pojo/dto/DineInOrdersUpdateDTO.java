package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * DineInOrders修改DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "DineInOrders修改DTO")
public class DineInOrdersUpdateDTO {

    @Schema(description =  "")
    private Long id;

    @Schema(description =  "")
    private Integer orderId;

    @Schema(description =  "")
    private Integer tableId;

    @Schema(description =  "")
    private Integer numberOfGuests;

    @Schema(description =  "")
    private String remark;

}