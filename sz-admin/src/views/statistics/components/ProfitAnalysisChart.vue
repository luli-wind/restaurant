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
        <div class="chart-bars">
          <div 
            v-for="(item, index) in chartData" 
            :key="index" 
            class="bar-container">
            <div class="bar-group">
              <div 
                class="bar bar-revenue" 
                :style="{ height: calculateBarHeight(item.revenue) }">
                <div class="bar-value">¥{{ formatValue(item.revenue) }}</div>
              </div>
              <div 
                class="bar bar-cost" 
                :style="{ height: calculateBarHeight(item.cost) }">
                <div class="bar-value">¥{{ formatValue(item.cost) }}</div>
              </div>
              <div 
                class="bar bar-profit" 
                :style="{ height: calculateBarHeight(item.profit) }">
                <div class="bar-value">¥{{ formatValue(item.profit) }}</div>
              </div>
            </div>
            <div class="bar-label">{{ item.date }}</div>
          </div>
        </div>
        <div class="profit-line">
          <div 
            v-for="(item, index) in chartData" 
            :key="index"
            class="profit-point"
            :style="{ left: `${(index / (chartData.length - 1)) * 100}%`, bottom: `${item.profitMargin}%` }">
            <div class="profit-tooltip">{{ item.profitMargin }}%</div>
          </div>
          <svg class="profit-svg">
            <polyline 
              :points="profitLinePoints"
              fill="none"
              stroke="#ff6b35"
              stroke-width="2" />
          </svg>
        </div>
        <div class="x-axis"></div>
      </div>
    </div>
    <div class="chart-legend">
      <div class="legend-item">
        <div class="legend-color revenue-color"></div>
        <span>收入</span>
      </div>
      <div class="legend-item">
        <div class="legend-color cost-color"></div>
        <span>成本</span>
      </div>
      <div class="legend-item">
        <div class="legend-color profit-color"></div>
        <span>利润</span>
      </div>
      <div class="legend-item">
        <div class="legend-color margin-color"></div>
        <span>利润率</span>
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

// 计算最大值
const maxValue = computed(() => {
  const values = props.data.flatMap(item => [item.revenue, item.cost, item.profit])
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

// 计算柱状图高度
const calculateBarHeight = (value: number) => {
  return `${(value / maxValue.value) * 100}%`
}

// 格式化数值
const formatValue = (value: number) => {
  if (value >= 10000) {
    return `${(value / 10000).toFixed(1)}w`
  } else if (value >= 1000) {
    return `${(value / 1000).toFixed(1)}k`
  }
  return value.toFixed(0)
}

// 计算利润率折线图的点
const profitLinePoints = computed(() => {
  if (!props.data.length) return ''
  
  const points = props.data.map((item, index) => {
    const x = (index / (props.data.length - 1)) * 100
    const y = 100 - (item.profitMargin / 100) * 100 // 反转Y轴
    return `${x},${y}`
  })
  return points.join(' ')
})
</script>

<style scoped lang="scss">
.profit-analysis-chart {
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
    height: 350px;
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

      .chart-bars {
        display: flex;
        align-items: flex-end;
        height: 300px;
        padding: 10px 0;
        border-bottom: 1px solid #eee;
        border-left: 1px solid #eee;

        .bar-container {
          flex: 1;
          display: flex;
          flex-direction: column;
          align-items: center;
          padding: 0 5px;

          .bar-group {
            display: flex;
            align-items: flex-end;
            height: 100%;
            gap: 2px;

            .bar {
              width: 20px;
              position: relative;
              transition: all 0.3s ease;
              border-radius: 2px 2px 0 0;

              &:hover {
                opacity: 0.8;
                transform: scale(1.05);
              }

              .bar-value {
                position: absolute;
                top: -25px;
                left: 50%;
                transform: translateX(-50%);
                font-size: 11px;
                color: #666;
                white-space: nowrap;
              }

              &.bar-revenue {
                background-color: #67c23a;
              }

              &.bar-cost {
                background-color: #f56c6c;
              }

              &.bar-profit {
                background-color: #409eff;
              }
            }
          }

          .bar-label {
            margin-top: 10px;
            font-size: 12px;
            color: #666;
            text-align: center;
          }
        }
      }

      .profit-line {
        position: absolute;
        top: 10px;
        left: 0;
        right: 0;
        bottom: 30px;
        pointer-events: none;

        .profit-point {
          position: absolute;
          width: 8px;
          height: 8px;
          background-color: #ff6b35;
          border-radius: 50%;
          transform: translate(-50%, 50%);
          transition: all 0.3s ease;

          &:hover {
            transform: translate(-50%, 50%) scale(1.5);
          }

          .profit-tooltip {
            position: absolute;
            bottom: 15px;
            left: 50%;
            transform: translateX(-50%);
            background-color: rgba(0, 0, 0, 0.7);
            color: white;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 11px;
            white-space: nowrap;
            opacity: 0;
            transition: opacity 0.3s ease;

            .profit-point:hover & {
              opacity: 1;
            }
          }
        }

        .profit-svg {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          pointer-events: none;
        }
      }

      .x-axis {
        height: 30px;
        border-top: 1px solid #eee;
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