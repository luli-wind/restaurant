<template>
  <div ref="chartContainer" class="echarts-bar-chart"></div>
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
  horizontal?: boolean;
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
        type: 'shadow'
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
        rotate: props.horizontal ? 0 : 45,
        interval: 0
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        type: 'bar',
        barWidth: '60%',
        data: props.data.map(item => item.value),
        itemStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: '#83bff6' },
              { offset: 0.5, color: '#188df0' },
              { offset: 1, color: '#188df0' }
            ]
          },
          borderRadius: [4, 4, 0, 0]
        },
        emphasis: {
          itemStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [
                { offset: 0, color: '#2378f7' },
                { offset: 0.7, color: '#2378f7' },
                { offset: 1, color: '#83bff6' }
              ]
            }
          }
        }
      }
    ]
  };

  // 如果是水平柱状图，交换xAxis和yAxis
  if (props.horizontal) {
    // @ts-ignore
    option.xAxis.type = 'value';
    // @ts-ignore
    option.yAxis.type = 'category';
    // @ts-ignore
    option.yAxis.data = props.data.map(item => item.label);
    // @ts-ignore
    option.xAxis.axisLabel = {};
    // @ts-ignore
    option.yAxis.axisLabel = {};
    
    // 调整系列数据
    // @ts-ignore
    option.series[0].data = props.data.map(item => item.value);
  }

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
.echarts-bar-chart {
  width: 100%;
  height: 400px;
}
</style>