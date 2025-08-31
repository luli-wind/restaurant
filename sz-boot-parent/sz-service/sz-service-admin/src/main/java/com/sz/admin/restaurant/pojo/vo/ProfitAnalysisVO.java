package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "利润分析响应")
public class ProfitAnalysisVO {

    @Schema(description = "利润数据列表")
    private List<ProfitData> profitData;

    @Data
    @Schema(description = "利润数据项")
    public static class ProfitData {
        @Schema(description = "日期", example = "2023-08")
        private String date;

        @Schema(description = "收入", example = "12865.00")
        private BigDecimal revenue;

        @Schema(description = "成本", example = "6432.50")
        private BigDecimal cost;

        @Schema(description = "利润", example = "6432.50")
        private BigDecimal profit;

        @Schema(description = "利润率(%)", example = "50.0")
        private BigDecimal profitMargin;
    }
}