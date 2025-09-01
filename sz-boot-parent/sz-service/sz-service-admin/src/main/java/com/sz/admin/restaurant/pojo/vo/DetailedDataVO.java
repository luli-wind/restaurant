package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "详细数据响应")
public class DetailedDataVO {
    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单号")
    private String orderNumber;

    @Schema(description = "订单时间")
    private String orderTime;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "订单状态")
    private String orderStatus;

    @Schema(description = "菜品名称")
    private String dishNames;
    
    @Schema(description = "订单数量")
    private Integer orderCount;
    
    @Schema(description = "客户数量")
    private Integer customerCount;

    @Schema(description = "利润率")
    private Integer profitMargin;
}
