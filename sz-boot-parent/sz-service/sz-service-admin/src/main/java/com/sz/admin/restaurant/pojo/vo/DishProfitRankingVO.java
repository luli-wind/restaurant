package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "菜品利润排行响应")
public class DishProfitRankingVO {

    @Schema(description = "菜品ID")
    private Long dishId;

    @Schema(description = "菜品名称")
    private String dishName;

    @Schema(description = "销售数量")
    private Integer salesCount;

    @Schema(description = "销售额")
    private BigDecimal salesAmount;

    @Schema(description = "成本")
    private BigDecimal cost;

    @Schema(description = "利润")
    private BigDecimal profit;

    @Schema(description = "利润率(%)")
    private BigDecimal profitMargin;

    @Schema(description = "排名")
    private Integer rank;
}