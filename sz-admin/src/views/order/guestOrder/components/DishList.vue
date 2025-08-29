<template>
  <div class="dish-list-container">
    <div class="dish-list-header">
      <h2>菜品列表</h2>
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索菜品"
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <div class="dish-list-content">
      <div 
        v-for="dish in filteredDishes" 
        :key="dish.dishId" 
        class="dish-item"
      >
        <div class="dish-image">
          <img :src="dish.imageUrl" :alt="dish.dishName" />
        </div>
        <div class="dish-info">
          <h3>{{ dish.dishName }}</h3>
          <p class="dish-description">{{ dish.description }}</p>
          <div class="dish-price">¥{{ Number(dish.price).toFixed(2) }}</div>
        </div>
        <div class="dish-actions">
          <el-button 
            type="primary" 
            @click="addToCart(dish)"
          >
            加入购物车
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getAllDishListApi } from '@/api/modules/restaurant/dish'
import type { DishRow } from '@/api/types/restaurant/dish'

// 搜索关键词
const searchKeyword = ref('')

// 菜品列表
const dishes = ref<DishRow[]>([])

// 过滤后的菜品列表
const filteredDishes = computed(() => {
  if (!searchKeyword.value) {
    return dishes.value
  }
  const keyword = searchKeyword.value.toLowerCase()
  return dishes.value.filter(dish => 
    dish.dishName.toLowerCase().includes(keyword) ||
    (dish.description && dish.description.toLowerCase().includes(keyword))
  )
})

// 获取菜品列表
const fetchDishList = async () => {
  try {
    const response = await getAllDishListApi()
    dishes.value = response.data || []
  } catch (error) {
    console.error('获取菜品列表失败:', error)
  }
}

// 搜索处理
const handleSearch = () => {
  // 过滤逻辑已经在 computed 中实现
}

// 添加到购物车
const emit = defineEmits<{
  (e: 'add-to-cart', dish: DishRow): void
}>()

const addToCart = (dish: DishRow) => {
  emit('add-to-cart', dish)
}

// 组件挂载时获取菜品列表
onMounted(() => {
  fetchDishList()
})
</script>

<style scoped lang="scss">
.dish-list-container {
  flex: 1;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.dish-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h2 {
    margin: 0;
    font-size: 24px;
    color: #333;
  }
  
  .search-box {
    width: 300px;
  }
}

.dish-list-content {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.dish-item {
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  }
}

.dish-image {
  height: 200px;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.dish-info {
  padding: 16px;
  flex: 1;
  
  h3 {
    margin: 0 0 8px 0;
    font-size: 18px;
    color: #333;
  }
  
  .dish-description {
    margin: 0 0 12px 0;
    color: #666;
    font-size: 14px;
    line-height: 1.4;
  }
  
  .dish-price {
    font-size: 20px;
    font-weight: bold;
    color: #ff6600;
  }
}

.dish-actions {
  padding: 0 16px 16px;
}
</style>