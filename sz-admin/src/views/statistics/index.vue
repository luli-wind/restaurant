<template>
  <div class="statistics-container">

    <!-- 核心指标区域 -->
    <el-row :gutter="20" class="metrics-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card sales-card" v-loading="loading.coreIndicators">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="28" color="#fff"><Money /></el-icon>
            </div>
            <div class="card-info">
              <p class="card-title">销售总额</p>
              <p class="card-number">¥{{ formatNumber(coreMetrics.totalSales) }}</p>
              <p class="card-change" :class="getChangeClass(coreMetrics.salesChange)">
              </p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card order-card" v-loading="loading.coreIndicators">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="28" color="#fff"><Document /></el-icon>
            </div>
            <div class="card-info">
              <p class="card-title">订单数量</p>
              <p class="card-number">{{ coreMetrics.orderCount }}</p>
              <p class="card-change" :class="getChangeClass(coreMetrics.orderChange)">
              </p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card customer-card" v-loading="loading.coreIndicators">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="28" color="#fff"><User /></el-icon>
            </div>
            <div class="card-info">
              <p class="card-title">客户数量</p>
              <p class="card-number">{{ coreMetrics.customerCount }}</p>
              <p class="card-change" :class="getChangeClass(coreMetrics.customerChange)">
              </p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card avg-card" v-loading="loading.coreIndicators">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="28" color="#fff"><Wallet /></el-icon>
            </div>
            <div class="card-info">
              <p class="card-title">平均客单价</p>
              <p class="card-number">¥{{ formatNumber(coreMetrics.avgOrderAmount) }}</p>
              <p class="card-change" :class="getChangeClass(coreMetrics.avgChange)">
              </p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表展示区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 销售趋势图表 -->
      <el-col :xs="24" :md="24">
        <el-card class="chart-card" v-loading="loading.salesTrend">
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
              <div class="chart-controls">
                <el-radio-group v-model="salesTrendType" @change="loadSalesTrendData">
                  <el-radio-button label="amount">销售额</el-radio-button>
                  <el-radio-button label="quantity">订单数</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </template>
          <div class="chart-container">
            <EChartsLineChart
              title="销售趋势"
              :data="salesTrendData"
              :max-value="salesTrendMaxValue"
            />
          </div>
        </el-card>
      </el-col>

