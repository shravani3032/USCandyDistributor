// src/components/Charts/SalesChart.vue

<template>
  <div class="chart-container">
    <canvas ref="chartCanvas"></canvas>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue';
import {
  Chart,
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  LineController,
  BarController,
  Title,
  Tooltip,
  Legend,
  type ChartConfiguration, 
  type ChartDataset 
} from 'chart.js';

Chart.register(
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  LineController,
  BarController,
  Title,
  Tooltip,
  Legend
);

interface ChartDataPoint {
  label: string; 
  value: number; 
}

interface Props {
  
  datasets: {
    label: string;
    data: ChartDataPoint[];
    type?: 'bar' | 'line'; 
    backgroundColor?: string | string[];
    borderColor?: string | string[];
    fill?: boolean;
    yAxisID?: string; 
  }[];
  type?: 'bar' | 'line'; 
  title?: string;
}

const props = withDefaults(defineProps<Props>(), {
  type: 'bar', 
  title: 'Chart'
});

const chartCanvas = ref<HTMLCanvasElement | null>(null);
let chartInstance: Chart | null = null;

function createChart() {
  if (!chartCanvas.value || !props.datasets.length) return;

  const chartDatasets: ChartDataset<"bar" | "line", (number | [number, number] | null)[]>[] = props.datasets.map(ds => ({
    type: ds.type || props.type, // Use dataset type or fallback to prop type
    label: ds.label,
    data: ds.data.map(d => d.value),
    backgroundColor: ds.backgroundColor || (ds.type === 'bar' ? 'rgba(59, 130, 246, 0.8)' : 'rgba(59, 130, 246, 0.2)'),
    borderColor: ds.borderColor || 'rgba(59, 130, 246, 1)',
    borderWidth: 2,
    fill: ds.fill !== undefined ? ds.fill : (ds.type === 'line'), 
    yAxisID: ds.yAxisID,
  }));

  const labels = props.datasets.length > 0 ? props.datasets[0].data.map(d => d.label) : [];

  const config: ChartConfiguration = {
    type: props.type, 
    data: {
      labels: labels,
      datasets: chartDatasets
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        title: {
          display: true,
          text: props.title,
          font: {
            size: 16,
            weight: 'bold'
          }
        },
        tooltip: {
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          titleColor: 'white',
          bodyColor: 'white',
          callbacks: {
            label: (context) => {
             
              const datasetLabel = context.dataset.label;
              const value = context.parsed.y;
              if (datasetLabel.includes('Percentage')) {
                return `${datasetLabel}: ${value.toFixed(2)}%`;
              }
              return `${datasetLabel}: $${value.toLocaleString()}`;
            }
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            callback: (value) => `$${Number(value).toLocaleString()}` // Default to currency
          }
        },
        
      }
    }
  };


  const hasPercentageData = props.datasets.some(ds => ds.label.includes('Percentage'));
  if (hasPercentageData) {
    if (config.options && config.options.scales && config.options.scales.y) {
      (config.options.scales.y as any).ticks.callback = (value: number) => `${Number(value).toLocaleString()}%`;
    }
  }


  chartInstance = new Chart(chartCanvas.value, config);
}

function destroyChart() {
  if (chartInstance) {
    chartInstance.destroy();
    chartInstance = null;
  }
}

watch(() => props.datasets, () => { 
  destroyChart();
  createChart();
}, { deep: true });

onMounted(createChart);
onUnmounted(destroyChart);
</script>

<style scoped>
.chart-container {
  position: relative;
  height: 400px;
  width: 100%;
}
</style>