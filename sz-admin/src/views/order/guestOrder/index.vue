<template>
  <div class="guest-order-container">
    <div class="page-header">
      <h1>访客下单</h1>
      <p>欢迎使用我们的在线订餐服务，请选择您喜欢的菜品</p>
      <div class="third-party-login" v-if="!isThirdPartyUser">
        <p>或使用第三方账号快速登录:</p>
        <div class="login-buttons">
          <button class="wechat-login" @click="loginWithWechat">微信登录</button>
          <button class="alipay-login" @click="loginWithAlipay">支付宝登录</button>
        </div>
      </div>
      <div class="header-actions">
        <button class="history-button" @click="goToOrderHistory" v-if="isThirdPartyUser">查看订单历史</button>
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
          :third-party-user-info="thirdPartyUserInfo"
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
import { thirdPartyLoginApi } from '@/api/modules/system/thirdPartyLogin'
import { logoutApi } from '@/api/modules/system/login'
import type { ThirdPartyLoginDTO, ThirdPartyLoginVO } from '@/api/types/system/thirdPartyLogin'
import { useUserStore } from '@/stores/modules/user'
import { useAuthStore } from '@/stores/modules/auth'
import type { TakeAwayOrderForm } from '@/api/types/order/takeAwayOrder'
import { loginWithAlipay as alipayLogin } from '@/utils/thirdPartyLogin'

// 路由实例
const router = useRouter()

// 购物车项目
const cartItems = ref<CartItem[]>([])

// 订单提交状态
const orderSubmitted = ref(false)
const submittedOrder = ref<any>(null)
// 第三方用户状态
const isThirdPartyUser = ref(false)
const thirdPartyUserInfo = ref<any>(null)

// 配送费和包装费
const deliveryFee = 5.00;  // 配送费5元
const packagingFee = 2.00; // 包装费2元

// 计算购物车总价（包含配送费和包装费）
const cartTotal = computed(() => {
  const itemsTotal = cartItems.value.reduce((total, item) => {
    return total + (Number(item.price) * item.quantity);
  }, 0);
  return itemsTotal + deliveryFee + packagingFee;
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
    // 配送费和包装费
    const deliveryFee = 5.00;  // 配送费5元
    const packagingFee = 2.00; // 包装费2元

    // 构造订单数据
    const orderForm: TakeAwayOrderForm = {
      customerName: orderData.customerName,
      customerPhone: orderData.customerPhone,
      deliveryAddress: orderData.deliveryAddress,
      remark: orderData.remark,
      deliveryFee: deliveryFee,
      packagingFee: packagingFee,
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

// 微信登录
const loginWithWechat = async () => {
  try {
    // 调用实际的微信登录逻辑
    // 这里应该是调用微信SDK获取授权码，然后调用后端接口
    // 暂时使用模拟数据
    const mockWechatUserInfo = {
      openId: 'mock-wechat-openid-' + Date.now(),
      provider: 'wechat',
      nickname: '微信用户' + Math.floor(Math.random() * 1000),
      avatarUrl: ''
    };
    
    const params: ThirdPartyLoginDTO = {
      openId: mockWechatUserInfo.openId,
      provider: mockWechatUserInfo.provider,
      nickname: mockWechatUserInfo.nickname,
      avatarUrl: mockWechatUserInfo.avatarUrl
    }
    
    const response = await thirdPartyLoginApi(params)
    
    // 登录成功，保存用户信息
    isThirdPartyUser.value = true
    thirdPartyUserInfo.value = response.data
    
    // 保存到本地存储，供订单历史页面使用
    localStorage.setItem('thirdPartyUserInfo', JSON.stringify(response.data))
    
    // 可以在这里添加自动填充表单的逻辑
    console.log('微信登录成功:', response.data)
  } catch (error) {
    console.error('微信登录失败:', error)
  }
}

// 支付宝登录
const loginWithAlipay = async () => {
  try {
    // 调用实际的支付宝登录逻辑
    // 使用支付宝SDK获取用户信息
    const alipayUserInfo = await alipayLogin();
    
    const params: ThirdPartyLoginDTO = {
      openId: alipayUserInfo.openId,
      provider: alipayUserInfo.provider,
      nickname: alipayUserInfo.nickname,
      avatarUrl: alipayUserInfo.avatarUrl,
      phone: alipayUserInfo.phone,
      email: alipayUserInfo.email
    }
    
    const response = await thirdPartyLoginApi(params)
    
    // 登录成功，保存用户信息
    isThirdPartyUser.value = true
    thirdPartyUserInfo.value = response.data
    
    // 保存到本地存储，供订单历史页面使用
    localStorage.setItem('thirdPartyUserInfo', JSON.stringify(response.data))
    
    // 可以在这里添加自动填充表单的逻辑
    console.log('支付宝登录成功:', response.data)
  } catch (error) {
    console.error('支付宝登录失败:', error)
  }
}

// 跳转到订单历史页面
const goToOrderHistory = () => {
  router.push('/order/history')
}

// 退出到登录页面
const exitToLogin = async () => {
  try {
    router.push('/login')
  } catch (error) {
    router.push('/login')
  }
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

.third-party-login {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background-color: #f9f9f9;
}

.third-party-login p {
  margin: 0 0 10px 0;
  color: #666;
  font-size: 14px;
}

.login-buttons {
  display: flex;
  gap: 10px;
}

.wechat-login, .alipay-login {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  color: white;
}

.wechat-login {
  background-color: #2ab548;
}

.wechat-login:hover {
  background-color: #25a040;
}

.alipay-login {
  background-color: #108ee9;
}

.alipay-login:hover {
  background-color: #0e7fd1;
}

.history-button {
  background-color: #67c23a;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  margin-right: 10px;
}

.history-button:hover {
  background-color: #85ce61;
}
</style>