<!--      &lt;!&ndash; 利润分析图表 &ndash;&gt;-->
<!--      <el-col :xs="24" :md="12">-->
<!--        <el-card class="chart-card" v-loading="loading.profitAnalysis">-->
<!--          <template #header>-->
<!--            <div class="card-header">-->
<!--              <span>利润分析</span>-->
<!--            </div>-->
<!--          </template>-->
<!--          <div class="chart-container">-->
<!--            <ProfitAnalysisChart :data="profitAnalysisData" />-->
<!--          </div>-->
<!--        </el-card>-->
<!--      </el-col>-->

      <!-- 菜品销售排行 -->
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" v-loading="loading.dishRanking">
          <template #header>
            <div class="card-header">
              <span>菜品销售排行</span>
              <div class="chart-controls">
                <el-radio-group v-model="dishRankingType" @change="loadDishRankingData">
                  <el-radio-button label="quantity">按数量</el-radio-button>
                  <el-radio-button label="amount">按金额</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </template>
          <div class="chart-container">
            <EChartsBarChart
              title="菜品销售排行"
              :data="dishRankingData"
              horizontal
            />
          </div>
        </el-card>
      </el-col>

      <!-- 订单状态分布 -->
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" v-loading="loading.orderStatus">
          <template #header>
            <div class="card-header">
              <span>订单状态分布</span>
            </div>
          </template>
          <div class="chart-container">
            <SimplePieChart
              title="订单状态分布"
              :data="orderStatusData"
            />
          </div>
        </el-card>
      </el-col>

    </el-row>

    <!-- 详细数据表格 -->
    <el-card class="data-table-card" v-loading="loading.detailedData">
      <template #header>
        <div class="card-header">
          <span>详细统计数据</span>
        </div>
      </template>
      <el-table :data="detailData" style="width: 100%" border>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="salesAmount" label="销售额" width="120">
          <template #default="scope">
            ¥{{ formatNumber(scope.row.salesAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="orderCount" label="订单数" width="100" />
        <el-table-column prop="customerCount" label="客户数" width="100" />
        <el-table-column prop="avgOrderAmount" label="客单价" width="120">
          <template #default="scope">
            ¥{{ formatNumber(scope.row.avgOrderAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="profitMargin" label="利润率" width="120">
          <template #default="scope">
            {{ scope.row.profitMargin }}%
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
<!--        <el-pagination-->
<!--          v-model:current-page="pagination.currentPage"-->
<!--          v-model:page-size="pagination.pageSize"-->
<!--          :page-sizes="[10, 20, 50, 100]"-->
<!--          :total="pagination.total"-->
<!--          layout="total, sizes, prev, pager, next, jumper"-->
<!--          @size-change="handleSizeChange"-->
<!--          @current-change="handleCurrentChange"-->
<!--        />-->
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Money,
  Document,
  User,
  Wallet,
  Top,
  Bottom
} from '@element-plus/icons-vue'
import SimplePieChart from './components/SimplePieChart.vue'
import ProfitAnalysisChart from './components/ProfitAnalysisChart.vue'
import EChartsBarChart from './components/EChartsBarChart.vue'
import EChartsLineChart from './components/EChartsLineChart.vue'
import type {StatisticsQueryParams} from "@/api/types/statistics/statistics";
import {
  getCoreIndicatorsApi,
  getSalesTrendApi,
  getDishRankingApi,
  getOrderStatusDistributionApi,
  getProfitAnalysisApi,
  getDetailedDataApi
} from '@/api/modules/statistics/statistics';

// 核心指标数据
const coreMetrics = ref({
  totalSales: 12865.00,
  salesChange: 12.5,
  orderCount: 128,
  orderChange: 8.3,
  customerCount: 42,
  customerChange: 5.7,
  avgOrderAmount: 100.51,
  avgChange: 3.2
})

// 销售趋势数据
const salesTrendData = ref([
  { label: '08-01', value: 1200 },
  { label: '08-02', value: 1500 },
  { label: '08-03', value: 890 },
  { label: '08-04', value: 2100 },
  { label: '08-05', value: 3200 },
  { label: '08-06', value: 1800 },
  { label: '08-07', value: 2300 }
])

// 销售趋势最大值
const salesTrendMaxValue = ref(3500)

// 菜品销售排行数据
const dishRankingData = ref([
])

// 订单状态分布数据
const orderStatusData = ref([
])

// 利润分析数据
const profitAnalysisData = ref([
  { date: '2023-08-01', revenue: 1200, cost: 600, profit: 600, profitMargin: 50 },
  { date: '2023-08-02', revenue: 1500, cost: 750, profit: 750, profitMargin: 50 },
  { date: '2023-08-03', revenue: 890, cost: 445, profit: 445, profitMargin: 50 },
  { date: '2023-08-04', revenue: 2100, cost: 1050, profit: 1050, profitMargin: 50 },
  { date: '2023-08-05', revenue: 3200, cost: 1600, profit: 1600, profitMargin: 50 },
  { date: '2023-08-06', revenue: 1800, cost: 900, profit: 900, profitMargin: 50 },
  { date: '2023-08-07', revenue: 2300, cost: 1150, profit: 1150, profitMargin: 50 }
])

// 图表类型控制
const salesTrendType = ref('amount')
const dishRankingType = ref('quantity')

// 详细数据表格
const detailData = ref([])

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 30
})

// 加载状态
const loading = reactive({
  coreIndicators: false,
  salesTrend: false,
  dishRanking: false,
  orderStatus: false,
  profitAnalysis: false,
  detailedData: false
})

// 错误信息
const error = ref<string | null>(null)

// 格式化数字
const formatNumber = (value: number): string => {
  return value.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 获取变化率的样式类
const getChangeClass = (change: number): string => {
  if (change > 0) return 'positive'
  if (change < 0) return 'negative'
  return 'neutral'
}

// 简化查询参数
const getQueryParams = (): StatisticsQueryParams => {
  return {
    dimension: 'day',
    page: pagination.currentPage,
    size: pagination.pageSize
  }
}

// 加载核心指标数据
const loadCoreIndicators = async () => {
  try {
    loading.coreIndicators = true
    error.value = null
    const params = getQueryParams()
    const response = await getCoreIndicatorsApi(params)
    const data = response.data
    
    coreMetrics.value = {
      totalSales: data.totalSales,
      salesChange: 12.5,
      orderCount: data.orderCount,
      orderChange: 8.3,
      customerCount: data.customerCount,
      customerChange: 5.7,
      avgOrderAmount: data.avgOrderAmount,
      avgChange: 3.2
    }
  } catch (err) {
    console.error('加载核心指标失败:', err)
    error.value = '加载核心指标失败'
    ElMessage.error('加载核心指标失败')
  } finally {
    loading.coreIndicators = false
  }
}

// 加载销售趋势数据
const loadSalesTrend = async () => {
  try {
    loading.salesTrend = true
    error.value = null
    const params = getQueryParams()
    params.type = salesTrendType.value as 'amount' | 'quantity'
    
    const response = await getSalesTrendApi(params)
    const data = response.data
    
    salesTrendData.value = data.map(item => ({
      label: item.date,
      value: salesTrendType.value === 'amount' ? Math.round(item.amount * 100) / 100 : item.count || 0
    }))
    
    salesTrendMaxValue.value = Math.max(...salesTrendData.value.map(item => item.value)) * 1.2
  } catch (err) {
    console.error('加载销售趋势失败:', err)
    error.value = '加载销售趋势失败'
    ElMessage.error('加载销售趋势失败')
  } finally {
    loading.salesTrend = false
  }
}

// 加载菜品排行数据
const loadDishRanking = async () => {
  try {
    loading.dishRanking = true
    error.value = null
    const params = getQueryParams()
    params.type = dishRankingType.value as 'quantity' | 'amount'
    params.limit = 10
    
    const response = await getDishRankingApi(params)
    const data = response.data
    
    dishRankingData.value = data.map(item => ({
      label: item.dishName,
      value: dishRankingType.value === 'quantity' ? item.quantity : item.amount
    }))
  } catch (err) {
    console.error('加载菜品排行失败:', err)
    error.value = '加载菜品排行失败'
    ElMessage.error('加载菜品排行失败')
  } finally {
    loading.dishRanking = false
  }
}

// 加载订单状态分布数据
const loadOrderStatusDistribution = async () => {
  try {
    loading.orderStatus = true
    error.value = null
    const params = getQueryParams()
    
    const response = await getOrderStatusDistributionApi(params)
    const data = response.data
    
    orderStatusData.value = data.map(item => ({
      label: item.statusName,
      value: item.count,
      percentage: item.percentage
    }))
  } catch (err) {
    console.error('加载订单状态分布失败:', err)
    error.value = '加载订单状态分布失败'
    ElMessage.error('加载订单状态分布失败')
  } finally {
    loading.orderStatus = false
  }
}

// 加载利润分析数据
const loadProfitAnalysis = async () => {
  try {
    loading.profitAnalysis = true
    error.value = null
    const params = getQueryParams()
    
    const response = await getProfitAnalysisApi(params)
    const data = response.data
    console.log(data)
    
    profitAnalysisData.value = data
  } catch (err) {
    console.error('加载利润分析失败:', err)
    error.value = '加载利润分析失败'
    ElMessage.error('加载利润分析失败')
  } finally {
    loading.profitAnalysis = false
  }
}

// 加载详细数据
const loadDetailedData = async () => {
  try {
    loading.detailedData = true
    error.value = null
    const params = getQueryParams()
    
    const response = await getDetailedDataApi(params)
    const data = response.data
    
    detailData.value = data.rows.map((item, index) => ({
      id: index + 1,
      date: item.orderTime,
      salesAmount: item.totalAmount,
      orderCount: item.orderCount,
      customerCount: item.customerCount,
      avgOrderAmount: item.totalAmount/item.orderCount,
      profitMargin: item.profitMargin
    }))
    
    pagination.total = data.total
  } catch (err) {
    console.error('加载详细数据失败:', err)
    error.value = '加载详细数据失败'
    ElMessage.error('加载详细数据失败')
  } finally {
    loading.detailedData = false
  }
}

// 加载所有数据
const loadAllData = async () => {
  await Promise.all([
    loadCoreIndicators(),
    loadSalesTrend(),
    loadDishRanking(),
    loadOrderStatusDistribution(),
    loadProfitAnalysis(),
    loadDetailedData()
  ])
}

// 加载销售趋势数据
const loadSalesTrendData = () => {
  loadSalesTrend()
}

// 加载菜品排行数据
const loadDishRankingData = () => {
  loadDishRanking()
}

// 导出数据
const exportData = () => {
  ElMessage.success('数据导出功能开发中')
}

// 处理分页大小变化
const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  pagination.currentPage = 1
  loadDetailedData()
}

// 处理当前页变化
const handleCurrentChange = (val: number) => {
  pagination.currentPage = val
  loadDetailedData()
}

onMounted(() => {
  loadAllData()
})
</script>

<style scoped lang="scss">
@use './styles/statistics';
</style>