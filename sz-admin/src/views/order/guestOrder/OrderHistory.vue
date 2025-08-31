<template>
  <div class="order-history-container">
    <div class="page-header">
      <h1>我的订单</h1>
      <p>查看您的历史订单记录</p>
    </div>

    <div class="order-list" v-if="orderList.length > 0">
      <div 
        class="order-item" 
        v-for="order in orderList" 
        :key="order.id"
        @click="viewOrderDetail(order)"
      >
        <div class="order-header">
          <div class="order-number">订单号: {{ order.orderNumber }}</div>
          <div class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </div>
        </div>
        <div class="order-content">
          <div class="order-info">
            <div class="info-item">
              <span class="label">下单时间:</span>
              <span>{{ order.createTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">配送地址:</span>
              <span>{{ order.deliveryAddress }}</span>
            </div>
            <div class="info-item">
              <span class="label">联系电话:</span>
              <span>{{ order.customerPhone }}</span>
            </div>
          </div>
          <div class="order-summary">
            <div class="dish-count">共{{ getOrderDishCount(order) }}件商品</div>
            <div class="order-amount">¥{{ order.totalAmount }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">📦</div>
      <p>暂无订单记录</p>
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog
      v-model="showOrderDetail"
      title="订单详情"
      width="600px"
      class="order-detail-dialog"
    >
      <div class="order-detail-content" v-if="selectedOrder">
        <div class="detail-header">
          <div class="order-number">订单号: {{ selectedOrder.orderNumber }}</div>
          <div class="order-status" :class="getStatusClass(selectedOrder.status)">
            {{ getStatusText(selectedOrder.status) }}
          </div>
        </div>
        
        <div class="detail-section">
          <h3>订单信息</h3>
          <div class="detail-info">
            <div class="info-item">
              <span class="label">下单时间:</span>
              <span>{{ selectedOrder.createTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">配送地址:</span>
              <span>{{ selectedOrder.deliveryAddress }}</span>
            </div>
            <div class="info-item">
              <span class="label">联系电话:</span>
              <span>{{ selectedOrder.customerPhone }}</span>
            </div>
            <div class="info-item" v-if="selectedOrder.remark">
              <span class="label">备注:</span>
              <span>{{ selectedOrder.remark }}</span>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h3>商品信息</h3>
          <div class="dish-list">
            <div 
              class="dish-item" 
              v-for="item in selectedOrder.orderItems" 
              :key="item.dishId"
            >
              <div class="dish-image">
                <img :src="item.imageUrl" :alt="item.dishName" v-if="item.imageUrl">
                <div class="placeholder" v-else>暂无图片</div>
              </div>
              <div class="dish-info">
                <div class="dish-name">{{ item.dishName }}</div>
                <div class="dish-price">¥{{ item.amount }}</div>
              </div>
              <div class="dish-quantity">x{{ item.number }}</div>
              <div class="dish-total">¥{{ (item.amount * item.number).toFixed(2) }}</div>
            </div>
          </div>
        </div>
        
        <div class="detail-section">
          <h3>费用明细</h3>
          <div class="fee-summary">
            <div class="fee-item">
              <span>商品总价:</span>
              <span>¥{{ calculateItemsTotal(selectedOrder) }}</span>
            </div>
            <div class="fee-item">
              <span>包装费:</span>
              <span>¥{{ selectedOrder.packagingFee || 0 }}</span>
            </div>
            <div class="fee-item">
              <span>配送费:</span>
              <span>¥{{ selectedOrder.deliveryFee || 0 }}</span>
            </div>
            <div class="fee-item total">
              <span>总计:</span>
              <span class="total-amount">¥{{ selectedOrder.totalAmount }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showOrderDetail = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getTakeAwayOrdersByThirdPartyUserApi } from '@/api/modules/order/takeAwayOrder'
import type { TakeAwayOrderRow } from '@/api/types/order/takeAwayOrder'

// 订单列表
const orderList = ref<TakeAwayOrderRow[]>([])

// 订单详情相关
const showOrderDetail = ref(false)
const selectedOrder = ref<TakeAwayOrderRow | null>(null)

// 获取订单列表
const loadOrderList = async () => {
  try {
    // 从本地存储获取第三方用户ID
    const thirdPartyUserInfo = JSON.parse(localStorage.getItem('thirdPartyUserInfo') || '{}');
    const thirdPartyUserId = thirdPartyUserInfo.id;
    
    if (!thirdPartyUserId) {
      console.error('未找到第三方用户ID');
      return;
    }
    
    const response = await getTakeAwayOrdersByThirdPartyUserApi({
      thirdPartyUserId: thirdPartyUserId.toString()
    })
    
    if (response.data && response.data.rows) {
      orderList.value = response.data.rows
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
  }
}

// 查看订单详情
const viewOrderDetail = (order: TakeAwayOrderRow) => {
  selectedOrder.value = order
  showOrderDetail.value = true
}

// 获取订单状态文本
const getStatusText = (status?: string) => {
  const statusMap: Record<string, string> = {
    'PENDING': '待处理',
    'CONFIRMED': '已确认',
    'PREPARING': '制作中',
    'DELIVERING': '配送中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status || ''] || '未知状态'
}

// 获取订单状态样式类
const getStatusClass = (status?: string) => {
  return `status-${(status || '').toLowerCase()}`
}

// 计算订单商品总数
const getOrderDishCount = (order: TakeAwayOrderRow) => {
  return order.orderItems?.reduce((total, item) => total + (item.number || 0), 0) || 0
}

// 计算商品总价
const calculateItemsTotal = (order: TakeAwayOrderRow) => {
  const itemsTotal = order.orderItems?.reduce((total, item) => {
    return total + (item.amount || 0) * (item.number || 0)
  }, 0) || 0
  
  // 减去配送费和包装费得到商品总价
  const deliveryFee = order.deliveryFee || 0
  const packagingFee = order.packagingFee || 0
  return (itemsTotal - deliveryFee - packagingFee).toFixed(2)
}

// 组件挂载时加载订单列表
onMounted(() => {
  loadOrderList()
})
</script>

<style scoped lang="scss">
.order-history-container {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.page-header {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  
  h1 {
    margin: 0 0 10px 0;
    font-size: 24px;
    color: #333;
  }
  
  p {
    margin: 0;
    color: #666;
    font-size: 14px;
  }
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.order-item {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.order-number {
  font-weight: bold;
  color: #333;
}

.order-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  
  &.status-pending {
    background-color: #fff3cd;
    color: #856404;
  }
  
  &.status-confirmed {
    background-color: #d1ecf1;
    color: #0c5460;
  }
  
  &.status-preparing {
    background-color: #cce7ff;
    color: #004085;
  }
  
  &.status-delivering {
    background-color: #d1ecf1;
    color: #0c5460;
  }
  
  &.status-completed {
    background-color: #d4edda;
    color: #155724;
  }
  
  &.status-cancelled {
    background-color: #f8d7da;
    color: #721c24;
  }
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-info {
  flex: 1;
  
  .info-item {
    display: flex;
    margin-bottom: 8px;
    
    .label {
      width: 80px;
      color: #666;
      font-size: 14px;
    }
    
    span {
      font-size: 14px;
      color: #333;
    }
  }
}

.order-summary {
  text-align: right;
  
  .dish-count {
    font-size: 14px;
    color: #666;
    margin-bottom: 5px;
  }
  
  .order-amount {
    font-size: 18px;
    font-weight: bold;
    color: #ff6600;
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  
  .empty-icon {
    font-size: 48px;
    margin-bottom: 20px;
  }
  
  p {
    font-size: 16px;
    color: #666;
    margin: 0;
  }
}

.order-detail-dialog {
  .order-detail-content {
    padding: 20px 0;
  }
  
  .detail-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid #eee;
  }
  
  .detail-section {
    margin-bottom: 25px;
    
    h3 {
      margin: 0 0 15px 0;
      font-size: 18px;
      color: #333;
      border-left: 4px solid #409eff;
      padding-left: 10px;
    }
    
    .detail-info {
      background: #f9f9f9;
      border-radius: 8px;
      padding: 15px;
      
      .info-item {
        display: flex;
        margin-bottom: 10px;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .label {
          width: 80px;
          color: #666;
          font-size: 14px;
        }
        
        span {
          font-size: 14px;
          color: #333;
          flex: 1;
        }
      }
    }
  }
  
  .dish-list {
    .dish-item {
      display: flex;
      align-items: center;
      padding: 15px 0;
      border-bottom: 1px solid #eee;
      
      &:last-child {
        border-bottom: none;
      }
      
      .dish-image {
        width: 60px;
        height: 60px;
        border-radius: 8px;
        overflow: hidden;
        margin-right: 15px;
        
        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
        
        .placeholder {
          width: 100%;
          height: 100%;
          background-color: #f0f0f0;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 12px;
          color: #999;
        }
      }
      
      .dish-info {
        flex: 1;
        
        .dish-name {
          font-weight: bold;
          margin-bottom: 5px;
          color: #333;
        }
        
        .dish-price {
          color: #ff6600;
          font-size: 14px;
        }
      }
      
      .dish-quantity {
        margin-right: 20px;
        color: #666;
        font-size: 14px;
      }
      
      .dish-total {
        color: #333;
        font-weight: bold;
        font-size: 14px;
      }
    }
  }
  
  .fee-summary {
    background: #f9f9f9;
    border-radius: 8px;
    padding: 15px;
    
    .fee-item {
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      &.total {
        border-top: 1px dashed #ddd;
        margin-top: 10px;
        padding-top: 10px;
        font-weight: bold;
        font-size: 16px;
        
        .total-amount {
          color: #ff6600;
          font-size: 18px;
        }
      }
    }
  }
}
</style>