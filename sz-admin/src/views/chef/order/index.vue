<template>
  <div class="chef-order-container">
    <!-- 订单分类标签页 -->
    <el-tabs v-model="activeTab" class="order-tabs" @tab-change="handleTabChange">
      <el-tab-pane label="全部订单" name="all"></el-tab-pane>
      <el-tab-pane label="待制作" name="pending"></el-tab-pane>
      <el-tab-pane label="制作中" name="processing"></el-tab-pane>
      <el-tab-pane label="已完成" name="completed"></el-tab-pane>
    </el-tabs>

    <!-- 订单列表 -->
    <div class="order-list-container">
      <el-row :gutter="20">
        <el-col 
          v-for="order in orderList" 
          :key="order.orderId" 
          :span="24" 
          class="order-item-col"
        >
          <el-card class="order-card" shadow="hover">
            <div class="order-header">
              <div class="order-basic-info">
                <div class="order-number">
                  <el-tag :type="getOrderTypeTag(order.orderType)">
                    {{ getOrderTypeName(order.orderType) }}
                  </el-tag>
                  <span class="number-text">订单号: {{ order.orderNumber }}</span>
                </div>
                <div class="order-time">
                  <i class="el-icon-time"></i>
                  <span>{{ order.createTime }}</span>
                </div>
              </div>
              <div class="order-status">
                <el-tag :type="getOrderStatusType(order.status)">
                  {{ getOrderStatusName(order.status) }}
                </el-tag>
              </div>
            </div>

            <div class="order-content">
              <!-- 堂食订单信息 -->
              <div v-if="isDineInOrder(order)" class="dine-in-info">
                <div class="info-item">
                  <label>桌号:</label>
                  <span>{{ order.tableName }}</span>
                </div>
                <div class="info-item">
                  <label>人数:</label>
                  <span>{{ order.numberOfGuests }}人</span>
                </div>
              </div>

              <!-- 外卖订单信息 -->
              <div v-else class="takeaway-info">
                <div class="info-item">
                  <label>客户:</label>
                  <span>{{ isTakeAwayOrder(order) ? order.customerName : '' }}</span>
                </div>
                <div class="info-item">
                  <label>电话:</label>
                  <span>{{ isTakeAwayOrder(order) ? order.customerPhone : '' }}</span>
                </div>
                <div class="info-item">
                  <label>地址:</label>
                  <span>{{ isTakeAwayOrder(order) ? order.deliveryAddress : '' }}</span>
                </div>
              </div>

              <!-- 菜品列表 -->
              <div class="dishes-list">
                <div 
                  v-for="(dish, index) in order.orderItems" 
                  :key="index" 
                  class="dish-item"
                >
                  <div class="dish-image">
                    <img 
                      :src="dish.imageUrl || defaultDishImage" 
                      :alt="dish.dishName"
                      @error="handleImageError"
                    />
                  </div>
                  <div class="dish-info">
                    <div class="dish-name">{{ dish.dishName }}</div>
                    <div class="dish-quantity">×{{ dish.number }}</div>
                  </div>
                  <div class="dish-price">¥{{ (dish.amount * dish.number).toFixed(2) }}</div>
                </div>
              </div>

              <!-- 订单总额 -->
              <div class="order-total">
                <span>总计:</span>
                <span class="total-amount">¥{{ calculateOrderTotal(order) }}</span>
              </div>
            </div>

            <div class="order-footer">
              <div class="order-actions">
                <el-button 
                  v-if="order.status === 'pending'" 
                  type="primary" 
                  @click="startCooking(order)"
                >
                  开始制作
                </el-button>
                <el-button 
                  v-else-if="order.status === 'processing'" 
                  type="success" 
                  @click="finishCooking(order)"
                >
                  完成制作
                </el-button>
                <el-button 
                  v-else-if="order.status === 'completed'" 
                  type="info" 
                  disabled
                >
                  已完成
                </el-button>
                <el-button @click="viewOrderDetail(order)">
                  查看详情
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="orderDetailVisible"
      title="订单详情"
      width="800px"
      class="order-detail-dialog"
    >
      <div v-if="currentOrder" class="order-detail-content">
        <!-- 订单基本信息 -->
        <el-card class="detail-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>订单信息</span>
            </div>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">订单号：</span>
                <span class="info-value">{{ currentOrder.orderNumber }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">下单时间：</span>
                <span class="info-value">{{ currentOrder.createTime }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">订单类型：</span>
                <span class="info-value">{{ getOrderTypeName(currentOrder.orderType) }}</span>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="info-item">
                <span class="info-label">订单状态：</span>
                <el-tag :type="getOrderStatusType(currentOrder.status)" class="status-tag">
                  {{ getOrderStatusName(currentOrder.status) }}
                </el-tag>
              </div>
            </el-col>
            
            <!-- 堂食订单信息 -->
            <template v-if="currentOrder && isDineInOrder(currentOrder)">
              <el-col :span="12">
                <div class="info-item">
                  <span class="info-label">桌号：</span>
                  <span class="info-value">{{ currentOrder.tableName }}</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="info-item">
                  <span class="info-label">用餐人数：</span>
                  <span class="info-value">{{ currentOrder.numberOfGuests }}人</span>
                </div>
              </el-col>
            </template>
            
            <!-- 外卖订单信息 -->
            <template v-else-if="currentOrder && isTakeAwayOrder(currentOrder)">
              <el-col :span="12">
                <div class="info-item">
                  <span class="info-label">客户姓名：</span>
                  <span class="info-value">{{ currentOrder.customerName }}</span>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="info-item">
                  <span class="info-label">联系电话：</span>
                  <span class="info-value">{{ currentOrder.customerPhone }}</span>
                </div>
              </el-col>
              <el-col :span="24">
                <div class="info-item">
                  <span class="info-label">配送地址：</span>
                  <span class="info-value">{{ currentOrder.deliveryAddress }}</span>
                </div>
              </el-col>
            </template>
            
            <el-col v-if="currentOrder.remark" :span="24">
              <div class="info-item">
                <span class="info-label">订单备注：</span>
                <span class="info-value">{{ currentOrder.remark }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 菜品详情 -->
        <el-card class="detail-card dishes-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>菜品详情</span>
            </div>
          </template>
          <div class="dishes-list">
            <div
              v-for="(item, index) in currentOrder.orderItems"
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
            <span class="total-amount">¥{{ calculateOrderTotal(currentOrder) }}</span>
          </div>
        </el-card>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="orderDetailVisible = false">关闭</el-button>
          <el-button 
            v-if="currentOrder && currentOrder.status === 'pending'" 
            type="primary" 
            @click="startCooking(currentOrder)"
          >
            开始制作
          </el-button>
          <el-button 
            v-else-if="currentOrder && currentOrder.status === 'processing'" 
            type="success" 
            @click="finishCooking(currentOrder)"
          >
            完成制作
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { OrderItem, DineInOrderRow } from "@/api/types/order/dineInOrder";
import type { TakeAwayOrderRow } from "@/api/types/order/takeAwayOrder";

// 定义联合类型
type Order = DineInOrderRow | TakeAwayOrderRow;

// 响应式数据
const activeTab = ref('all')
const orderList = ref<Order[]>([])
const orderDetailVisible = ref(false)
const currentOrder = ref<Order | null>(null)
const defaultDishImage = ref('https://via.placeholder.com/80x80?text=菜品图片')

// 分页数据
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 模拟订单数据
const mockOrderList: Order[] = [
  {
    orderId: 1,
    orderNumber: 20250825001,
    orderType: 'dineIn',
    status: 'pending',
    createTime: '2025-08-25 12:30:00',
    tableName: 'A01',
    numberOfGuests: 4,
    remark: '少放辣椒',
    orderItems: [
      {
        dishId: 101,
        dishName: '宫保鸡丁',
        imageUrl: '',
        number: 2,
        amount: 38.00
      },
      {
        dishId: 102,
        dishName: '麻婆豆腐',
        imageUrl: '',
        number: 1,
        amount: 22.00
      }
    ]
  } as DineInOrderRow,
  {
    orderId: 2,
    orderNumber: 20250825002,
    orderType: 'takeaway',
    status: 'processing',
    createTime: '2025-08-25 12:45:00',
    customerName: '张三',
    customerPhone: '13800138000',
    deliveryAddress: '北京市朝阳区某某街道123号',
    orderItems: [
      {
        dishId: 103,
        dishName: '红烧肉',
        imageUrl: '',
        number: 1,
        amount: 48.00
      },
      {
        dishId: 104,
        dishName: '清炒时蔬',
        imageUrl: '',
        number: 1,
        amount: 18.00
      }
    ]
  } as TakeAwayOrderRow,
  {
    orderId: 3,
    orderNumber: 20250825003,
    orderType: 'dineIn',
    status: 'completed',
    createTime: '2025-08-25 11:20:00',
    tableName: 'B05',
    numberOfGuests: 2,
    orderItems: [
      {
        dishId: 105,
        dishName: '糖醋里脊',
        imageUrl: '',
        number: 1,
        amount: 42.00
      }
    ]
  } as DineInOrderRow
]

// 类型守卫函数
const isDineInOrder = (order: Order): order is DineInOrderRow => {
  return order.orderType === 'dineIn';
}

const isTakeAwayOrder = (order: Order): order is TakeAwayOrderRow => {
  return order.orderType === 'takeaway';
}

// 获取订单类型标签
const getOrderTypeTag = (type: string) => {
  switch (type) {
    case 'dineIn':
      return 'primary'
    case 'takeaway':
      return 'success'
    default:
      return 'info'
  }
}

// 获取订单类型名称
const getOrderTypeName = (type: string) => {
  switch (type) {
    case 'dineIn':
      return '堂食'
    case 'takeaway':
      return '外卖'
    default:
      return '未知'
  }
}

// 获取订单状态类型
const getOrderStatusType = (status: string) => {
  switch (status) {
    case 'pending':
      return ''
    case 'processing':
      return 'warning'
    case 'completed':
      return 'success'
    default:
      return 'info'
  }
}

// 获取订单状态名称
const getOrderStatusName = (status: string) => {
  switch (status) {
    case 'pending':
      return '待制作'
    case 'processing':
      return '制作中'
    case 'completed':
      return '已完成'
    default:
      return '未知'
  }
}

// 计算订单总额
const calculateOrderTotal = (order: Order) => {
  return order.orderItems.reduce((total, item) => {
    return total + (item.amount * item.number)
  }, 0).toFixed(2)
}

// 处理标签页切换
const handleTabChange = (tabName: string) => {
  console.log('切换到标签页:', tabName)
  loadOrderList()
}

// 处理分页大小变化
const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  loadOrderList()
}

// 处理当前页变化
const handleCurrentChange = (val: number) => {
  pagination.pageNum = val
  loadOrderList()
}

// 加载订单列表
const loadOrderList = () => {
  // 模拟API调用
  setTimeout(() => {
    orderList.value = mockOrderList
    pagination.total = mockOrderList.length
  }, 300)
}

// 处理图片加载错误
const handleImageError = (e: Event) => {
  const target = e.target as HTMLImageElement
  target.src = defaultDishImage.value
}

// 开始制作
const startCooking = (order: Order) => {
  ElMessageBox.confirm('确认开始制作该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟API调用更新订单状态
    order.status = 'processing'
    ElMessage.success('开始制作成功')
    if (currentOrder.value && currentOrder.value.orderId === order.orderId) {
      currentOrder.value.status = 'processing'
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 完成制作
const finishCooking = (order: Order) => {
  ElMessageBox.confirm('确认完成该订单的制作吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟API调用更新订单状态
    order.status = 'completed'
    ElMessage.success('制作完成')
    if (currentOrder.value && currentOrder.value.orderId === order.orderId) {
      currentOrder.value.status = 'completed'
    }
  }).catch(() => {
    // 用户取消操作
  })
}

// 查看订单详情
const viewOrderDetail = (order: Order) => {
  currentOrder.value = { ...order }
  orderDetailVisible.value = true
}

// 组件挂载时加载数据
onMounted(() => {
  loadOrderList()
})
</script>

<style scoped lang="scss">
.chef-order-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 120px);

  .page-header {
    margin-bottom: 20px;
    
    h2 {
      font-size: 24px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 10px;
    }
    
    p {
      font-size: 14px;
      color: #606266;
    }
  }

  .order-tabs {
    margin-bottom: 20px;
    
    :deep(.el-tabs__header) {
      margin-bottom: 0;
      
      .el-tabs__nav-wrap::after {
        height: 1px;
      }
    }
  }

  .order-list-container {
    .order-item-col {
      margin-bottom: 20px;
      
      .order-card {
        border-radius: 8px;
        border: 1px solid #ebeef5;
        transition: all 0.3s ease;
        
        &:hover {
          box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
          transform: translateY(-2px);
        }
        
        .order-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 15px;
          border-bottom: 1px solid #ebeef5;
          background-color: #f8f9fa;
          border-radius: 8px 8px 0 0;
          
          .order-basic-info {
            display: flex;
            flex-direction: column;
            
            .order-number {
              display: flex;
              align-items: center;
              margin-bottom: 5px;
              
              .number-text {
                margin-left: 10px;
                font-weight: bold;
                color: #303133;
              }
            }
            
            .order-time {
              display: flex;
              align-items: center;
              font-size: 12px;
              color: #909399;
              
              i {
                margin-right: 5px;
              }
            }
          }
          
          .order-status {
            .el-tag {
              font-weight: bold;
            }
          }
        }
        
        .order-content {
          padding: 15px;
          
          .dine-in-info,
          .takeaway-info {
            display: flex;
            margin-bottom: 15px;
            padding-bottom: 15px;
            border-bottom: 1px dashed #ebeef5;
            
            .info-item {
              margin-right: 20px;
              display: flex;
              
              label {
                font-weight: bold;
                color: #606266;
                margin-right: 5px;
              }
              
              span {
                color: #303133;
              }
            }
          }
          
          .dishes-list {
            .dish-item {
              display: flex;
              align-items: center;
              padding: 10px 0;
              border-bottom: 1px solid #f5f7fa;
              
              &:last-child {
                border-bottom: none;
              }
              
              .dish-image {
                width: 60px;
                height: 60px;
                border-radius: 6px;
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
                display: flex;
                justify-content: space-between;
                align-items: center;
                
                .dish-name {
                  font-weight: bold;
                  color: #303133;
                }
                
                .dish-quantity {
                  color: #606266;
                }
              }
              
              .dish-price {
                font-weight: bold;
                color: #f56c6c;
              }
            }
          }
          
          .order-total {
            display: flex;
            justify-content: flex-end;
            align-items: center;
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px solid #ebeef5;
            
            span:first-child {
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
        
        .order-footer {
          padding: 15px;
          border-top: 1px solid #ebeef5;
          background-color: #f8f9fa;
          border-radius: 0 0 8px 8px;
          
          .order-actions {
            display: flex;
            justify-content: flex-end;
            
            .el-button {
              margin-left: 10px;
            }
          }
        }
      }
    }
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
    padding: 20px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  }
}

.order-detail-dialog {
  .order-detail-content {
    max-height: 70vh;
    overflow-y: auto;
    
    .detail-card {
      margin-bottom: 20px;
      border-radius: 8px;
      box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
      
      :deep(.el-card__header) {
        padding: 15px 20px;
        background-color: #f5f7fa;
        border-bottom: 1px solid #ebeef5;
        border-radius: 8px 8px 0 0;
      }
      
      .card-header {
        font-size: 18px;
        font-weight: bold;
        color: #303133;
      }
      
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

// 响应式设计
@media (max-width: 768px) {
  .chef-order-container {
    padding: 10px;
    
    .order-list-container {
      .order-item-col {
        .order-card {
          .order-header {
            flex-direction: column;
            align-items: flex-start;
            
            .order-basic-info {
              margin-bottom: 10px;
            }
          }
          
          .order-content {
            .dine-in-info,
            .takeaway-info {
              flex-direction: column;
              
              .info-item {
                margin-bottom: 5px;
                margin-right: 0;
              }
            }
            
            .dishes-list {
              .dish-item {
                .dish-info {
                  flex-direction: column;
                  align-items: flex-start;
                  
                  .dish-quantity {
                    margin-top: 5px;
                  }
                }
              }
            }
          }
          
          .order-footer {
            .order-actions {
              flex-direction: column;
              
              .el-button {
                margin-left: 0;
                margin-bottom: 10px;
                
                &:last-child {
                  margin-bottom: 0;
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>