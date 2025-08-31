import http from '@/api';
import { ADMIN_MODULE } from '@/api/helper/prefix';
import type {
    CoreIndicatorsResponse,
    SalesTrendResponse,
    DishRankingResponse,
    OrderStatusResponse,
    ProfitAnalysisResponse,
    DetailedDataResponse,
    StatisticsQueryParams,
} from '@/api/types/statistics/statistics';

/**
 * 获取核心指标
 * @param params 查询参数
 * @returns 核心指标数据
 */
export const getCoreIndicatorsApi = (params: StatisticsQueryParams) => {
    return http.get<CoreIndicatorsResponse>(ADMIN_MODULE + `/statistics/core-indicators`, params);
};

/**
 * 获取销售趋势
 * @param params 查询参数
 * @returns 销售趋势数据
 */
export const getSalesTrendApi = (params: StatisticsQueryParams) => {
    return http.get<SalesTrendResponse>(ADMIN_MODULE + `/statistics/sales-trend`, params);
};

/**
 * 获取菜品销售排行
 * @param params 查询参数
 * @returns 菜品排行数据
 */
export const getDishRankingApi = (params: StatisticsQueryParams) => {
    return http.get<DishRankingResponse>(ADMIN_MODULE + `/statistics/dish-ranking`, params);
};

/**
 * 获取订单状态分布
 * @param params 查询参数
 * @returns 订单状态分布数据
 */
export const getOrderStatusDistributionApi = (params: StatisticsQueryParams) => {
    return http.get<OrderStatusResponse>(ADMIN_MODULE + `/statistics/order-status-distribution`, params);
};

/**
 * 获取利润分析
 * @param params 查询参数
 * @returns 利润分析数据
 */
export const getProfitAnalysisApi = (params: StatisticsQueryParams) => {
    return http.get<ProfitAnalysisResponse>(ADMIN_MODULE + `/statistics/profit-analysis`, params);
};

/**
 * 获取详细数据
 * @param params 查询参数
 * @returns 详细数据
 */
export const getDetailedDataApi = (params: StatisticsQueryParams) => {
    return http.get<DetailedDataResponse>(ADMIN_MODULE + `/statistics/detailed-data`, params);
};

/**
 * 获取菜品利润率排行
 * @param params 查询参数
 * @returns 菜品利润率排行数据
 */
export const getDishProfitRankingApi = (params: StatisticsQueryParams) => {
    return http.get<DishRankingResponse>(ADMIN_MODULE + `/statistics/dish-profit-ranking`, params);
};