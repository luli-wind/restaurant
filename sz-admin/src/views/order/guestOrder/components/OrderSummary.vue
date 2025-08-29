<template>
  <div class="order-summary-overlay" @click="closeSummary">
    <div class="order-summary-container" @click.stop>
      <div class="summary-header">
        <h2>订单提交成功</h2>
        <el-button 
          type="danger" 
          :icon="Close" 
          circle 
          @click="closeSummary"
        />
      </div>

      <div class="summary-content">
        <div class="order-info">
          <div class="info-item">
            <span class="label">订单号:</span>
            <span class="value">{{ orderData.orderNumber }}</span>
          </div>
          <div class="info-item">
            <span class="label">下单时间:</span>
            <span class="value">{{ formatDate(orderData.createTime) }}</span>
          </div>
          <div class="info-item">
            <span class="label">客户姓名:</span>
            <span class="value">{{ orderData.customerName }}</span>
          </div>
          <div class="info-item">
            <span class="label">联系电话:</span>
            <span class="value">{{ orderData.customerPhone }}</span>
          </div>
          <div class="info-item">
            <span class="label">配送地址:</span>
            <span class="value">{{ orderData.deliveryAddress }}</span>
          </div>
          <div class="info-item" v-if="orderData.remark">
            <span class="label">备注:</span>
            <span class="value">{{ orderData.remark }}</span>
          </div>
        </div>

        <div class="order-items">
          <h3>订单详情</h3>
          <div 
            v-for="item in orderData.items" 
            :key="item.dishId" 
            class="item-row"
          >
            <div class="item-name">{{ item.dishName }}</div>
            <div class="item-quantity">x{{ item.quantity }}</div>
            <div class="item-price">¥{{ (Number(item.price) * item.quantity).toFixed(2) }}</div>
          </div>
        </div>

        <div class="order-total">
          <div class="total-label">总计:</div>
          <div class="total-value">¥{{ Number(orderData.totalAmount).toFixed(2) }}</div>
        </div>
      </div>

      <div class="summary-footer">
        <el-button 
          type="primary" 
          @click="closeSummary"
          size="large"
          style="width: 100%"
        >
          继续点餐
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Close } from '@element-plus/icons-vue'
import type { OrderData } from '../types/index'

// 定义props
const props = defineProps<{
  orderData: OrderData
}>()

// 定义emits
const emit = defineEmits<{
  (e: 'close-summary'): void
}>()

// 格式化日期
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

// 关闭订单汇总
const closeSummary = () => {
  emit('close-summary')
}
</script>

<style scoped lang="scss">
.order-summary-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.order-summary-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.2);
  width: 600px;
  max-width: 90%;
  max-height: 90%;
  display: flex;
  flex-direction: column;
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
  
  h2 {
    margin: 0;
    font-size: 24px;
    color: #333;
  }
}

.summary-content {
  padding: 20px;
  flex: 1;
  overflow-y: auto;
}

.order-info {
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  margin-bottom: 12px;
  
  .label {
    width: 80px;
    font-weight: bold;
    color: #666;
  }
  
  .value {
    flex: 1;
    color: #333;
  }
}

.order-items {
  margin-bottom: 20px;
  
  h3 {
    margin: 0 0 16px 0;
    font-size: 18px;
    color: #333;
  }
}

.item-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  
  .item-name {
    flex: 1;
  }
  
  .item-quantity {
    width: 60px;
    text-align: center;
  }
  
  .item-price {
    width: 80px;
    text-align: right;
    font-weight: bold;
  }
}

.order-total {
  display: flex;
  justify-content: flex-end;
  padding: 16px 0;
  border-top: 1px solid #eee;
  
  .total-label {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    margin-right: 16px;
  }
  
  .total-value {
    font-size: 24px;
    font-weight: bold;
    color: #ff6600;
  }
}

.summary-footer {
  padding: 20px;
  border-top: 1px solid #eee;
}
</style>