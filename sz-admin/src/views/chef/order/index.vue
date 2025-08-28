<template>
  <div class="chef-order-container">
    <!-- 订单分类标签页 -->
    <el-tabs v-model="activeTab" class="order-tabs" @tab-change="handleTabChange">
      <el-tab-pane label="全部订单" name="all"></el-tab-pane>
      <el-tab-pane label="待制作" name="pending"></el-tab-pane>
      <el-tab-pane label="制作中" name="processing"></el-tab-pane>
<!--      <el-tab-pane label="已完成" name="completed"></el-tab-pane>-->
    </el-tabs>

    <!-- 订单列表 -->
    <div class="order-list-container">
      <el-row :gutter="20">
        <el-col
          v-for="(order, index) in filteredOrderList"
          :key="order.orderId  || 'order-' + index"
          :span="24"
          class="order-item-col"
        >
          <el-card class="order-card" shadow="hover">
            <div class="order-header">
              <div class="order-basic-info">
                <div class="order-number">
                  <el-tag :type="getOrderTypeTag(order.orderType)">
                    {{ order.orderType }}
                  </el-tag>
                  <span class="number-text">订单号: {{ order.orderNumber }}</span>
                </div>
                <div class="order-time">
                  <i class="el-icon-time"></i>
                  <span>{{ order.createTime }}</span>
                </div>
              </div>
              <!-- 备注信息 -->
              <div v-if="order.remark" class="order-remark">
                <label>备注:</label>
                <span class="remark-content">{{ order.remark }}</span>
              </div>
              <div class="order-status">
                <el-tag :type="getOrderStatusType(order.status)" v-if="isDineInOrder(order)">
                  {{ optionsStore.getDictNameById('dine_in_order_status', order.status || '') }}
                </el-tag>
                <el-tag :type="getOrderStatusType(order.status)" v-if="isTakeAwayOrder(order)">
                  {{ optionsStore.getDictNameById('take_away_order_status', order.status || '') }}
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
                  v-if="order.status == '2005007' || order.status =='2004001'"
                  type="primary"
                  size="large"
                  @click="startCooking(order)"
                  :disabled="!order.orderId && !order.id"
                >
                  开始制作
                </el-button>
                <el-button
                  v-else-if="order.status == '2005002'|| order.status =='2004002'"
                  type="success"
                  size="large"
                  @click="finishCooking(order)"
                  :disabled="!order.orderId && !order.id"
                >
                  完成制作
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

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import mittBus from '@/utils/mittBus';
import type { OrderItem, DineInOrderRow } from '@/api/types/order/dineInOrder';
import type { TakeAwayOrderRow } from '@/api/types/order/takeAwayOrder';
import { getChefOrderDetailApi, startCookingApi, finishCookingApi } from '@/api/modules/order/chefOrder';
import { getDineInOrderListApi } from '@/api/modules/order/dineInOrders'
import { getTakeAwayOrderListApi } from '@/api/modules/order/takeAwayOrder';
import {getOrderDetailListApi} from "@/api/modules/order/orderDetail";
import {useOptionsStore} from "@/stores/modules/options";

const optionsStore = useOptionsStore()


// 定义联合类型
type Order = DineInOrderRow | TakeAwayOrderRow;

// 响应式数据
const activeTab = ref('all')
const orderList = ref<Order[]>([])
// 计算属性：过滤掉状态为2005001的订单，并按创建时间倒序排列
const filteredOrderList = computed(() => {
  return orderList.value
    .filter(order => order.status !== '2005001')
    .sort((a, b) => {
      // 按创建时间倒序排列（最新的在前）
      const timeA = a.createTime ? new Date(a.createTime).getTime() : 0;
      const timeB = b.createTime ? new Date(b.createTime).getTime() : 0;
      return timeB - timeA;
    });
});
const currentOrder = ref<Order | null>(null)
const defaultDishImage = ref('https://via.placeholder.com/80x80?text=菜品图片')

// 分页数据
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
});


// 类型守卫函数
const isDineInOrder = (order: Order): order is DineInOrderRow => {
  return order.orderType === '堂食';
}

const isTakeAwayOrder = (order: Order): order is TakeAwayOrderRow => {
  return order.orderType === '外卖';
}

// 获取订单类型标签
const getOrderTypeTag = (type: string | undefined) => {
  switch (type) {
    case '堂食':
      return 'primary'
    case '外卖':
      return 'success'
    default:
      return 'info'
  }
}



// 获取订单状态类型
const getOrderStatusType = (status: string | undefined) => {
  switch (status) {
    case '2004002'|| '2005002':
      return 'default'
    case '2004003' || '2005003':
      return 'warning'
    case '2004005' || '2005005':
      return 'success'
    case '2004006' || '2005006':
      return 'danger'
    default:
      return 'info'
  }
}


