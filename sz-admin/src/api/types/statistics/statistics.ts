// 核心指标接口返回数据
export interface CoreIndicatorsResponse {
    totalSales: number
    orderCount: number
    customerCount: number
    avgOrderAmount: number
}

// 销售趋势数据
export interface SalesTrendData {
    date: string
    amount: number
    count: number
}

export type SalesTrendResponse = SalesTrendData[]

// 菜品排行数据
export interface DishRankingData {
    dishId: number
    dishName: string
    quantity: number
    amount: number
}

export type DishRankingResponse = DishRankingData[]

// 订单状态数据
export interface OrderStatusData {
    status: string
    statusName: string
    count: number
    percentage: number
}

export type OrderStatusResponse = OrderStatusData[]

// 利润分析数据
export interface ProfitAnalysisData {
    date: string
    revenue: number
    cost: number
    profit: number
    profitMargin: number
}

export type ProfitAnalysisResponse = ProfitAnalysisData[]

// 详细数据
export interface DetailedRecord {
    orderId: number
    orderNumber: string
    orderTime: string
    totalAmount: number
    orderStatus: string
    orderCount:number
    customerCount:number
    profitMargin:number
    dishNames: string
}

export interface DetailedDataResponse {
    rows: DetailedRecord[]
    total: number
    current: number
    limit: number
    totalPage: number
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
