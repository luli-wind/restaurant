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

    <!-- 菜品分类标签页 -->
    <div class="category-tabs">
      <!-- 分类标签页 -->
      <el-tabs v-model="activeCategory" type="card" @tab-change="changeCategory">
        <el-tab-pane
          v-for="category in dishCategories"
          :key="category.id"
          :label="category.codeName"
          :name="category.id"
        >
        </el-tab-pane>
      </el-tabs>
      
      <!-- 按分类分组展示菜品 -->
      <div v-for="(dishes, categoryName) in groupedDishes" :key="categoryName" class="dish-group">
        <h3 class="group-title">{{ getCategoryName(categoryName) }}</h3>
        <div class="dish-list-content">
          <div
            v-for="dish in dishes"
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
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getAllDishListApi } from '@/api/modules/restaurant/dish'
import { useDictOptions } from '@/hooks/useDictOptions'
import type { DishRow } from '@/api/types/restaurant/dish'

// 搜索关键词
const searchKeyword = ref('')

// 菜品列表
const dishes = ref<DishRow[]>([])

// 菜品分类
const dishCategories = useDictOptions('dish_category')
const activeCategory = ref('')

// 过滤后的菜品列表
const filteredDishes = computed(() => {
  let filtered = dishes.value
  
  // 按分类过滤
  if (activeCategory.value) {
    filtered = filtered.filter(dish => dish.category === activeCategory.value)
  }
  
  // 按搜索关键词过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(dish =>
      dish.dishName.toLowerCase().includes(keyword) ||
      (dish.description && dish.description.toLowerCase().includes(keyword))
    )
  }
  
  return filtered
})

// 按分类分组的菜品
const groupedDishes = computed(() => {
  const groups: Record<string, DishRow[]> = {}
  
  filteredDishes.value.forEach(dish => {
    const category = dish.category || '未分类'
    if (!groups[category]) {
      groups[category] = []
    }
    groups[category].push(dish)
  })
  
  return groups
})

// 获取分类名称
const getCategoryName = (categoryValue: string) => {
  const category = dishCategories.value.find(cat => cat.id === categoryValue)
  return category ? category.codeName : categoryValue || '未分类'
}

// 获取菜品列表
const fetchDishList = async () => {
  try {
    const response = await getAllDishListApi()
    console.log(response)
    dishes.value = response.data || []
    
    // 设置默认分类为第一个分类
    if (dishCategories.value && dishCategories.value.length > 0 && !activeCategory.value) {
      activeCategory.value = dishCategories.value[0].id
    }
  } catch (error) {
    console.error('获取菜品列表失败:', error)
  }
}

// 搜索处理
const handleSearch = () => {
  // 过滤逻辑已经在 computed 中实现
}

// 切换分类
const changeCategory = (categoryId: string) => {
  activeCategory.value = categoryId
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