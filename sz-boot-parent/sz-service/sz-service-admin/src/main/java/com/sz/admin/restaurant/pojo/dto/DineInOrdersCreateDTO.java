package com.sz.admin.restaurant.pojo.dto;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
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
   private Long orderId;

   @Schema(description =  "")
   private Long tableId;

   @Schema(description =  "")
   private Integer numberOfGuests;

   @Schema(description =  "")
   private String remark;

   // Orders实体的字段
   @Schema(description = "订单号")
   private String orderNumber;


   @Schema(description = "订单类型")
   private String orderType;

   @Schema(description = "总金额")
   private Double totalAmount;


   @Schema(description = "状态")
   private String status;


   @Schema(description = "创建时间")
   private java.time.LocalDateTime createTime;

   @Schema(description ="支付状态")
   private String payStatus;


   @Schema(description = "支付时间")
   private java.time.LocalDateTime payTime;

   //table字段
   @Schema(description = "桌子编号")
   private String tableName;


}