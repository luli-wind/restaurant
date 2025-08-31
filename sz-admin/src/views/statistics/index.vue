<template>
  <div class="statistics-container">

    <!-- 筛选条件区域 -->
    <el-card class="filter-card">
      <div class="filter-content">
        <el-form :model="filterForm" label-width="80px" inline>
          <el-form-item label="时间范围">
            <el-date-picker
              v-model="filterForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
              @change="handleDateRangeChange"
            />
          </el-form-item>
          <el-form-item label="统计维度">
            <el-select v-model="filterForm.dimension" @change="handleDimensionChange">
              <el-option label="按天" value="day" />
              <el-option label="按周" value="week" />
              <el-option label="按月" value="month" />
              <el-option label="按季度" value="quarter" />
            </el-select>
          </el-form-item>
          <el-form-item label="餐厅区域">
            <el-select v-model="filterForm.areaId" placeholder="请选择区域" clearable>
              <el-option
                v-for="area in areaOptions"
                :key="area.id"
                :label="area.name"
                :value="area.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="refreshData">查询</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>

    <!-- 核心指标区域 -->
    <el-row :gutter="20" class="metrics-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card sales-card">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="28" color="#fff"><Money /></el-icon>
            </div>
            <div class="card-info">
              <p class="card-title">销售总额</p>
              <p class="card-number">¥{{ formatNumber(coreMetrics.totalSales) }}</p>
              <p class="card-change" :class="getChangeClass(coreMetrics.salesChange)">
                <el-icon v-if="coreMetrics.salesChange > 0"><Top /></el-icon>
                <el-icon v-else-if="coreMetrics.salesChange < 0"><Bottom /></el-icon>
                {{ Math.abs(coreMetrics.salesChange) }}% 同比
              </p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card order-card">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="28" color="#fff"><Document /></el-icon>
            </div>
            <div class="card-info">
              <p class="card-title">订单数量</p>
              <p class="card-number">{{ coreMetrics.orderCount }}</p>
              <p class="card-change" :class="getChangeClass(coreMetrics.orderChange)">
                <el-icon v-if="coreMetrics.orderChange > 0"><Top /></el-icon>
                <el-icon v-else-if="coreMetrics.orderChange < 0"><Bottom /></el-icon>
                {{ Math.abs(coreMetrics.orderChange) }}% 同比
              </p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card customer-card">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="28" color="#fff"><User /></el-icon>
            </div>
            <div class="card-info">
              <p class="card-title">客户数量</p>
              <p class="card-number">{{ coreMetrics.customerCount }}</p>
              <p class="card-change" :class="getChangeClass(coreMetrics.customerChange)">
                <el-icon v-if="coreMetrics.customerChange > 0"><Top /></el-icon>
                <el-icon v-else-if="coreMetrics.customerChange < 0"><Bottom /></el-icon>
                {{ Math.abs(coreMetrics.customerChange) }}% 同比
              </p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="metric-card avg-card">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="28" color="#fff"><Wallet /></el-icon>
            </div>
            <div class="card-info">
              <p class="card-title">平均客单价</p>
              <p class="card-number">¥{{ formatNumber(coreMetrics.avgOrderAmount) }}</p>
              <p class="card-change" :class="getChangeClass(coreMetrics.avgChange)">
                <el-icon v-if="coreMetrics.avgChange > 0"><Top /></el-icon>
                <el-icon v-else-if="coreMetrics.avgChange < 0"><Bottom /></el-icon>
                {{ Math.abs(coreMetrics.avgChange) }}% 同比
              </p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表展示区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 销售趋势图表 -->
      <el-col :xs="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
              <div class="chart-controls">
                <el-radio-group v-model="salesTrendType" @change="loadSalesTrendData">
                  <el-radio-button label="amount">销售额</el-radio-button>
                  <el-radio-button label="count">订单数</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </template>
          <div class="chart-container">
            <SimpleBarChart
              title="销售趋势"
              :data="salesTrendData"
              :max-value="salesTrendMaxValue"
            />
          </div>
        </el-card>
      </el-col>

      <!-- 利润分析图表 -->
      <el-col :xs="24" :md="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>利润分析</span>
            </div>
          </template>
          <div class="chart-container">
            <ProfitAnalysisChart :data="profitAnalysisData" />
          </div>
        </el-card>
      </el-col>

      <!-- 菜品销售排行 -->
      <el-col :xs="24" :md="12">
        <el-card class="chart-card">
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
            <SimpleBarChart
              title="菜品销售排行"
              :data="dishRankingData"
              horizontal
            />
          </div>
        </el-card>
      </el-col>

      <!-- 订单状态分布 -->
      <el-col :xs="24" :md="12">
        <el-card class="chart-card">
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
    <el-card class="data-table-card">
      <template #header>
        <div class="card-header">
          <span>详细统计数据</span>
          <div class="table-controls">
            <el-button @click="exportData">导出数据</el-button>
          </div>
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
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
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
import SimpleBarChart from './components/SimpleBarChart.vue'
import SimplePieChart from './components/SimplePieChart.vue'
import ProfitAnalysisChart from './components/ProfitAnalysisChart.vue'

