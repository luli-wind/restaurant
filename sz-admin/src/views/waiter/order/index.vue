<template>
  <div class="waiter-order-page">
    <el-row :gutter="20" class="main-content">
      <!-- 左侧区域：餐桌选择和菜品展示 -->
      <el-col :span="16" class="left-panel">
        <!-- 餐桌选择区 -->
        <el-card class="table-selection-card">
          <template #header>
            <div class="card-header">
              <span>选择餐桌</span>
              <el-input
                v-model="tableSearch"
                placeholder="搜索餐桌号"
                class="table-search-input"
                clearable
              >
                <template #prefix>
                  <el-icon class="table-search-icon"><Search /></el-icon>
                </template>
              </el-input>
            </div>
          </template>
          <div class="table-grid">
            <div
              v-for="table in filteredTables"
              :key="table.tableId"
              :class="[
                'table-item',
                {
                  'table-item--disabled': table.status != '2001001',
                  'table-item--selected': selectedTable?.tableId === table.tableId,
                  'table-item--available': table.status === '2001001'
                }
              ]"
              @click="table.status === '2001001' && selectTable(table)"
            >
              <div class="table-item__name">
                <span>{{ table.tableName }}</span>
                <el-icon v-if="selectedTable?.tableId === table.tableId" class="table-item__selected-icon">
                  <Check />
                </el-icon>
              </div>
              <div class="table-item__capacity">{{ table.capacity }}人桌</div>
              <div class="table-item__status" :class="'table-item__status--' + table.status">
                {{ optionsStore.getDictNameById('table_status', table.status || '') }}
              </div>
            </div>
          </div>
        </el-card>

        <!-- 菜品展示区 -->
        <el-card class="dish-display-card">
          <template #header>
            <div class="card-header">
              <span>菜品列表</span>
              <el-input
                v-model="dishSearch"
                placeholder="搜索菜品"
                style="width: 200px; margin-left: 20px;"
                clearable
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
            </div>
          </template>
          <!-- 菜品分类标签页 -->
          <el-tabs v-model="activeCategory" type="card" @tab-change="handleCategoryChange">
            <el-tab-pane
              v-for="category in dishCategories"
              :key="category.id"
              :label="category.codeName"
              :name="category.id"
            >
              <!-- 菜品网格 -->
              <div class="dish-grid">
                <el-card
                  v-for="dish in filteredDishes"
                  :key="dish.dishId"
                  class="dish-card"
                  @click="addToOrder(dish)"
                >
                  <div class="dish-content">
                    <div class="dish-image">
                      <img :src="dish.imageUrl || defaultDishImage" :alt="dish.dishName" />
                    </div>
                    <div class="dish-info">
                      <div class="dish-name">{{ dish.dishName }}</div>
                      <div class="dish-price">¥{{ dish.price }}</div>
                      <div class="dish-description">{{ dish.description }}</div>
                    </div>
                  </div>
                </el-card>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>

      <!-- 右侧区域：订单操作 -->
      <el-col :span="8" class="right-panel">
        <!-- 订单信息区 -->
        <el-card class="order-info-card">
          <template #header>
            <div class="card-header">
              <span>订单信息</span>
            </div>
          </template>
          <el-form :model="orderForm" label-width="80px">
            <el-form-item label="餐桌号">
              <el-input v-model="orderForm.tableName" disabled />
            </el-form-item>
            <el-form-item label="用餐人数">
              <el-input-number
                v-model="orderForm.numberOfGuests"
                :min="1"
                :max="selectedTable?.capacity || 10"
                controls-position="right"
              />
            </el-form-item>
            <el-form-item label="订单备注">
              <el-input
                v-model="orderForm.remark"
                type="textarea"
                :rows="2"
                placeholder="请输入订单备注"
              />
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 已点菜品清单 -->
        <el-card class="order-items-card">
          <template #header>
            <div class="card-header">
              <span>已点菜品</span>
            </div>
          </template>
          <div class="order-items-list">
            <div
              v-for="(item, index) in orderItems"
              :key="index"
              class="order-item"
            >
              <div class="item-info">
                <div class="item-name">{{ item.dishName }}</div>
                <div class="item-price">¥{{ item.price }} × {{ item.quantity }}</div>
              </div>
              <div class="item-actions">
                <el-input-number
                  v-model="item.quantity"
                  :min="1"
                  size="small"
                  controls-position="right"
                  @change="updateItemQuantity(index, $event)"
                />
                <el-button
                  type="danger"
                  link
                  :icon="Delete"
                  @click="removeFromOrder(index)"
                />
              </div>
            </div>
            <div v-if="orderItems.length === 0" class="empty-order">
              暂无菜品
            </div>
          </div>
        </el-card>

        <!-- 订单操作区 -->
        <el-card class="order-action-card">
          <div class="order-total">
            <span>总计：</span>
            <span class="total-amount">¥{{ totalAmount }}</span>
          </div>
          <div class="order-buttons">
            <el-button
              type="danger"
              plain
              @click="clearOrder"
            >
              清空订单
            </el-button>
            <el-button
              type="primary"
              :disabled="orderItems.length === 0 || !selectedTable"
              @click="submitOrder"
            >
              提交订单
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { Search, Delete, Check } from '@element-plus/icons-vue'
  import {getDictByCode,} from "@/api/modules/system/dict";
  import type {
    DiningTableRow,
    DiningTableQuery
  } from '@/api/types/restaurant/diningTable'
  import type {
    DishRow,
    DishQuery
  } from '@/api/types/restaurant/dish'
  import type {
    DineInOrderForm
  } from '@/api/types/order/dineInOrder'
  import {
    getAllDiningTableListApi
  } from '@/api/modules/restaurant/diningTable'
  import {
    getAllDishListApi
  } from '@/api/modules/restaurant/dish'
  import {
    createDineInOrderApi
  } from '@/api/modules/order/dineInOrders'
  import { useDictOptions } from '@/hooks/useDictOptions'
  import { useOptionsStore } from '@/stores/modules/options'
  import {useDict} from "@/hooks/useDict";

