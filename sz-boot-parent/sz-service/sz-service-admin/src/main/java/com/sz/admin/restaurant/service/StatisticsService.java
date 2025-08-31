package com.sz.admin.restaurant.service;

import com.sz.admin.restaurant.pojo.dto.StatisticsQueryDTO;
import com.sz.admin.restaurant.pojo.vo.*;
import com.sz.core.common.entity.PageResult;

import java.util.List;

public interface StatisticsService {

    /**
     * 获取核心指标数据
     */
    CoreIndicatorsVO getCoreIndicators(StatisticsQueryDTO query);

    /**
     * 获取销售趋势数据
     */
    List<SalesTrendVO.TrendData> getSalesTrend(StatisticsQueryDTO query);

    /**
     * 获取菜品排行数据
     */
    List<DishRankingVO> getDishRanking(StatisticsQueryDTO query);

    /**
     * 获取订单状态分布数据
     */
    List<OrderStatusVO> getOrderStatusDistribution(StatisticsQueryDTO query);

    /**
     * 获取利润分析数据
     */
    List<ProfitAnalysisVO.ProfitData> getProfitAnalysis(StatisticsQueryDTO query);

    /**
     * 获取详细数据列表
     */
    PageResult<DetailedDataVO> getDetailedData(StatisticsQueryDTO query);

    /**
     * 获取菜品利润排行数据
     */
    List<DishProfitRankingVO> getDishProfitRanking(StatisticsQueryDTO query);
}