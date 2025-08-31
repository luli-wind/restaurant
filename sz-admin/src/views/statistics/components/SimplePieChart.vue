<template>
  <div class="simple-pie-chart">
    <div class="chart-header">
      <h3>{{ title }}</h3>
    </div>
    <div class="chart-container">
      <div class="chart-wrapper">
        <svg class="pie-chart" viewBox="0 0 100 100">
          <circle cx="50" cy="50" r="45" fill="#f0f0f0" />
          <path 
            v-for="(slice, index) in pieSlices" 
            :key="index"
            :d="slice.path"
            :fill="slice.color"
            :stroke="slice.stroke"
            stroke-width="0.5"
          />
        </svg>
      </div>
      <div class="legend">
        <div 
          v-for="(item, index) in chartData" 
          :key="index" 
          class="legend-item"
        >
          <div class="legend-color" :style="{ backgroundColor: getColor(index) }"></div>
          <div class="legend-label">{{ item.label }}</div>
          <div class="legend-value">{{ item.value }} ({{ item.percentage }}%)</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

interface ChartItem {
  label: string
  value: number
  percentage: number
}

const props = defineProps<{
  title: string
  data: ChartItem[]
}>()

// 颜色数组
const colors = [
  '#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399',
  '#00c1d4', '#ff9a00', '#ff6699', '#33cc33', '#9966ff'
]

// 获取颜色
const getColor = (index: number) => {
  return colors[index % colors.length]
}

// 计算饼图切片
const pieSlices = computed(() => {
  const slices = []
  let startAngle = 0
  
  for (let i = 0; i < props.data.length; i++) {
    const item = props.data[i]
    const sliceAngle = (item.percentage / 100) * 360
    const endAngle = startAngle + sliceAngle
    
    // 计算起始点和结束点
    const startX = 50 + 45 * Math.cos((Math.PI / 180) * (startAngle - 90))
    const startY = 50 + 45 * Math.sin((Math.PI / 180) * (startAngle - 90))
    const endX = 50 + 45 * Math.cos((Math.PI / 180) * (endAngle - 90))
    const endY = 50 + 45 * Math.sin((Math.PI / 180) * (endAngle - 90))
    
    // 判断是否为大弧
    const largeArcFlag = sliceAngle > 180 ? 1 : 0
    
    // 生成路径数据
    const pathData = `M 50 50 L ${startX} ${startY} A 45 45 0 ${largeArcFlag} 1 ${endX} ${endY} Z`
    
    slices.push({
      path: pathData,
      color: getColor(i),
      stroke: '#ffffff'
    })
    
    startAngle = endAngle
  }
  
  return slices
})

// 图表数据
const chartData = computed(() => {
  return props.data.map((item, index) => ({
    ...item,
    color: getColor(index)
  }))
})
</script>

<style scoped lang="scss">
.simple-pie-chart {
  .chart-header {
    margin-bottom: 20px;

    h3 {
      margin: 0;
      font-size: 18px;
      color: #333;
    }
  }

  .chart-container {
    display: flex;
    align-items: center;

    .chart-wrapper {
      flex: 1;
      display: flex;
      justify-content: center;

      .pie-chart {
        width: 200px;
        height: 200px;
      }
    }

    .legend {
      width: 200px;
      padding-left: 20px;

      .legend-item {
        display: flex;
        align-items: center;
        margin-bottom: 10px;

        .legend-color {
          width: 12px;
          height: 12px;
          border-radius: 50%;
          margin-right: 8px;
        }

        .legend-label {
          flex: 1;
          font-size: 14px;
          color: #666;
        }

        .legend-value {
          font-size: 14px;
          color: #333;
          font-weight: 500;
        }
      }
    }
  }

  // 响应式设计
  @media (max-width: 768px) {
    .chart-container {
      flex-direction: column;

      .chart-wrapper {
        margin-bottom: 20px;
      }

      .legend {
        width: 100%;
        padding-left: 0;
      }
    }
  }
}
</style>