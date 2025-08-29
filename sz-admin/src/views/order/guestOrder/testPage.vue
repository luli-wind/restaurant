<template>
  <div class="test-page-container">
    <h1>访客下单功能测试页面</h1>
    
    <div class="test-section">
      <h2>测试菜品列表</h2>
      <div class="dish-grid">
        <div 
          v-for="dish in testDishes" 
          :key="dish.dishId" 
          class="dish-card"
          @click="addToCart(dish)"
        >
          <img :src="dish.imageUrl" :alt="dish.dishName" />
          <div class="dish-info">
            <h3>{{ dish.dishName }}</h3>
            <p>{{ dish.description }}</p>
            <div class="dish-price">¥{{ Number(dish.price).toFixed(2) }}</div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="test-section">
      <h2>测试购物车</h2>
      <div v-if="cartItems.length === 0" class="empty-cart">
        购物车为空
      </div>
      <div v-else>
        <div 
          v-for="item in cartItems" 
          :key="item.dishId" 
          class="cart-item"
        >
          <span>{{ item.dishName }}</span>
          <span>¥{{ Number(item.price).toFixed(2) }} x {{ item.quantity }}</span>
          <span>¥{{ (Number(item.price) * item.quantity).toFixed(2) }}</span>
        </div>
        <div class="cart-total">
          总计: ¥{{ cartTotal.toFixed(2) }}
        </div>
      </div>
    </div>
    
    <div class="test-section">
      <h2>测试订单表单</h2>
      <div class="form-group">
        <label>姓名:</label>
        <input v-model="orderData.customerName" placeholder="请输入姓名" />
      </div>
      <div class="form-group">
        <label>电话:</label>
        <input v-model="orderData.customerPhone" placeholder="请输入电话" />
      </div>
      <div class="form-group">
        <label>地址:</label>
        <textarea v-model="orderData.deliveryAddress" placeholder="请输入配送地址"></textarea>
      </div>
      <div class="form-group">
        <label>备注:</label>
        <textarea v-model="orderData.remark" placeholder="请输入备注"></textarea>
      </div>
      <button @click="submitTestOrder">提交测试订单</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { CartItem } from '@/views/order/guestOrder/types/index'
import { testDishes, testCartItems, testOrderData } from '@/views/order/guestOrder/testData'

// 购物车项目
const cartItems = ref<CartItem[]>([])

// 订单数据
const orderData = ref({ ...testOrderData })

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

// 提交测试订单
const submitTestOrder = () => {
  const orderItems = cartItems.value.map(item => ({
    dishId: parseInt(item.dishId),
    dishName: item.dishName,
    imageUrl: item.imageUrl,
    number: item.quantity,
    amount: Number(item.price)
  }))
  
  const orderForm = {
    ...orderData.value,
    orderItems,
    totalAmount: cartTotal.value
  }
  
  console.log('提交的订单数据:', orderForm)
  alert('订单已提交，详情请查看控制台')
}
</script>

<style scoped lang="scss">
.test-page-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.test-section {
  margin-bottom: 40px;
  padding: 20px;
  background: #f5f5f5;
  border-radius: 8px;
  
  h2 {
    margin-top: 0;
  }
}

.dish-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.dish-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s;
  
  &:hover {
    transform: translateY(-4px);
  }
  
  img {
    width: 100%;
    height: 150px;
    object-fit: cover;
  }
  
  .dish-info {
    padding: 16px;
    
    h3 {
      margin: 0 0 8px 0;
      font-size: 18px;
    }
    
    p {
      margin: 0 0 12px 0;
      color: #666;
      font-size: 14px;
    }
    
    .dish-price {
      font-size: 20px;
      font-weight: bold;
      color: #ff6600;
    }
  }
}

.cart-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.cart-total {
  font-size: 18px;
  font-weight: bold;
  text-align: right;
  margin-top: 10px;
}

.form-group {
  margin-bottom: 16px;
  
  label {
    display: block;
    margin-bottom: 4px;
    font-weight: bold;
  }
  
  input, textarea {
    width: 100%;
    padding: 8px;
    border: 1px solid #ddd;
    border-radius: 4px;
  }
  
  textarea {
    min-height: 80px;
  }
}

button {
  padding: 12px 24px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  
  &:hover {
    background: #66b1ff;
  }
}
</style>