// 计算订单总额
const calculateOrderTotal = (order: Order) => {
  return order.orderItems.reduce((total, item) => {
    return total + item.amount * item.number;
  }, 0).toFixed(2);
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
const loadOrderList = async () => {
  // 构造查询参数
  const params = {
    page: pagination.pageNum,
    limit: pagination.pageSize
  };
  
  // 根据当前选中的标签页确定订单类型和状态
  if (activeTab.value === 'all') {
    // 加载所有订单（堂食+外卖）
    try {
      const [dineInRes, takeAwayRes] = await Promise.all([
        getDineInOrderListApi(params),
        getTakeAwayOrderListApi(params)
      ]);
      // 合并订单列表
      orderList.value = [...dineInRes.data.rows, ...takeAwayRes.data.rows];
      pagination.total = dineInRes.data.total + takeAwayRes.data.total;
    } catch (err: any) {
      ElMessage.error('获取订单列表失败: ' + err.message);
    }
  } else {
    let dineInStatus:string='';
    let takeAwatStatus:string='';
    // 按状态分类加载订单
    if(activeTab.value=='pending'){
      dineInStatus = '2004001';
      takeAwatStatus='2005007';
    }else if(activeTab.value=='processing') {
      dineInStatus = '2004002';
      takeAwatStatus = '2005002';
    }
    try {
      const [dineInRes, takeAwayRes] = await Promise.all([
        getDineInOrderListApi({ ...params, status:dineInStatus }),
        getTakeAwayOrderListApi({ ...params, status: takeAwatStatus})
      ]);
      // 合并订单列表
      orderList.value = [...dineInRes.data.rows, ...takeAwayRes.data.rows];
      pagination.total = dineInRes.data.total + takeAwayRes.data.total;
    } catch (err: any) {
      ElMessage.error('获取订单列表失败: ' + err.message);
    }
  }
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
    // 调用API更新订单状态为制作中
    const orderId = order.orderId;
    if (!orderId) {
      ElMessage.error('订单ID不存在');
      return;
    }
    // 确定订单类型
    const orderType = isDineInOrder(order) ? 'dineIn' : 'takeaway';
    startCookingApi({ orderId: orderId, orderType }).then(() => {
      ElMessage.success('开始制作成功');
      if (currentOrder.value && (currentOrder.value.orderId === orderId || currentOrder.value.id === orderId)&&isDineInOrder(order)) {
        currentOrder.value.status = '2004002';
      }else if(currentOrder.value && (currentOrder.value.orderId === orderId || currentOrder.value.id === orderId)&&isTakeAwayOrder(order)){
        currentOrder.value.status = '2005002';
      }
    }).catch(err => {
      ElMessage.error('开始制作失败: ' + err.message);
    });
  }).catch(() => {
    // 用户取消操作
  });
}

// 完成制作
const finishCooking = (order: Order) => {
  ElMessageBox.confirm('确认完成该订单的制作吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 调用API更新订单状态为已完成
    const orderId = order.orderId;
    if (!orderId) {
      ElMessage.error('订单ID不存在');
      return;
    }
    // 确定订单类型
    const orderType = isDineInOrder(order) ? 'dineIn' : 'takeaway';
    finishCookingApi({ orderId: orderId, orderType }).then(() => {
      order.status = 'completed';
      ElMessage.success('制作完成');
      if (currentOrder.value && (currentOrder.value.orderId === orderId || currentOrder.value.id === orderId)&&isDineInOrder(order)) {
        currentOrder.value.status = '2004003'
      }else if(currentOrder.value && (currentOrder.value.orderId === orderId || currentOrder.value.id === orderId)&&isTakeAwayOrder(order)){
        currentOrder.value.status = '2005003'
      }
    }).catch(err => {
      ElMessage.error('完成制作失败: ' + err.message);
    });
  }).catch(() => {
    // 用户取消操作
  });
}



// 组件挂载时加载数据
onMounted(() => {
  loadOrderList()
  // 监听WebSocket消息，实时刷新订单列表
  mittBus.on('socket.TODO', handleWebSocketMessage);
})

// 组件卸载时移除监听
onUnmounted(() => {
  mittBus.off('socket.TODO', handleWebSocketMessage);
})

// 处理WebSocket消息
const handleWebSocketMessage = (data: any) => {
  // 收到催单消息时，重新加载订单列表
  loadOrderList()
}
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
              font-size: 20px;
            }
          }
          .order-remark {
            margin-top: 10px;
            padding: 8px 12px;
            background-color: #fffbe6;
            border: 1px solid #ffe58f;
            border-radius: 4px;
            font-size: 14px;

            label {
              font-weight: bold;
              color: rgb(0, 0, 0);
              margin-right: 8px;
            }

            .remark-content {
              color: #333;
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
                margin-left: 25px;
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