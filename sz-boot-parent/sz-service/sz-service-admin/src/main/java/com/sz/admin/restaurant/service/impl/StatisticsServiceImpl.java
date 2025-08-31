package com.sz.admin.restaurant.service.impl;

import com.sz.admin.restaurant.mapper.StatisticsMapper;
import com.sz.admin.restaurant.pojo.dto.StatisticsQueryDTO;
import com.sz.admin.restaurant.pojo.vo.*;
import com.sz.admin.restaurant.service.StatisticsService;
import com.sz.core.common.entity.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;

    @Override
    public CoreIndicatorsVO getCoreIndicators(StatisticsQueryDTO query) {
        return statisticsMapper.queryCoreIndicators(query);
    }

    @Override
    public List<SalesTrendVO.TrendData> getSalesTrend(StatisticsQueryDTO query) {
        return statisticsMapper.querySalesTrend(query);
    }

    @Override
    public List<DishRankingVO> getDishRanking(StatisticsQueryDTO query) {
        return statisticsMapper.queryDishRanking(query);
    }

    @Override
    public List<OrderStatusVO> getOrderStatusDistribution(StatisticsQueryDTO query) {
        return statisticsMapper.queryOrderStatusDistribution(query);
    }

    @Override
    public List<ProfitAnalysisVO.ProfitData> getProfitAnalysis(StatisticsQueryDTO query) {
        return statisticsMapper.queryProfitAnalysis(query);
    }

    @Override
    public PageResult<DetailedDataVO> getDetailedData(StatisticsQueryDTO query) {
        if (query.getPageNum() == null || query.getPageSize() == null) {
            query.setPageNum(1);
            query.setPageSize(10);
        }
        
        long total = statisticsMapper.countDetailedData(query);
        List<DetailedDataVO> data = statisticsMapper.queryDetailedData(query);
        
        return new  PageResult(query.getPageNum(), query.getPageSize(),total/query.getPageSize(),total,data);
    }

    @Override
    public List<DishProfitRankingVO> getDishProfitRanking(StatisticsQueryDTO query) {
        return statisticsMapper.queryDishProfitRanking(query);
    }
}