<template>
  <div class="home-container">
    <!-- 数据概览 -->
    <el-row :gutter="20" class="dashboard-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="dashboard-card income-card">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="28" color="#fff"><Money /></el-icon>
            </div>
            <div class="card-info">
              <p class="card-title">今日收入</p>
              <p class="card-number">¥{{ dashboardData.todayIncome }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="dashboard-card order-card">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="28" color="#fff"><Document /></el-icon>
            </div>
            <div class="card-info">
              <p class="card-title">今日订单</p>
              <p class="card-number">{{ dashboardData.todayOrders }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="dashboard-card table-card">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="28" color="#fff"><ForkSpoon /></el-icon>
            </div>
            <div class="card-info">
              <p class="card-title">餐桌使用率</p>
              <p class="card-number">{{ dashboardData.tableUsageRate }}%</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="dashboard-card customer-card">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="28" color="#fff"><User /></el-icon>
            </div>
            <div class="card-info">
              <p class="card-title">当前顾客</p>
              <p class="card-number">{{ dashboardData.currentCustomers }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷操作和餐厅状态 -->
    <el-row :gutter="20" class="main-content-row">
      <!-- 快捷操作 -->
      <el-col :xs="24" :md="16">
        <el-card class="quick-actions-card">
          <template #header>
            <div class="card-header">
              <span>快捷操作</span>
            </div>
          </template>
          <div class="quick-actions-content">
            <el-row :gutter="20">
              <el-col :span="6" v-for="action in quickActions" :key="action.title">
                <div class="action-item" @click="handleAction(action)">
                  <div class="action-icon" :class="action.iconClass">
                    <el-icon :size="20" color="#fff"><component :is="action.icon" /></el-icon>
                  </div>
                  <p class="action-title">{{ action.title }}</p>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-card>

        <!-- 最近订单 -->
        <el-card class="recent-orders-card">
          <template #header>
            <div class="card-header">
              <span>最近订单</span>
              <el-button link @click="viewAllOrders">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="orderNumber" label="订单号" width="120" />
            <el-table-column prop="tableName" label="桌号" width="80" />
            <el-table-column prop="totalAmount" label="金额" width="100">
              <template #default="scope">
                ¥{{ scope.row.totalAmount }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getOrderStatusType(scope.row.status)">
                  {{ getOrderStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="下单时间" />
          </el-table>
        </el-card>
      </el-col>

      <!-- 餐厅状态 -->
      <el-col :xs="24" :md="8">
        <el-card class="restaurant-status-card">
          <template #header>
            <div class="card-header">
              <span>餐厅状态</span>
            </div>
          </template>
          <div class="status-content">
            <div class="status-header">
              <h3>餐桌使用情况</h3>
              <p>总餐桌数: {{ tableStatus.totalTables }}</p>
            </div>
            <div class="table-status-grid">
              <div 
                v-for="table in tableStatus.tables" 
                :key="table.id"
                class="table-item"
                :class="getTableItemClass(table.status)"
              >
                <div class="table-number">{{ table.name }}</div>
                <div class="table-status">{{ getTableStatusText(table.status) }}</div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 通知中心 -->
        <el-card class="notification-card">
          <template #header>
            <div class="card-header">
              <span>通知中心</span>
            </div>
          </template>
          <div class="notification-content">
            <div 
              v-for="notification in notifications" 
              :key="notification.id"
              class="notification-item"
              :class="{ unread: !notification.read }"
            >
              <div class="notification-icon">
                <el-icon :size="14" color="#666"><component :is="notification.icon" /></el-icon>
              </div>
              <div class="notification-text">
                <p class="notification-title">{{ notification.title }}</p>
                <p class="notification-time">{{ notification.time }}</p>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Money,
  Document,
  ForkSpoon,
  User,
  Plus,
  Dish,
  Desk,
  Avatar
} from '@element-plus/icons-vue'

// 模拟数据
const dashboardData = ref({
  todayIncome: '12,865.00',
  todayOrders: 128,
  tableUsageRate: 72,
  currentCustomers: 42
})

const quickActions = ref([
  { title: '新增订单', icon: Plus, iconClass: 'add-order-icon', path: '/order/dineInOrder' },
  { title: '菜品管理', icon: Dish, iconClass: 'dish-icon', path: '/restaurant/dish' },
  { title: '餐桌管理', icon: Desk, iconClass: 'table-icon', path: '/restaurant/diningTable' },
  { title: '员工管理', icon: Avatar, iconClass: 'staff-icon', path: '/system/accountManage' }
])

const recentOrders = ref([
  { id: 1, orderNumber: 'D20230801001', tableName: 'A01', totalAmount: 268.00, status: 'completed', createTime: '2023-08-01 12:30:25' },
  { id: 2, orderNumber: 'D20230801002', tableName: 'B03', totalAmount: 156.50, status: 'preparing', createTime: '2023-08-01 12:45:10' },
  { id: 3, orderNumber: 'D20230801003', tableName: 'C02', totalAmount: 89.00, status: 'pending', createTime: '2023-08-01 13:10:45' },
  { id: 4, orderNumber: 'D20230801004', tableName: 'A05', totalAmount: 324.80, status: 'completed', createTime: '2023-08-01 13:25:30' },
  { id: 5, orderNumber: 'D20230801005', tableName: 'B01', totalAmount: 198.00, status: 'served', createTime: '2023-08-01 13:40:15' }
])

const tableStatus = ref({
  totalTables: 20,
  tables: [
    { id: 1, name: 'A01', status: 'occupied' },
    { id: 2, name: 'A02', status: 'available' },
    { id: 3, name: 'A03', status: 'occupied' },
    { id: 4, name: 'B01', status: 'reserved' },
    { id: 5, name: 'B02', status: 'occupied' },
    { id: 6, name: 'B03', status: 'available' },
    { id: 7, name: 'C01', status: 'occupied' },
    { id: 8, name: 'C02', status: 'occupied' }
  ]
})

const notifications = ref([
  { id: 1, title: '新订单提醒 #D20230801006', time: '5分钟前', icon: Document, read: false },
  { id: 2, title: '餐桌A05需要清洁', time: '15分钟前', icon: Desk, read: false },
  { id: 3, title: '菜品库存不足提醒', time: '1小时前', icon: Dish, read: true },
  { id: 4, title: '系统维护通知', time: '2小时前', icon: Avatar, read: true }
])

const router = useRouter()

// 处理快捷操作点击
const handleAction = (action: any) => {
  if (action.path) {
    router.push(action.path)
  } else {
    ElMessage.info(`功能开发中: ${action.title}`)
  }
}

// 查看所有订单
const viewAllOrders = () => {
  router.push('/order/dineInOrder')
}

// 获取订单状态类型
const getOrderStatusType = (status: string) => {
  const statusMap: Record<string, any> = {
    pending: 'info',
    preparing: 'warning',
    served: 'primary',
    completed: 'success',
    cancelled: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取订单状态文本
const getOrderStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: '待处理',
    preparing: '制作中',
    served: '已上菜',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取餐桌状态类名
const getTableItemClass = (status: string) => {
  const statusMap: Record<string, string> = {
    available: 'table-available',
    occupied: 'table-occupied',
    reserved: 'table-reserved',
    cleaning: 'table-cleaning'
  }
  return statusMap[status] || ''
}

// 获取餐桌状态文本
const getTableStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    available: '空闲',
    occupied: '使用中',
    reserved: '已预定',
    cleaning: '清洁中'
  }
  return statusMap[status] || '未知'
}

onMounted(() => {
  // 页面加载时可以请求真实数据
  console.log('首页数据加载完成')
})
</script>

<style scoped lang="scss">
@use './index';
</style>