const optionsStore = useOptionsStore()


useDict(['dish_category']);
useDict(['table_status'])
// 餐桌相关数据
const tables = ref<DiningTableRow[]>([])
const tableSearch = ref('')
const selectedTable = ref<DiningTableRow | null>(null)

// 菜品相关数据
const dishes = ref<DishRow[]>([])
const dishSearch = ref('')
const activeCategory = ref('')
const dishCategories = useDictOptions('dish_category')
// 订单相关数据
const orderForm = ref({
  tableName: '',
  numberOfGuests: 1,
  remark: ''
})

const orderItems = ref<Array<{
  dishId: number
  dishName: string
  price: number
  quantity: number
}>>([])

// 默认菜品图片
const defaultDishImage = ref('')

// 计算属性
const filteredTables = computed(() => {
  if (!tableSearch.value) {
    return tables.value
  }
  return tables.value.filter(table =>
    table.tableName?.includes(tableSearch.value)
  )
})

const filteredDishes = computed(() => {
  if (!dishSearch.value) {
    return dishes.value.filter(dish =>
      activeCategory.value ? dish.category === activeCategory.value : true
    )
  }
  return dishes.value.filter(dish =>
    dish.dishName?.includes(dishSearch.value) &&
    (activeCategory.value ? dish.category === activeCategory.value : true)
  )
})

const totalAmount = computed(() => {
  return orderItems.value.reduce((total, item) => {
    return total + (item.price * item.quantity)
  }, 0)
})

// 方法
const selectTable = (table: DiningTableRow) => {
  selectedTable.value = table
  orderForm.value.tableName = table.tableName || ''
}

const addToOrder = (dish: DishRow) => {
  if (!selectedTable.value) {
    ElMessage.warning('请先选择餐桌')
    return
  }

  // 检查菜品是否已存在于订单中
  const existingItem = orderItems.value.find(item => item.dishId === dish.dishId)
  if (existingItem) {
    existingItem.quantity++
  } else {
    orderItems.value.push({
      dishId: dish.dishId,
      dishName: dish.dishName || '',
      price: dish.price || 0,
      quantity: 1
    })
  }
}

const updateItemQuantity = (index: number, value: number | null) => {
  if (value === null) return
  if (value === 0) {
    removeFromOrder(index)
  } else {
    orderItems.value[index].quantity = value
  }
}

const removeFromOrder = (index: number) => {
  orderItems.value.splice(index, 1)
}

const clearOrder = () => {
  ElMessageBox.confirm('确定要清空订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    orderItems.value = []
    ElMessage.success('订单已清空')
  }).catch(() => {
    // 取消操作
  })
}

const submitOrder = async () => {
  if (!selectedTable.value) {
    ElMessage.error('请选择餐桌')
    return
  }

  if (orderItems.value.length === 0) {
    ElMessage.error('请至少选择一个菜品')
    return
  }

  try {
    const orderData: DineInOrderForm = {
      tableId: selectedTable.value.tableId,
      numberOfGuests: orderForm.value.numberOfGuests,
      remark: orderForm.value.remark,
      orderItems: orderItems.value.map(item => ({
        dishId: item.dishId,
        quantity: item.quantity,
        price: item.price
      }))
    }

    await createDineInOrderApi(orderData)
    ElMessage.success('订单提交成功')
    // 清空订单
    orderItems.value = []
  } catch (error) {
    ElMessage.error('订单提交失败')
    console.error(error)
  }
}

