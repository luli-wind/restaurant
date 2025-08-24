package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * <p>
 * OrderDetail添加DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-24
 */
@Data
@Schema(description = "OrderDetail添加DTO")
public class OrderDetailCreateDTO {

   @Schema(description =  "菜品名")
   private String dishName;

   @Schema(description =  "菜品图片")
   private String imageUrl;

   @Schema(description =  "菜品ID")
   private Integer dishId;

   @Schema(description =  "数量")
   private Integer number;

   @Schema(description =  "总价")
   private Double amount;

}