import request from '@/api/helper/request'
import type { AxiosResponse } from 'axios'

// 核心指标接口返回数据
export interface CoreIndicatorsResponse {
  totalSales: number
  orderCount: number
  customerCount: number
  avgOrderAmount: number
  tableUsageRate: number
}

// 销售趋势数据
export interface SalesTrendData {
  date: string
  amount: number
}

export interface SalesTrendResponse {
  trendData: SalesTrendData[]
}

// 菜品排行数据
export interface DishRankingData {
  dishId: number
  dishName: string
  quantity: number
  amount: number
}

export interface DishRankingResponse {
  rankingData: DishRankingData[]
}

// 订单状态数据
export interface OrderStatusData {
  status: string
  statusName: string
  count: number
  percentage: number
}

export interface OrderStatusResponse {
  statusData: OrderStatusData[]
}

// 利润分析数据
export interface ProfitAnalysisData {
  date: string
  revenue: number
  cost: number
  profit: number
  profitMargin: number
}

export interface ProfitAnalysisResponse {
  profitData: ProfitAnalysisData[]
}

// 详细数据
export interface DetailedRecord {
  date: string
  orderCount: number
  customerCount: number
  totalSales: number
  avgOrderAmount: number
  tableUsageRate: number
}

export interface DetailedDataResponse {
  records: DetailedRecord[]
  total: number
  page: number
  size: number
}

// 请求参数接口
export interface StatisticsQueryParams {
  startTime: string
  endTime: string
  dimension?: 'day' | 'week' | 'month' | 'quarter'
  areaId?: number
  page?: number
  size?: number
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
  type?: 'quantity' | 'amount'
  limit?: number
}

// 统计API服务
export const statisticsApi = {
  // 获取核心指标
  getCoreIndicators(params: StatisticsQueryParams): Promise<AxiosResponse<CoreIndicatorsResponse>> {
    return request({
      url: '/api/statistics/core-indicators',
      method: 'get',
      params
    })
  },

  // 获取销售趋势
  getSalesTrend(params: StatisticsQueryParams): Promise<AxiosResponse<SalesTrendResponse>> {
    return request({
      url: '/api/statistics/sales-trend',
      method: 'get',
      params
    })
  },

  // 获取菜品销售排行
  getDishRanking(params: StatisticsQueryParams): Promise<AxiosResponse<DishRankingResponse>> {
    return request({
      url: '/api/statistics/dish-ranking',
      method: 'get',
      params
    })
  },

  // 获取订单状态分布
  getOrderStatusDistribution(params: StatisticsQueryParams): Promise<AxiosResponse<OrderStatusResponse>> {
    return request({
      url: '/api/statistics/order-status-distribution',
      method: 'get',
      params
    })
  },

  // 获取利润分析
  getProfitAnalysis(params: StatisticsQueryParams): Promise<AxiosResponse<ProfitAnalysisResponse>> {
    return request({
      url: '/api/statistics/profit-analysis',
      method: 'get',
      params
    })
  },

  // 获取详细数据
  getDetailedData(params: StatisticsQueryParams): Promise<AxiosResponse<DetailedDataResponse>> {
    return request({
      url: '/api/statistics/detailed-data',
      method: 'get',
      params
    })
  },

  // 获取菜品利润率排行
  getDishProfitRanking(params: StatisticsQueryParams): Promise<AxiosResponse<DishRankingResponse>> {
    return request({
      url: '/api/statistics/dish-profit-ranking',
      method: 'get',
      params
    })
  }
}