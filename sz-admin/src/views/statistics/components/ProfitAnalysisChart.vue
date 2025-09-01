<template>
  <div class="profit-analysis-chart">
    <div class="chart-header">
      <h3>利润分析</h3>
    </div>
    <div class="chart-container">
      <div class="y-axis">
        <div class="y-axis-label" v-for="label in yAxisLabels" :key="label">
          {{ label }}
        </div>
      </div>
      <div class="chart-content">
        <svg class="chart-svg">
          <!-- 利润折线 -->
          <polyline
            :points="profitLinePoints"
            fill="none"
            stroke="#409eff"
            stroke-width="3" />
        </svg>
        <div class="x-axis">
          <div
            v-for="(item, index) in chartData"
            :key="index"
            class="x-axis-label"
            :style="{ left: `${(index / (chartData.length - 1)) * 100}%` }">
            {{ item.date }}
          </div>
        </div>
      </div>
    </div>
    <div class="chart-legend">
      <div class="legend-item">
        <div class="legend-color profit-color"></div>
        <span>利润</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface ProfitData {
  date: string
  revenue: number
  cost: number
  profit: number
  profitMargin: number
}

const props = defineProps<{
  data: ProfitData[]
}>()

const chartData = props.data

// 计算最大值
const maxValue = computed(() => {
  const values = props.data.map(item => item.profit) // 只获取利润数据的最大值
  return Math.max(...values) * 1.1 // 留出10%空间
})

// 计算Y轴标签
const yAxisLabels = computed(() => {
  const max = maxValue.value
  const step = max / 4
  return [
    max.toFixed(0),
    (step * 3).toFixed(0),
    (step * 2).toFixed(0),
    step.toFixed(0),
    '0'
  ]
})

// 计算利润折线图的点
const profitLinePoints = computed(() => {
  if (!props.data.length) return ''
  
  const points = props.data.map((item, index) => {
    const x = (index / (props.data.length - 1)) * 100
    const y = (item.profit / maxValue.value) * 100
    return `${x},${100 - y}` // 反转Y轴
  })
  return points.join(' ')
})
</script>

<style scoped lang="scss">
.profit-analysis-chart {
  width: 480px;
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
    height: 350px; /* 增加高度以提供更多空间 */
    position: relative;

    .y-axis {
      width: 50px;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      padding: 10px 0;
      font-size: 12px;
      color: #666;

      .y-axis-label {
        height: 20px;
        line-height: 20px;
        text-align: right;
        padding-right: 10px;
      }
    }

    .chart-content {
      flex: 1;
      position: relative;

      .chart-svg {
        position: absolute;
        top: 10px;
        left: 0;
        width: 100%;
        height: 300px;
        pointer-events: none;
      }

      .x-axis {
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        height: 30px;
        border-top: 1px solid #eee;

        .x-axis-label {
          position: absolute;
          bottom: 0;
          transform: translateX(-50%) rotate(-45deg); /* 旋转标签以节省空间 */
          transform-origin: center bottom;
          font-size: 11px; /* 减小字体大小 */
          color: #666;
          white-space: nowrap;
          max-width: 80px; /* 限制标签宽度 */
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }
  }

  .chart-legend {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 20px;

    .legend-item {
      display: flex;
      align-items: center;
      gap: 5px;
      font-size: 12px;
      color: #666;

      .legend-color {
        width: 12px;
        height: 12px;
        border-radius: 2px;

        &.revenue-color {
          background-color: #67c23a;
        }

        &.cost-color {
          background-color: #f56c6c;
        }

        &.profit-color {
          background-color: #409eff;
        }

        &.margin-color {
          background-color: #ff6b35;
        }
      }
    }
  }
}
</style>