<template>
  <div ref="chartContainer" class="echarts-line-chart"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick, onUnmounted } from 'vue';
import * as echarts from 'echarts';

interface ChartItem {
  label: string;
  value: number;
}

const props = defineProps<{
  title: string;
  data: ChartItem[];
  maxValue?: number;
}>();

const chartContainer = ref<HTMLElement | null>(null);
let chartInstance: echarts.ECharts | null = null;

const initChart = () => {
  if (chartContainer.value) {
    chartInstance = echarts.init(chartContainer.value);
    updateChart();
  }
};

const updateChart = () => {
  if (!chartInstance || !props.data.length) return;

  const option: echarts.EChartsOption = {
    title: {
      text: props.title,
      textStyle: {
        fontSize: 18,
        color: '#333'
      },
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'line'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: props.data.map(item => item.label),
      axisTick: {
        alignWithLabel: true
      },
      axisLabel: {
        rotate: 0,
        interval: 0
      }
    },
    yAxis: {
      type: 'value',

    },
    series: [
      {
        type: 'line',
        data: props.data.map(item => item.value),
        smooth: true,
        lineStyle: {
          width: 3,
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 1,
            y2: 0,
            colorStops: [
              { offset: 0, color: '#83bff6' },
              { offset: 1, color: '#188df0' }
            ]
          }
        },
        areaStyle: {
          opacity: 0.3,
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#83bff6' },
              { offset: 1, color: '#188df0' }
            ]
          }
        },
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#188df0',
          borderWidth: 2,
          borderColor: '#fff'
        },
        emphasis: {
          itemStyle: {
            color: '#2378f7',
            borderWidth: 3,
            borderColor: '#fff'
          }
        }
      }
    ]
  };

  chartInstance.setOption(option, true);
};

// 监听数据变化，更新图表
watch(
  () => props.data,
  () => {
    nextTick(() => {
      updateChart();
    });
  },
  { deep: true }
);

// 监听容器大小变化，调整图表大小
const resizeChart = () => {
  if (chartInstance) {
    chartInstance.resize();
  }
};

// 组件挂载时初始化图表
onMounted(() => {
  initChart();
  window.addEventListener('resize', resizeChart);
});

// 组件卸载时销毁图表
onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose();
  }
  window.removeEventListener('resize', resizeChart);
});
</script>

<style scoped>
.echarts-line-chart {
  width: 100%;
  height: 400px;
}
</style>