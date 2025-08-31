package com.sz.admin.restaurant.mapper;

import com.sz.admin.restaurant.pojo.dto.StatisticsQueryDTO;
import com.sz.admin.restaurant.pojo.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StatisticsMapper {

    /**
     * 查询核心指标数据
     */
    CoreIndicatorsVO queryCoreIndicators(@Param("query") StatisticsQueryDTO query);

    /**
     * 查询销售趋势数据
     */
    List<SalesTrendVO.TrendData> querySalesTrend(@Param("query") StatisticsQueryDTO query);

    /**
     * 查询菜品排行数据
     */
    List<DishRankingVO> queryDishRanking(@Param("query") StatisticsQueryDTO query);

    /**
     * 查询订单状态分布数据
     */
    List<OrderStatusVO> queryOrderStatusDistribution(@Param("query") StatisticsQueryDTO query);

    /**
     * 查询利润分析数据
     */
    List<ProfitAnalysisVO.ProfitData> queryProfitAnalysis(@Param("query") StatisticsQueryDTO query);

    /**
     * 查询详细数据列表
     */
    List<DetailedDataVO> queryDetailedData(@Param("query") StatisticsQueryDTO query);

    /**
     * 查询详细数据总数
     */
    Long countDetailedData(@Param("query") StatisticsQueryDTO query);

    /**
     * 查询菜品利润排行数据
     */
    List<DishProfitRankingVO> queryDishProfitRanking(@Param("query") StatisticsQueryDTO query);
}