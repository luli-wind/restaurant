<template>
  <div class="chart-placeholder">
    <div class="chart-content">
      <el-icon :size="48" color="#c0c4cc"><PieChart /></el-icon>
      <p class="chart-title">{{ title }}</p>
      <p class="chart-desc">图表数据加载中...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { PieChart } from '@element-plus/icons-vue';

defineProps({
  title: {
    type: String,
    default: '数据图表'
  }
});
</script>

<style scoped lang="scss">
.chart-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  min-height: 300px;

  .chart-content {
    text-align: center;

    .chart-title {
      font-size: 18px;
      color: #666;
      margin: 20px 0 10px;
    }

    .chart-desc {
      font-size: 14px;
      color: #999;
    }
  }
}
</style>