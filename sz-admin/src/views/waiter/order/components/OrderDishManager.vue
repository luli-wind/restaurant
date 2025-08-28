<template>
  <div class="order-dish-manager">
    <!-- 加单按钮 -->
    <el-button 
      type="primary" 
      :icon="Plus" 
      @click="handleAddDish"
      class="add-dish-btn"
    >
      加单
    </el-button>

    <!-- 已点菜品清单（带修改功能） -->
    <div class="order-items-list">
      <div
        v-for="(item, index) in orderItems"
        :key="index"
        class="order-item"
      >
        <div class="item-info">
          <div class="item-name">{{ item.dishName }}</div>
          <div class="item-price">¥{{ item.amount }} × {{ item.number }}</div>
        </div>
        <div class="item-actions">
          <el-input-number
            v-model="item.number"
            :min="1"
            size="small"
            controls-position="right"
            @change="updateItemQuantity(index, $event)"
          />
          <!-- 修改菜品按钮（仅在订单状态为2004001时可用） -->
          <el-button
            type="primary"
            link
            :icon="Edit"
            :disabled="orderStatus !== '2004001'"
            @click="handleEditDish(item, index)"
            title="仅当订单状态为待处理时可修改"
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

    <!-- 修改菜品对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="修改菜品"
      width="500px"
      @close="resetEditForm"
    >
      <el-form
        :model="editForm"
        label-width="80px"
        ref="editFormRef"
      >
        <el-form-item label="菜品名称">
          <el-input v-model="editForm.dishName" disabled />
        </el-form-item>
        <el-form-item label="单价">
          <el-input v-model="editForm.amount" disabled />
        </el-form-item>
        <el-form-item label="数量" prop="number" :rules="[{ required: true, message: '请输入数量', trigger: 'blur' }]">
          <el-input-number
            v-model="editForm.number"
            :min="1"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="editForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmEditDish">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

// 定义组件props
interface OrderItem {
  dishId: number
  dishName: string
  imageUrl: string
  amount: number
  number: number
  remark?: string
}

interface Props {
  orderItems: OrderItem[]
  orderStatus?: string
}

interface Emits {
  (e: 'update-item', index: number, item: OrderItem): void
  (e: 'remove-item', index: number): void
  (e: 'add-dish'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 编辑表单相关
const editDialogVisible = ref(false)
const editFormRef = ref()
const currentEditIndex = ref(-1)

const editForm = reactive({
  dishId: 0,
  dishName: '',
  amount: 0,
  number: 1,
  remark: ''
})

// 更新菜品数量
const updateItemQuantity = (index: number, value: number | null) => {
  if (value === null) return
  if (value === 0) {
    removeFromOrder(index)
  } else {
    const updatedItem = { ...props.orderItems[index], number: value }
    emit('update-item', index, updatedItem)
  }
}

// 从订单中移除菜品
const removeFromOrder = (index: number) => {
  emit('remove-item', index)
}

// 处理加单操作
const handleAddDish = () => {
  emit('add-dish')
}

// 处理编辑菜品
const handleEditDish = (item: OrderItem, index: number) => {
  // 检查订单状态是否允许修改
  if (props.orderStatus !== '2004001') {
    ElMessage.warning('只有当订单状态为待处理时才能修改菜品')
    return
  }

  // 初始化编辑表单
  currentEditIndex.value = index
  editForm.dishId = item.dishId
  editForm.dishName = item.dishName
  editForm.amount = item.amount
  editForm.number = item.number
  editForm.remark = item.remark || ''
  
  editDialogVisible.value = true
}

// 确认修改菜品
const confirmEditDish = async () => {
  if (!editFormRef.value) return
  
  await editFormRef.value.validate((valid: boolean) => {
    if (valid) {
      const updatedItem: OrderItem = {
        dishId: editForm.dishId,
        dishName: editForm.dishName,
        imageUrl: '', // 这里可能需要从原始数据中获取
        amount: editForm.amount,
        number: editForm.number,
        remark: editForm.remark
      }
      
      emit('update-item', currentEditIndex.value, updatedItem)
      editDialogVisible.value = false
      ElMessage.success('菜品修改成功')
    }
  })
}

// 重置编辑表单
const resetEditForm = () => {
  currentEditIndex.value = -1
  editForm.dishId = 0
  editForm.dishName = ''
  editForm.amount = 0
  editForm.number = 1
  editForm.remark = ''
}
</script>

<style scoped lang="scss">
.order-dish-manager {
  .add-dish-btn {
    margin-bottom: 15px;
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
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
}
</style>