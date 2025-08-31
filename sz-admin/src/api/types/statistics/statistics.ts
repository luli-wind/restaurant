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
