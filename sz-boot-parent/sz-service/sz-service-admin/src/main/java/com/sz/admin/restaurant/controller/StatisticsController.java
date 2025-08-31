package com.sz.admin.restaurant.controller;

import com.sz.admin.restaurant.pojo.dto.StatisticsQueryDTO;
import com.sz.admin.restaurant.pojo.vo.*;
import com.sz.admin.restaurant.service.StatisticsService;
import com.sz.core.common.entity.ApiResult;
import com.sz.core.common.entity.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/statistics")
@Tag(name = "统计管理")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/core-indicators")
    @Operation(summary = "获取核心指标数据")
    public ApiResult<CoreIndicatorsVO> getCoreIndicators(StatisticsQueryDTO query) {
        return ApiResult.success(statisticsService.getCoreIndicators(query));
    }

    @GetMapping("/sales-trend")
    @Operation(summary = "获取销售趋势数据")
    public ApiResult<List<SalesTrendVO.TrendData>> getSalesTrend(StatisticsQueryDTO query) {
        return ApiResult.success(statisticsService.getSalesTrend(query));
    }

    @GetMapping("/dish-ranking")
    @Operation(summary = "获取菜品排行数据")
    public ApiResult<List<DishRankingVO>> getDishRanking(StatisticsQueryDTO query) {
        return ApiResult.success(statisticsService.getDishRanking(query));
    }

    @GetMapping("/order-status-distribution")
    @Operation(summary = "获取订单状态分布数据")
    public ApiResult<List<OrderStatusVO>> getOrderStatusDistribution(StatisticsQueryDTO query) {
        return ApiResult.success(statisticsService.getOrderStatusDistribution(query));
    }

    @GetMapping("/profit-analysis")
    @Operation(summary = "获取利润分析数据")
    public ApiResult<List<ProfitAnalysisVO.ProfitData>> getProfitAnalysis(StatisticsQueryDTO query) {
        return ApiResult.success(statisticsService.getProfitAnalysis(query));
    }

    @GetMapping("/detailed-data")
    @Operation(summary = "获取详细数据列表")
    public ApiResult<PageResult<DetailedDataVO>> getDetailedData(StatisticsQueryDTO query) {
        return ApiResult.success(statisticsService.getDetailedData(query));
    }

    @GetMapping("/dish-profit-ranking")
    @Operation(summary = "获取菜品利润排行数据")
    public ApiResult<List<DishProfitRankingVO>> getDishProfitRanking(StatisticsQueryDTO query) {
        return ApiResult.success(statisticsService.getDishProfitRanking(query));
    }
}
