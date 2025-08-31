package com.sz.admin.restaurant.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "统计查询参数")
public class StatisticsQueryDTO {

    @Schema(description = "开始日期", example = "2023-08-01")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2023-08-31")
    private LocalDate endDate;

    @Schema(description = "统计维度：day, week, month", example = "day")
    private String dimension;

    @Schema(description = "餐厅区域ID", example = "1")
    private Long restaurantAreaId;

    @Schema(description = "菜品分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "排序字段", example = "salesAmount")
    private String sortBy;

    @Schema(description = "排序方式：asc, desc", example = "desc")
    private String sortOrder;

    @Schema(description = "页码", example = "1")
    private Integer pageNum;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize;
}