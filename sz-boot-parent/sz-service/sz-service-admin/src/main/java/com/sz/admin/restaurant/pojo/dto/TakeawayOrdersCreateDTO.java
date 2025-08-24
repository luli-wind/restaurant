package com.sz.admin.restaurant.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

/**
 * <p>
 * TakeawayOrders添加DTO
 * </p>
 *
 * @author sz-admin
 * @since 2025-08-22
 */
@Data
@Schema(description = "TakeawayOrders添加DTO")
public class TakeawayOrdersCreateDTO {

   @Schema(description =  "")
   private Long orderId;

   @Schema(description =  "")
   private String customerPhone;

   @Schema(description =  "")
   private String deliveryAddress;

   @Schema(description =  "")
   private BigDecimal packagingFee;

   @Schema(description =  "配送费")
   private BigDecimal deliveryFee;

   @Schema(description =  "")
   private String remark;

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


   @Schema(description ="退款原因")
   private String refundReason;

}