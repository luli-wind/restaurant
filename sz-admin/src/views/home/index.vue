<template>
  <div class="home-container">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="welcome-content">
        <h1 class="welcome-title">欢迎使用餐厅管理系统</h1>
        <p class="welcome-subtitle">高效、智能、便捷的餐厅管理体验</p>
        <div class="welcome-stats">
          <div class="stat-item">
            <el-icon :size="20" color="#409EFF"><User /></el-icon>
            <span class="stat-text">当前用户: {{ currentUser }}</span>
          </div>
          <div class="stat-item">
            <el-icon :size="20" color="#67C23A"><Calendar /></el-icon>
            <span class="stat-text">今天是: {{ currentDate }}</span>
          </div>
        </div>
      </div>
      <div class="welcome-decoration">
        <div class="decoration-circle circle-1"></div>
        <div class="decoration-circle circle-2"></div>
        <div class="decoration-circle circle-3"></div>
      </div>
    </div>

    <!-- 餐厅状态和通知中心 -->
    <el-row :gutter="24" class="main-content-row">
      <el-col :xs="24" :md="15">

        <!-- 最近订单 -->
        <el-card class="recent-orders-card">
          <template #header>
            <div class="card-header">
              <span>最近订单</span>
            </div>
          </template>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="orderNumber" label="订单号" width="300" />
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
      <el-col :xs="24" :md="9">
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


      </el-col>
    </el-row>
             <!-- 美观内容展示区 -->
             <el-row :gutter="24" class="main-content-row">
               <el-col :xs="24">
                 <el-card class="gallery-card">
                   <template #header>
                     <div class="card-header">
                       <span>餐厅风采</span>
                     </div>
                   </template>
                   <div class="gallery-content">
                     <div class="gallery-item" v-for="item in galleryItems" :key="item.id">
                       <img :src="item.image" :alt="item.title" class="gallery-image" />
                       <div class="gallery-info">
                         <h3>{{ item.title }}</h3>
                         <p>{{ item.description }}</p>
                       </div>
                     </div>
                   </div>
                 </el-card>
               </el-col>
             </el-row>
           </div>
        </template>

<script setup lang="ts">
// 定义数据类型
interface OrderItem {
  id: string;
  orderNumber: string;
  tableName: string;
  totalAmount: number;
  status: string;
  createTime: string;
}

interface TableItem {
  id: string;
  name: string;
  status: string;
}

// 通知数据类型直接使用API返回的类型
import type { MessageRow } from '@/api/types/system/message';

import {ref, onMounted, computed} from 'vue'
import { useRouter } from 'vue-router'
import {
  Document,
  User,
  Calendar
} from '@element-plus/icons-vue'
import { getNoticeMessageListApi } from '@/api/modules/system/message'
import { getAllDiningTableListApi } from '@/api/modules/restaurant/diningTable'
import { getDineInOrderListApi } from '@/api/modules/order/dineInOrders'
import { useDict } from '@/hooks/useDict'
import { useDictOptions } from '@/hooks/useDictOptions'
import type { DictCustom } from '@/api/types/system/dict'
import {useUserStore} from "@/stores/modules/user";

// 使用useDict Hook 主动加载字典
useDict(['dining_table_status', 'dine_in_order_status']);

// 获取字典选项
const tableStatusOptions = useDictOptions('dining_table_status');
const orderStatusOptions = useDictOptions('dine_in_order_status');

// 欢迎页面数据
const userStore = useUserStore();
const currentUser = computed(() => userStore.userInfo.nickname)
const currentDate = ref(new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long'
}))

const recentOrders = ref<OrderItem[]>([])

const tableStatus = ref({
  totalTables: 12,
  tables: [] as TableItem[]
})
const notifications = ref<MessageRow[]>([]);

// 图片展示数据
const galleryItems = ref([
  {
    id: '1',
    title: '优雅用餐环境',
    description: '精心设计的用餐空间，为您提供舒适愉悦的用餐体验',
    image: 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=800&q=80'
  },
  {
    id: '2',
    title: '精致美食',
    description: '由资深厨师团队精心烹制，每一道菜都是艺术品',
    image: 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=800&q=80'
  },
  {
    id: '3',
    title: '专业服务',
    description: '训练有素的服务团队，为您提供贴心周到的服务',
    image: 'https://images.unsplash.com/photo-1550338861-b7cfeaf8ffd8?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=800&q=80'
  }
]);


// 获取订单状态类型
const getOrderStatusType = (status: string) => {
  const statusMap: Record<string, any> = {
    '2004001': 'info',
    '2004002': 'warning',
    '2004003': 'primary',
    '2004005': 'success',
    '2004004': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取订单状态文本
const getOrderStatusText = (status: string) => {
  if (!status) return '';
  const statusItem = orderStatusOptions.value.find((item: DictCustom) => item.id === status);
  return statusItem ? statusItem.codeName : '';
}

// 获取餐桌状态类名
const getTableItemClass = (status: string) => {
  const statusMap: Record<string, string> = {
    '2001001': 'table-available',
    '2001002': 'table-occupied',
    reserved: 'table-reserved',
    cleaning: 'table-cleaning'
  }
  return statusMap[status] || ''
}

// 获取餐桌状态文本
const getTableStatusText = (status: string) => {
  if (!status) return '';
  const statusItem = tableStatusOptions.value.find((item: DictCustom) => item.id === status);
  return statusItem ? statusItem.codeName : '';
}

onMounted(async () => {
  // 页面加载时可以请求真实数据
  console.log('首页数据加载完成')
  
  // 获取通知数据
  try {
    const res = await getNoticeMessageListApi()
    notifications.value = res.data
  } catch (error) {
    console.error('获取通知数据失败:', error)
  }
  
  // 获取餐桌状态数据
  try {
    const res = await getAllDiningTableListApi()
    // 转换数据格式以匹配现有结构
    tableStatus.value = {
      totalTables: res.data.length,
      tables: res.data.map((item: any) => ({
        id: item.tableId,
        name: item.tableName,
        status: item.status
      }))
    }
  } catch (error) {
    console.error('获取餐桌状态数据失败:', error)
  }
  
  // 获取订单数据
  try {
    const res = await getDineInOrderListApi({ page: 1, limit: 5 })
    // 转换数据格式以匹配现有结构
    recentOrders.value = res.data.rows.map((item: any) => ({
      id: item.id,
      orderNumber: item.orderNumber,
      tableName: item.tableName,
      totalAmount: item.totalAmount,
      status: item.status,
      createTime: item.createTime
    }));
  } catch (error) {
    console.error('获取订单数据失败:', error)
  }
})
</script>

<style scoped lang="scss">
@use './index';
</style>
