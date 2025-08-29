<template>
  <div class="guest-order-container">
    <div class="page-header">
      <h1>访客下单</h1>
      <p>欢迎使用我们的在线订餐服务，请选择您喜欢的菜品</p>
      <div class="header-actions">
        <button class="exit-button" @click="exitToLogin">退出</button>
      </div>
    </div>

    <div class="order-content">
      <!-- 菜品列表 -->
      <DishList @add-to-cart="addToCart" />

      <!-- 右侧购物车和订单信息 -->
      <div class="order-sidebar">
        <!-- 购物车 -->
        <ShoppingCart 
          :cart-items="cartItems" 
          @update-quantity="updateCartItemQuantity"
          @remove-item="removeCartItem"
        />

        <!-- 订单表单 -->
        <OrderForm 
          :cart-total="cartTotal"
          @submit-order="submitOrder"
        />
      </div>
    </div>

    <!-- 订单汇总 -->
    <OrderSummary 
      v-if="orderSubmitted"
      :order-data="submittedOrder"
      @close-summary="closeOrderSummary"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import DishList from '@/views/order/guestOrder/components/DishList.vue'
import ShoppingCart from '@/views/order/guestOrder/components/ShoppingCart.vue'
import OrderForm from '@/views/order/guestOrder/components/OrderForm.vue'
import OrderSummary from '@/views/order/guestOrder/components/OrderSummary.vue'
import type { CartItem, OrderFormData } from '@/views/order/guestOrder/types/index'
import { createGuestTakeAwayOrderApi } from '@/api/modules/order/guestOrder'
import type { TakeAwayOrderForm } from '@/api/types/order/takeAwayOrder'

// 路由实例
const router = useRouter()

// 购物车项目
const cartItems = ref<CartItem[]>([])

// 订单提交状态
const orderSubmitted = ref(false)
const submittedOrder = ref<any>(null)

// 计算购物车总价
const cartTotal = computed(() => {
  return cartItems.value.reduce((total, item) => {
    return total + (Number(item.price) * item.quantity);
  }, 0);
})

// 添加到购物车
const addToCart = (dish: any) => {
  const existingItem = cartItems.value.find(item => item.dishId === dish.dishId)
  if (existingItem) {
    existingItem.quantity += 1
  } else {
    cartItems.value.push({
      dishId: dish.dishId,
      dishName: dish.dishName,
      price: dish.price,
      quantity: 1,
      imageUrl: dish.imageUrl
    })
  }
}

// 更新购物车项目数量
const updateCartItemQuantity = (dishId: string, quantity: number) => {
  const item = cartItems.value.find(item => item.dishId === dishId)
  if (item) {
    if (quantity <= 0) {
      // 如果数量小于等于0，移除项目
      cartItems.value = cartItems.value.filter(item => item.dishId !== dishId)
    } else {
      item.quantity = quantity
    }
  }
}

// 移除购物车项目
const removeCartItem = (dishId: string) => {
  cartItems.value = cartItems.value.filter(item => item.dishId !== dishId)
}

// 提交订单
const submitOrder = async (orderData: OrderFormData) => {
  try {
    // 构造订单数据
    const orderForm: TakeAwayOrderForm = {
      customerName: orderData.customerName,
      customerPhone: orderData.customerPhone,
      deliveryAddress: orderData.deliveryAddress,
      remark: orderData.remark,
      totalAmount: cartTotal.value,
      orderItems: cartItems.value.map((item: CartItem) => ({
        dishId: parseInt(item.dishId),
        dishName: item.dishName,
        imageUrl: item.imageUrl,
        number: item.quantity,
        amount: Number(item.price)
      }))
    };

    // 调用后端API创建订单
    const response = await createGuestTakeAwayOrderApi(orderForm);
    
    // API调用成功
    submittedOrder.value = {
      ...orderData,
      items: cartItems.value,
      totalAmount: cartTotal.value,
      orderNumber: response.data.orderNumber,
      createTime: response.data.createTime
    };
    orderSubmitted.value = true;
    
    // 清空购物车
    cartItems.value = [];
  } catch (error) {
    console.error('提交订单失败:', error);
    // 这里应该有错误处理
  }
}

// 关闭订单汇总
const closeOrderSummary = () => {
  orderSubmitted.value = false
  submittedOrder.value = null
}

// 退出到登录页面
const exitToLogin = () => {
  router.push('/login')
}
</script>

<style scoped lang="scss">
@import './styles/guestOrder.scss';

.header-actions {
  margin-top: 15px;
}

.exit-button {
  background-color: #409eff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.exit-button:hover {
  background-color: #66b1ff;
}
</style>