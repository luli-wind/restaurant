<template>
  <div class="simple-bar-chart">
    <div class="chart-header">
      <h3>{{ title }}</h3>
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
            <div 
              class="bar" 
              :style="{ height: calculateBarHeight(item.value) }"
              :class="getBarColorClass(index)">
              <div class="bar-value">{{ formatValue(item.value) }}</div>
            </div>
            <div class="bar-label">{{ item.label }}</div>
          </div>
        </div>
        <div class="x-axis"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

interface ChartItem {
  label: string;
  value: number;
}

const props = defineProps<{
  title: string;
  data: ChartItem[];
  maxValue?: number;
}>();

// 计算Y轴标签
const yAxisLabels = computed(() => {
  const max = props.maxValue || Math.max(...props.data.map(item => item.value));
  const step = max / 4;
  return [
    max.toFixed(0),
    (step * 3).toFixed(0),
    (step * 2).toFixed(0),
    step.toFixed(0),
    '0'
  ];
});

// 计算最大值
const maxValue = computed(() => {
  return props.maxValue || Math.max(...props.data.map(item => item.value));
});

// 计算柱状图高度
const calculateBarHeight = (value: number) => {
  return `${(value / maxValue.value) * 100}%`;
};

// 格式化数值
const formatValue = (value: number) => {
  if (value >= 10000) {
    return `${(value / 10000).toFixed(1)}w`;
  } else if (value >= 1000) {
    return `${(value / 1000).toFixed(1)}k`;
  }
  return value.toFixed(0);
};

// 获取柱状图颜色类
const getBarColorClass = (index: number) => {
  const colors = ['primary', 'success', 'warning', 'danger', 'info'];
  return `bar-${colors[index % colors.length]}`;
};
</script>

<style scoped lang="scss">
.simple-bar-chart {
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
          padding: 0 10px;

          .bar {
            width: 60%;
            min-width: 30px;
            background-color: #409eff;
            position: relative;
            transition: all 0.3s ease;
            border-radius: 4px 4px 0 0;

            &:hover {
              opacity: 0.8;
              transform: scale(1.05);
            }

            .bar-value {
              position: absolute;
              top: -25px;
              left: 50%;
              transform: translateX(-50%);
              font-size: 12px;
              color: #666;
              white-space: nowrap;
            }
          }

          .bar-label {
            margin-top: 10px;
            font-size: 12px;
            color: #666;
            text-align: center;
            word-break: break-all;
          }

          // 不同颜色的柱状图
          .bar-primary {
            background-color: #409eff;
          }

          .bar-success {
            background-color: #67c23a;
          }

          .bar-warning {
            background-color: #e6a23c;
          }

          .bar-danger {
            background-color: #f56c6c;
          }

          .bar-info {
            background-color: #909399;
          }
        }
      }

      .x-axis {
        height: 30px;
        border-top: 1px solid #eee;
      }
    }
  }
}
</style>