const handleCategoryChange = (name: string) => {
  activeCategory.value = name
}

// 初始化数据
const initTables = async () => {
  try {
    const res = await getAllDiningTableListApi()
    tables.value = res.data || []
  } catch (error) {
    ElMessage.error('获取餐桌列表失败')
    console.error(error)
  }
}

const initDishes = async () => {
  try {
    const res = await getAllDishListApi()
    console.log(res)
    dishes.value = res.data || []
    
    // 设置默认分类为第一个分类
    if (dishCategories.value.length > 0 && !activeCategory.value) {
      activeCategory.value = dishCategories.value[0].id
    }
  } catch (error) {
    ElMessage.error('获取菜品列表失败')
    console.error(error)
  }
}

// 组件挂载时初始化数据
onMounted(() => {
  initTables()
  initDishes()
})
</script>

<style scoped lang="scss">
  .waiter-order-page {
    padding: 20px;
    background: linear-gradient(135deg, #f5f7fa 0%, #e4e7f4 100%);
    min-height: calc(100vh - 120px);
  }
  
  .main-content {
    height: 100%;
  }
  
  .left-panel, .right-panel {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .el-card {
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    transition: box-shadow 0.3s ease;
    
    &:hover {
      box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
    }
  }
  
  .el-card__header {
    padding: 0;
  }


.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 20px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
  border-radius: 4px 4px 0 0;
}

.card-header span {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.table-search-input {
  width: 220px;
  margin-left: 20px;
  
  :deep(.el-input__wrapper) {
    border-radius: 20px;
  }
}

.table-search-icon {
  color: #909399;
}

.table-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 15px;
  padding: 15px 0;
}

.table-item {
  position: relative;
  padding: 15px 10px;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background-color: #f5f5f5;
  border: 2px solid #e0e0e0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  
  &:hover:not(.table-item--disabled) {
    transform: translateY(-3px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    border-color: #409eff;
  }
  
  &.table-item--selected {
    background-color: #ecf5ff;
    border-color: #409eff;
    box-shadow: 0 4px 8px rgba(64, 158, 255, 0.2);
    animation: pulse 2s infinite;
  }
  
  &.table-item--disabled {
    cursor: not-allowed;
    opacity: 0.6;
    background-color: #f5f5f5;
    
    &::after {
      content: "";
      position: absolute;
      top: 50%;
      left: 0;
      right: 0;
      height: 2px;
      background-color: #909399;
      transform: rotate(-45deg);
    }
  }
  
  &.table-item--available {
    background-color: #f0f9eb;
    border-color: #e1f3d8;
    
    &:hover {
      border-color: #67c23a;
      background-color: #f0f9eb;
    }
  }
}

.table-item__name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #303133;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-item__selected-icon {
  color: #409eff;
  font-size: 18px;
  font-weight: bold;
}

.table-item__capacity {
  font-size: 12px;
  color: #606266;
  margin-bottom: 5px;
}

.table-item__status {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  display: inline-block;
  
  &.table-item__status--2001001 {
    background-color: #f0f9eb;
    color: #67c23a;
  }
  
  &.table-item__status--2001002 {
    background-color: #fdf6ec;
    color: #e6a23c;
  }
  
  &.table-item__status--2001003 {
    background-color: #fef0f0;
    color: #f56c6c;
  }
}

.dish-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  padding: 10px 0;
}

.dish-card {
  cursor: pointer;
  transition: transform 0.2s;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.dish-content {
  display: flex;
  flex-direction: column;
}

.dish-image {
  height: 120px;
  overflow: hidden;
  border-radius: 4px;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.dish-info {
  margin-top: 10px;
}

.dish-name {
  font-weight: bold;
  margin-bottom: 5px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dish-price {
  color: #f56c6c;
  font-weight: bold;
  margin-bottom: 5px;
}

.dish-description {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.order-items-list {
  max-height: 300px;
  overflow-y: auto;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
  
  &:last-child {
    border-bottom: none;
  }
}

.item-info {
  flex: 1;
}

.item-name {
  font-weight: bold;
  margin-bottom: 3px;
}

.item-price {
  font-size: 14px;
  color: #666;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.empty-order {
  text-align: center;
  padding: 20px;
  color: #999;
}

.order-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
}

.total-amount {
  color: #f56c6c;
  font-size: 20px;
}

.order-buttons {
  display: flex;
  justify-content: space-between;
  gap: 10px;
}

@keyframes pulse {
  0% {
    box-shadow: 0 4px 8px rgba(64, 158, 255, 0.2);
  }
  50% {
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  }
  100% {
    box-shadow: 0 4px 8px rgba(64, 158, 255, 0.2);
  }
}
</style>