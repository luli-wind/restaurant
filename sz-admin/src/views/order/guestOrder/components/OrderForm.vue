<template>
  <div class="order-form-container">
    <div class="form-header">
      <h2>订单信息</h2>
    </div>

    <el-form
      ref="formRef"
      :model="orderForm"
      :rules="formRules"
      label-width="80px"
      class="order-form"
    >
      <el-form-item label="姓名" prop="customerName">
        <el-input
          v-model="orderForm.customerName"
          placeholder="请输入您的姓名"
        />
      </el-form-item>

      <el-form-item label="电话" prop="customerPhone">
        <el-input
          v-model="orderForm.customerPhone"
          placeholder="请输入您的电话"
        />
      </el-form-item>

      <el-form-item label="配送地址" prop="deliveryAddress">
        <el-input
          v-model="orderForm.deliveryAddress"
          type="textarea"
          :rows="3"
          placeholder="请输入配送地址"
        />
      </el-form-item>

      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="orderForm.remark"
          type="textarea"
          :rows="3"
          placeholder="请输入订单备注（可选）"
        />
      </el-form-item>

      <el-form-item label="总价">
        <div class="total-amount">¥{{ Number(cartTotal).toFixed(2) }}</div>
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          :loading="submitLoading"
          @click="submitOrder"
          size="large"
          style="width: 100%"
        >
          提交订单
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { OrderFormData } from '../types/index'

// 定义props
const props = defineProps<{
  cartTotal: number
}>()

// 定义emits
const emit = defineEmits<{
  (e: 'submit-order', orderData: OrderFormData): void
}>()

// 表单引用
const formRef = ref<FormInstance>()

// 提交按钮加载状态
const submitLoading = ref(false)

// 订单表单数据
const orderForm = reactive<OrderFormData>({
  customerName: '',
  customerPhone: '',
  deliveryAddress: '',
  remark: ''
})

// 表单验证规则
const formRules = reactive<FormRules>({
  customerName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  customerPhone: [
    { required: true, message: '请输入电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  deliveryAddress: [
    { required: true, message: '请输入配送地址', trigger: 'blur' }
  ]
})

// 提交订单
const submitOrder = async () => {
  if (!formRef.value) return
  
  try {
    // 验证表单
    await formRef.value.validate()
    
    // 设置提交按钮加载状态
    submitLoading.value = true
    
    // 发送订单数据到父组件
    emit('submit-order', { ...orderForm })
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    // 重置提交按钮加载状态
    submitLoading.value = false
  }
}
</script>

<style scoped lang="scss">
.order-form-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.form-header {
  margin-bottom: 20px;
  
  h2 {
    margin: 0;
    font-size: 20px;
    color: #333;
  }
}

.order-form {
  :deep(.el-form-item__label) {
    font-weight: bold;
  }
}

.total-amount {
  font-size: 24px;
  font-weight: bold;
  color: #ff6600;
}
</style>