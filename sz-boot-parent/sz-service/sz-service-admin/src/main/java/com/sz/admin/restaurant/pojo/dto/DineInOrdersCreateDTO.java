package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * DineInOrders添加DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "DineInOrders添加DTO")
public class DineInOrdersCreateDTO {

   @Schema(description =  "")
   private Integer orderId;

   @Schema(description =  "")
   private Integer tableId;

   @Schema(description =  "")
   private Integer numberOfGuests;

   @Schema(description =  "")
   private String remark;

}