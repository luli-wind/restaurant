package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "菜品排行响应")
public class DishRankingVO {

    @Schema(description = "排行数据列表")
    private List<RankingData> rankingData;

    @Data
    @Schema(description = "排行数据项")
    public static class RankingData {
        @Schema(description = "菜品ID")
        private Long dishId;

        @Schema(description = "菜品名称")
        private String dishName;

        @Schema(description = "销售数量")
        private Integer quantity;

        @Schema(description = "销售金额")
        private BigDecimal amount;
    }
}