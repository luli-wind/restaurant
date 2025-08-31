package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "核心指标响应")
public class CoreIndicatorsVO {

    @Schema(description = "销售总额")
    private BigDecimal totalSales;

    @Schema(description = "订单数量")
    private Integer orderCount;

    @Schema(description = "客户数量")
    private Integer customerCount;

    @Schema(description = "平均客单价")
    private BigDecimal avgOrderAmount;
}