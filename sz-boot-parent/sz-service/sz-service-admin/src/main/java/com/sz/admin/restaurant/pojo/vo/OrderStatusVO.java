package com.sz.admin.restaurant.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "订单状态分布响应")
public class OrderStatusVO {

    @Schema(description = "状态数据列表")
    private List<StatusData> statusData;

    @Data
    @Schema(description = "状态数据项")
    public static class StatusData {
        @Schema(description = "状态码", example = "2004001")
        private String status;

        @Schema(description = "状态名称", example = "已下单")
        private String statusName;

        @Schema(description = "数量", example = "10")
        private Integer count;

        @Schema(description = "百分比", example = "7.8")
        private Double percentage;
    }
}