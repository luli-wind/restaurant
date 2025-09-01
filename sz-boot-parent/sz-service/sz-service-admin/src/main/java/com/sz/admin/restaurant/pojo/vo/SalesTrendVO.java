package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "销售趋势响应")
public class SalesTrendVO {

    @Schema(description = "趋势数据列表")
    private List<TrendData> trendData;

    @Data
    @Schema(description = "趋势数据项")
    public static class TrendData {
        @Schema(description = "日期")
        private String date;

        @Schema(description = "销售额")
        private Double amount;

        @Schema(description = "订单数")
        private Integer count;
    }
}