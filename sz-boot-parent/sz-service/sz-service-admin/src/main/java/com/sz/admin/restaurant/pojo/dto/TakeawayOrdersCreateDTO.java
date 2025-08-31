package com.sz.admin.restaurant.pojo.dto;

import cn.idev.excel.annotation.ExcelProperty;
import com.sz.admin.restaurant.pojo.po.OrderDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

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

   @Schema(description = "")
   private String customerName;

   @Schema(description =  "")
   private String customerPhone;

   @Schema(description =  "")
   private String deliveryAddress;

   @Schema(description =  "")
   private Double packagingFee;

   @Schema(description =  "配送费")
   private Double deliveryFee;

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

   @Schema(description ="第三方用户ID")
   private Long thirdPartyUserId;

   // 订单项列表
   @Schema(description = "订单项列表")
   private List<OrderDetail> orderItems;

}