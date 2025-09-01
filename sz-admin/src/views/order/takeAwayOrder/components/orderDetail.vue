<template>
  <el-dialog
    v-model="visible"
    title="订单详情"
    width="800px"
    :before-close="handleClose"
    class="order-detail-dialog"
  >
    <div class="order-detail-container">
      <!-- 订单基本信息 -->
      <el-card class="order-info-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">订单信息</span>
          </div>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="info-item">
              <span class="info-label">订单号：</span>
              <span class="info-value">{{ orderData.orderNumber }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="info-label">客户姓名：</span>
              <span class="info-value">{{ orderData.customerName }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="info-label">客户电话：</span>
              <span class="info-value">{{ orderData.customerPhone }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="info-label">配送地址：</span>
              <span class="info-value">{{ orderData.deliveryAddress }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="info-label">包装费：</span>
              <span class="info-value">¥{{ orderData.packagingFee }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="info-label">配送费：</span>
              <span class="info-value">¥{{ orderData.deliveryFee }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="info-label">下单时间：</span>
              <span class="info-value">{{ orderData.createTime }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="info-label">订单状态：</span>
              <el-tag :type="getStatusType(orderData.status)" class="status-tag">
                {{ getStatusName(orderData.status) }}
              </el-tag>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="info-label">支付状态：</span>
              <el-tag :type="getPayStatusType(orderData.payStatus)" class="status-tag">
                {{ getPayStatusName(orderData.payStatus) }}
              </el-tag>
            </div>
          </el-col>
          <el-col v-if="orderData.payTime" :span="24">
            <div class="info-item">
              <span class="info-label">支付时间：</span>
              <span class="info-value">{{ orderData.payTime }}</span>
            </div>
          </el-col>
          <el-col v-if="orderData.remark" :span="24">
            <div class="info-item">
              <span class="info-label">订单备注：</span>
              <span class="info-value">{{ orderData.remark }}</span>
            </div>
          </el-col>
          <el-col v-if="orderData.refundReason" :span="24">
            <div class="info-item">
              <span class="info-label">退款原因：</span>
              <span class="info-value">{{ orderData.refundReason }}</span>
            </div>
          </el-col>
        </el-row>
      </el-card>

      <!-- 菜品详情 -->
      <el-card class="dishes-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="card-title">菜品详情</span>
          </div>
        </template>
        <div class="dishes-list">
          <div
            v-for="(item, index) in orderDetailList"
            :key="index"
            class="dish-item"
          >
            <div class="dish-image">
              <img
                :src="item.imageUrl || defaultDishImage"
                :alt="item.dishName"
                @error="handleImageError"
              />
            </div>
            <div class="dish-info">
              <div class="dish-name">{{ item.dishName }}</div>
              <div class="dish-price">¥{{ item.amount }}</div>
            </div>
            <div class="dish-quantity">
              <span class="quantity">×{{ item.number }}</span>
            </div>
            <div class="dish-total">
              <span class="total">¥{{ (item.amount * item.number).toFixed(2) }}</span>
            </div>
          </div>
        </div>
        <div class="order-total">
          <span class="total-label">订单总额：</span>
          <span class="total-amount">¥{{ calculateTotalAmount() }}</span>
        </div>
      </el-card>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button
          v-if="orderData.status !== '2005005'"
          type="warning"
          @click="handleCancelOrder"
        >
          取消订单
        </el-button>
        <el-button
          v-if="orderData.status === '2005001'"
          type="primary"
          @click="handleStartCooking"
        >
          接单
        </el-button>
        <el-button
          v-if="orderData.status === '2005003'"
          type="success"
          @click="handleDelivery"
        >
          开始配送
        </el-button>
        <el-button
          v-if="orderData.status === '2005004'"
          type="success"
          @click="handleFinish"
        >
          完成订单
        </el-button>
        <el-button
          v-if="orderData.payStatus === '2006001'"
          type="danger"
          @click="handleRefund"
        >
          申请退款
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 退款申请对话框 -->
  <el-dialog
    v-model="refundDialogVisible"
    title="申请退款"
    width="500px"
    class="refund-dialog"
  >
    <el-form :model="refundForm" label-width="100px">
      <el-form-item label="退款原因:">
        <el-input
          v-model="refundForm.refundReason"
          type="textarea"
          placeholder="请输入退款原因"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRefund">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderDetailListApi } from '@/api/modules/order/orderDetail'
import type { OrderDetailRow } from '@/api/types/order/orderDetail'
import type { TakeAwayOrderRow } from '@/api/types/order/takeAwayOrder'

// 定义组件属性
const props = defineProps<{
  modelValue: boolean
  orderData: TakeAwayOrderRow
  getStatusName: (status: string | undefined) => string
  getPayStatusName: (payStatus: string | undefined) => string
}>()

// 定义事件
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'updateStatus', order: TakeAwayOrderRow, statusData: { status?: string; }): void
  (e: 'updatePayStatus', order: TakeAwayOrderRow, statusData: {  payStatus?: string;refundReason?:string}): void
}>()

// 响应式数据
const visible = ref(false)
const orderDetailList = ref<OrderDetailRow[]>([])
const refundDialogVisible = ref(false)
const refundForm = reactive({
  id: 0 as any,
  refundReason: ''
})

// 默认菜品图片
const defaultDishImage = ref('https://via.placeholder.com/80x80?text=菜品图片')

// 监听modelValue变化
watch(
  () => props.modelValue,
  (val) => {
    visible.value = val
    if (val) {
      loadOrderDetail()
    }
  }
)

// 监听visible变化
watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 加载订单详情
const loadOrderDetail = async () => {
  try {
    const res = await getOrderDetailListApi({ orderId: props.orderData.orderId || ''})
    orderDetailList.value = Array.isArray(res.data) ? res.data : []
  } catch (error) {
    ElMessage.error('获取订单详情失败')
    console.error(error)
  }
}

// 处理图片加载错误
const handleImageError = (e: Event) => {
  const target = e.target as HTMLImageElement
  target.src = defaultDishImage.value
}

// 计算订单总额
const calculateTotalAmount = () => {
  return orderDetailList.value.reduce((total, item) => {
    return total + item.number * item.amount
  }, 0).toFixed(2)
}

// 获取订单状态类型
const getStatusType = (status: string | undefined) => {
  switch (status) {
    case '2005001': // 已下单
      return 'default'
    case '2005002': // 制作中
      return 'info'
    case '2005003': // 制作已完成/配送中
      return 'success'
    case '2005004': // 已取消
      return 'warning'
    case '2005005': // 已完成
      return 'success'
    default:
      return 'info'
  }
}

// 获取支付状态类型
const getPayStatusType = (payStatus: string | undefined) => {
  switch (payStatus) {
    case '2006001': // 已支付
      return 'success'
    case '2006002': // 未支付
      return 'warning'
    case '2006003': // 已退款
      return 'info'
    default:
      return 'info'
  }
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
}

// 取消订单
const handleCancelOrder = () => {
  emit('updateStatus', props.orderData, { status: '2005006' })
  handleClose()
}

// 开始制作
const handleStartCooking = () => {
  emit('updateStatus', props.orderData, { status: '2005002' })
  handleClose()
}


// 开始配送
const handleDelivery = () => {
  emit('updateStatus', props.orderData, { status: '2005004' })
  handleClose()
}

// 完成订单
const handleFinish = () => {
  emit('updateStatus', props.orderData, { status: '2005005' })
  handleClose()
}

// 申请退款
const handleRefund = () => {
  refundForm.id = props.orderData.orderId
  refundForm.refundReason = ''
  refundDialogVisible.value = true
}

// 标记为已支付
const handleMarkAsPaid = () => {
  emit('updatePayStatus', props.orderData, { payStatus: '2006001' })
  handleClose()
}

// 提交退款申请
const submitRefund = () => {
  if (!refundForm.refundReason) {
    ElMessage.warning('请输入退款原因')
    return
  }
  emit('updatePayStatus', props.orderData, {
    refundReason: refundForm.refundReason,
    payStatus: '2006003'
  })
  refundDialogVisible.value = false
  handleClose()
}
</script>

<style scoped lang="scss">
.order-detail-dialog {
  .order-detail-container {
    max-height: 70vh;
    overflow-y: auto;
    
    .el-card {
      margin-bottom: 20px;
      border-radius: 8px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
      
      :deep(.el-card__header) {
        padding: 15px 20px;
        background-color: #f5f7fa;
        border-bottom: 1px solid #ebeef5;
        border-radius: 8px 8px 0 0;
      }
    }
    
    .card-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      
      .card-title {
        font-size: 18px;
        font-weight: bold;
        color: #303133;
      }
    }
    
    .order-info-card {
      .info-item {
        display: flex;
        margin-bottom: 15px;
        align-items: center;
        
        .info-label {
          width: 100px;
          font-weight: bold;
          color: #606266;
        }
        
        .info-value {
          flex: 1;
          color: #303133;
        }
        
        .status-tag {
          margin-left: 10px;
        }
      }
    }
    
    .dishes-card {
      .dishes-list {
        .dish-item {
          display: flex;
          align-items: center;
          padding: 15px 0;
          border-bottom: 1px solid #ebeef5;
          
          &:last-child {
            border-bottom: none;
          }
          
          .dish-image {
            width: 80px;
            height: 80px;
            border-radius: 8px;
            overflow: hidden;
            margin-right: 15px;
            
            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }
          }
          
          .dish-info {
            flex: 1;
            
            .dish-name {
              font-size: 16px;
              font-weight: bold;
              color: #303133;
              margin-bottom: 5px;
            }
            
            .dish-price {
              font-size: 14px;
              color: #f56c6c;
              font-weight: bold;
            }
          }
          
          .dish-quantity {
            width: 60px;
            text-align: center;
            
            .quantity {
              font-size: 14px;
              color: #606266;
            }
          }
          
          .dish-total {
            width: 100px;
            text-align: right;
            
            .total {
              font-size: 16px;
              font-weight: bold;
              color: #303133;
            }
          }
        }
      }
      
      .order-total {
        display: flex;
        justify-content: flex-end;
        align-items: center;
        padding-top: 20px;
        margin-top: 20px;
        border-top: 1px solid #ebeef5;
        
        .total-label {
          font-size: 16px;
          color: #606266;
          margin-right: 10px;
        }
        
        .total-amount {
          font-size: 20px;
          font-weight: bold;
          color: #f56c6c;
        }
      }
    }
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
}

.refund-dialog {
  :deep(.el-textarea__inner) {
    min-height: 100px;
  }
}
</style>