// 筛选表单数据
const filterForm = reactive({
  dateRange: [],
  dimension: 'day',
  areaId: undefined
})

// 餐厅区域选项
const areaOptions = ref([
  { id: 1, name: '大厅区域' },
  { id: 2, name: '包间区域' },
  { id: 3, name: '户外区域' },
  { id: 4, name: 'VIP区域' }
])

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
  { label: '宫保鸡丁', value: 120 },
  { label: '麻婆豆腐', value: 95 },
  { label: '红烧肉', value: 80 },
  { label: '糖醋里脊', value: 70 },
  { label: '鱼香肉丝', value: 65 }
])

// 订单状态分布数据
const orderStatusData = ref([
  { label: '已完成', value: 120, percentage: 60 },
  { label: '制作中', value: 40, percentage: 20 },
  { label: '待处理', value: 25, percentage: 12.5 },
  { label: '已取消', value: 15, percentage: 7.5 }
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
const detailData = ref([
  { 
    id: 1, 
    date: '2023-08-01', 
    salesAmount: 1200.00, 
    orderCount: 25, 
    customerCount: 30, 
    avgOrderAmount: 48.00, 
    profitMargin: 48 
  },
  { 
    id: 2, 
    date: '2023-08-02', 
    salesAmount: 1500.00, 
    orderCount: 32, 
    customerCount: 38, 
    avgOrderAmount: 46.88, 
    profitMargin: 45 
  },
  { 
    id: 3, 
    date: '2023-08-03', 
    salesAmount: 890.00, 
    orderCount: 18, 
    customerCount: 22, 
    avgOrderAmount: 49.44, 
    profitMargin: 50 
  },
  { 
    id: 4,
    date: '2023-08-04', 
    salesAmount: 2100.00, 
    orderCount: 42, 
    customerCount: 50, 
    avgOrderAmount: 50.00, 
    profitMargin: 52 
  },
  { 
    id: 5, 
    date: '2023-08-05', 
    salesAmount: 3200.00, 
    orderCount: 65, 
    customerCount: 75, 
    avgOrderAmount: 49.23, 
    profitMargin: 50 
  }
])

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 30
})

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

// 处理日期范围变化
const handleDateRangeChange = (value: [string, string] | null) => {
  console.log('日期范围变化:', value)
  // 这里可以触发数据重新加载
}

// 处理维度变化
const handleDimensionChange = (value: string) => {
  console.log('统计维度变化:', value)
  // 这里可以触发数据重新加载
}

// 刷新数据
const refreshData = () => {
  ElMessage.success('数据刷新成功')
  // 这里应该重新加载所有数据
}

// 重置筛选条件
const resetFilter = () => {
  filterForm.dateRange = []
  filterForm.dimension = 'day'
  filterForm.areaId = undefined
  ElMessage.info('筛选条件已重置')
}

// 加载销售趋势数据
const loadSalesTrendData = () => {
  console.log('加载销售趋势数据，类型:', salesTrendType.value)
  // 这里应该根据类型加载不同的数据并更新图表
}

// 加载菜品排行数据
const loadDishRankingData = () => {
  console.log('加载菜品排行数据，类型:', dishRankingType.value)
  // 这里应该根据类型加载不同的数据并更新图表
}

// 导出数据
const exportData = () => {
  ElMessage.success('数据导出功能开发中')
}

// 处理分页大小变化
const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  console.log('分页大小变化:', val)
}

// 处理当前页变化
const handleCurrentChange = (val: number) => {
  pagination.currentPage = val
  console.log('当前页变化:', val)
}

onMounted(() => {
  console.log('统计页面初始化完成')
})
</script>

<style scoped lang="scss">
@use './styles/statistics';
</style>