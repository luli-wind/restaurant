<template>
  <div class="shopping-cart-container">
    <div class="cart-header">
      <h2>购物车</h2>
      <el-button 
        type="danger" 
        plain 
        :disabled="cartItems.length === 0"
        @click="clearCart"
      >
        清空购物车
      </el-button>
    </div>

    <div class="cart-content">
      <div v-if="cartItems.length === 0" class="empty-cart">
        <el-icon><ShoppingCart /></el-icon>
        <p>购物车为空</p>
      </div>

      <div v-else>
        <div 
          v-for="item in cartItems" 
          :key="item.dishId" 
          class="cart-item"
        >
          <div class="item-image">
            <img :src="item.imageUrl" :alt="item.dishName" />
          </div>
          <div class="item-info">
            <h3>{{ item.dishName }}</h3>
            <div class="item-price">¥{{ Number(item.price).toFixed(2) }}</div>
          </div>
          <div class="item-quantity">
            <el-input-number
              v-model="item.quantity"
              :min="1"
              :max="99"
              @change="updateQuantity(item.dishId, $event)"
            />
          </div>
          <div class="item-total">
            ¥{{ (item.price * item.quantity).toFixed(2) }}
          </div>
          <div class="item-actions">
            <el-button 
              type="danger" 
              :icon="Delete" 
              circle 
              @click="removeItem(item.dishId)"
            />
          </div>
        </div>
      </div>
    </div>

    <div class="cart-footer">
      <div class="cart-total">
        <span>总计:</span>
        <span class="total-amount">¥{{ Number(cartTotal).toFixed(2) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { ShoppingCart, Delete } from '@element-plus/icons-vue'
import type { CartItem } from '../types/index'

// 定义props
const props = defineProps<{
  cartItems: CartItem[]
}>()

// 定义emits
const emit = defineEmits<{
  (e: 'update-quantity', dishId: string, quantity: number): void
  (e: 'remove-item', dishId: string): void
  (e: 'clear-cart'): void
}>()

// 计算购物车总价
const cartTotal = computed(() => {
  return props.cartItems.reduce((total, item) => {
    return total + (item.price * item.quantity)
  }, 0)
})

// 更新数量
const updateQuantity = (dishId: string, quantity: number) => {
  emit('update-quantity', dishId, quantity)
}

// 移除项目
const removeItem = (dishId: string) => {
  emit('remove-item', dishId)
}

// 清空购物车
const clearCart = () => {
  emit('clear-cart')
}
</script>

<style scoped lang="scss">
.shopping-cart-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  overflow: hidden;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
  
  h2 {
    margin: 0;
    font-size: 20px;
    color: #333;
  }
}

.cart-content {
  padding: 0 20px;
  max-height: 400px;
  overflow-y: auto;
}

.empty-cart {
  text-align: center;
  padding: 40px 0;
  color: #999;
  
  .el-icon {
    font-size: 48px;
    margin-bottom: 16px;
  }
  
  p {
    margin: 0;
    font-size: 16px;
  }
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #eee;
  
  &:last-child {
    border-bottom: none;
  }
}

.item-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  overflow: hidden;
  margin-right: 16px;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.item-info {
  flex: 1;
  
  h3 {
    margin: 0 0 8px 0;
    font-size: 16px;
    color: #333;
  }
  
  .item-price {
    font-size: 16px;
    font-weight: bold;
    color: #ff6600;
  }
}

.item-quantity {
  margin: 0 16px;
}

.item-total {
  width: 80px;
  text-align: right;
  font-weight: bold;
  color: #333;
  font-size: 16px;
}

.item-actions {
  margin-left: 16px;
}

.cart-footer {
  padding: 16px 20px;
  border-top: 1px solid #eee;
  
  .cart-total {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 18px;
    font-weight: bold;
    
    .total-amount {
      color: #ff6600;
      font-size: 24px;
    }
  }
}
</style>