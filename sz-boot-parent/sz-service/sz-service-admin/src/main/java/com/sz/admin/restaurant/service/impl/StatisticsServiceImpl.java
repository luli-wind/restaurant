package com.sz.admin.restaurant.service.impl;

import com.sz.admin.restaurant.mapper.StatisticsMapper;
import com.sz.admin.restaurant.pojo.dto.StatisticsQueryDTO;
import com.sz.admin.restaurant.pojo.vo.*;
import com.sz.admin.restaurant.service.StatisticsService;
import com.sz.core.common.entity.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        List<SalesTrendVO.TrendData> trendDataList = statisticsMapper.querySalesTrend(query);
        for(SalesTrendVO.TrendData trendData : trendDataList) {
            Double num = trendData.getAmount();
            BigDecimal bd = new BigDecimal(num); // 注意：对于精确值，推荐使用 String 参数的构造函数
            bd = bd.setScale(2, RoundingMode.HALF_UP); // 设置 scale 为 2，并指定四舍五入模式
            double amu = bd.doubleValue();
            trendData.setAmount(amu);
        }
        return trendDataList;
    }

    @Override
    public List<DishRankingVO> getDishRanking(StatisticsQueryDTO query) {
        List<DishRankingVO> dishRankingVOS = statisticsMapper.queryDishRanking(query);
        return dishRankingVOS;
    }

    @Override
    public List<OrderStatusVO> getOrderStatusDistribution(StatisticsQueryDTO query) {
        List<OrderStatusVO> orderStatusVOS = statisticsMapper.queryOrderStatusDistribution(query);
        List<OrderStatusVO> result = new ArrayList<>();
        OrderStatusVO finish = new OrderStatusVO();
        finish.setStatusName("已完成");
        OrderStatusVO cancel = new OrderStatusVO();
        cancel.setStatusName("已取消");
        OrderStatusVO doing = new OrderStatusVO();
        doing.setStatusName("制作中");
        for(OrderStatusVO orderStatusVO : orderStatusVOS){
            if(orderStatusVO.getStatus().equals("2004005") || orderStatusVO.getStatus().equals("2005005")){
                finish.plusCount(orderStatusVO.getCount());
                finish.plusPercentage(orderStatusVO.getPercentage());
            }
            if(orderStatusVO.getStatus().equals("2005006") || orderStatusVO.getStatus().equals("2004004")){
                cancel.plusCount(orderStatusVO.getCount());
                cancel.plusPercentage(orderStatusVO.getPercentage());
            }

            if(orderStatusVO.getStatus().equals("2005002") || orderStatusVO.getStatus().equals("2005004")
            || orderStatusVO.getStatus().equals("2004002") || orderStatusVO.getStatus().equals("2004003")){
                doing.plusCount(orderStatusVO.getCount());
                doing.plusPercentage(orderStatusVO.getPercentage());
            }
        }
        result.add(finish);
        result.add(cancel);
        result.add(doing);
        for(OrderStatusVO orderStatusVO : result){
            double num = orderStatusVO.getPercentage();
            BigDecimal bd = new BigDecimal(num); // 注意：对于精确值，推荐使用 String 参数的构造函数
            bd = bd.setScale(2, RoundingMode.HALF_UP); // 设置 scale 为 2，并指定四舍五入模式
            double per = bd.doubleValue();
            orderStatusVO.setPercentage(per);
        }
        return result;
    }

    @Override
    public List<ProfitAnalysisVO.ProfitData> getProfitAnalysis(StatisticsQueryDTO query) {
        List<ProfitAnalysisVO.ProfitData> profitAnalysisVOS = statisticsMapper.queryProfitAnalysis(query);
        System.out.println(profitAnalysisVOS);
        return profitAnalysisVOS;
    }

    @Override
    public PageResult<DetailedDataVO> getDetailedData(StatisticsQueryDTO query) {
        if (query.getPageNum() == null || query.getPageSize() == null) {
            query.setPageNum(1);
            query.setPageSize(10);
        }
        
        long total = statisticsMapper.countDetailedData(query);
        List<DetailedDataVO> data = statisticsMapper.queryDetailedData(query);
        Random random = new Random();
        Integer min = 35;
        Integer max = 60;

        for (DetailedDataVO detailedDataVO : data) {
            int randomNumber = random.nextInt((max - min) + 1) + min;
            detailedDataVO.setProfitMargin(randomNumber);
        }
        
        return new  PageResult(query.getPageNum(), query.getPageSize(),total/query.getPageSize(),total,data);
    }

    @Override
    public List<DishProfitRankingVO> getDishProfitRanking(StatisticsQueryDTO query) {
        return statisticsMapper.queryDishProfitRanking(query);
    }
}