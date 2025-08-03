<template>
  <div class="dashboard">
    <!-- <div class="dashboard-header">
      <h1 class="page-title">Dashboard</h1>
      <p class="page-subtitle">Candy distributor performance overview</p>
    </div> -->

    <div class="kpi-grid">
      <div class="kpi-card">
        <VIcon name="star-dollar-line" class="kpi-icon"  />
        <div class="kpi-content">
          <h3 class="kpi-value">${{ analyticsStore.summary.totalSales.toLocaleString('en-US', {
            minimumFractionDigits:
              2, maximumFractionDigits: 2
          }) }}</h3>
          <p class="kpi-label">Total Sales</p>
        </div>
      </div>

      <div class="kpi-card">
        <VIcon name="calendar-clock-line" class="kpi-icon"  />
        <div class="kpi-content">
          <h3 class="kpi-value">${{ analyticsStore.summary.totalTarget.toLocaleString('en-US', {
            minimumFractionDigits:
              2, maximumFractionDigits: 2
          }) }}</h3>
          <p class="kpi-label">Total Target</p>
        </div>
      </div>

      <div class="kpi-card">
        <VIcon name="report-line" class="kpi-icon"  />
        <div class="kpi-content">
          <h3 class="kpi-value">{{ analyticsStore.summary.percentageGoalAchieved.toFixed(2) }}%</h3>
          <p class="kpi-label">Performance Achieved</p>
        </div>
      </div>

      <div class="kpi-card">
        <VIcon name="cart-full-solid" class="kpi-icon"  />
        <div class="kpi-content">
          <h3 class="kpi-value">{{ analyticsStore.summary.salesCount.toLocaleString() }}</h3>
          <p class="kpi-label">Total Orders</p>
        </div>
      </div>

    </div>
   
    <div class="chart-card">
      <h2 class="chart-title">Sales Locations</h2>
      <SalesMap />
    </div>
  
    

    <div class="chart-card" v-if="analyticsStore.last12MonthsSales.length">
      <SalesChart :datasets="[{
        label: 'Total Sales',
        data: analyticsStore.last12MonthsSales.map(item => ({ label: item.month, value: item.totalSales })),
        type: 'line',
        borderColor: 'rgba(59, 130, 246, 1)',
        backgroundColor: 'rgba(59, 130, 246, 0.2)',
        fill: true
      }]" title="Last 12 Months Sales Performance" />
    </div>

    <div class="chart-card" v-if="analyticsStore.salesByProduct.length">
      <SalesChart :datasets="[{
        label: 'Total Sales',
        data: analyticsStore.salesByProduct.map(item => ({ label: item.productName, value: item.totalSales })),
        type: 'bar',
        backgroundColor: 'rgba(168, 85, 247, 0.8)', 
        borderColor: 'rgba(168, 85, 247, 1)',
      }]" title="Sales by Product" />
    </div>
    <div class="chart-card" v-if="analyticsStore.divisionPerformance.length">
      <SalesChart :datasets="[
        {
          label: 'Total Sales',
          data: analyticsStore.divisionPerformance.map(item => ({ label: item.division, value: item.totalSales })),
          type: 'bar',
          backgroundColor: 'rgba(34, 197, 94, 0.8)',
          borderColor: 'rgba(34, 197, 94, 1)',
        },
        {
          label: 'Target',
          data: analyticsStore.divisionPerformance.map(item => ({ label: item.division, value: item.target })),
          type: 'bar',
          backgroundColor: 'rgba(251, 191, 36, 0.8)', 
          borderColor: 'rgba(251, 191, 36, 1)',
        }
      ]" title="Division Sales vs. Target" />
    </div>



  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useAnalyticsStore } from '../api/dashboard';
import SalesChart from '../components/Charts/SalesChart.vue';
import SalesMap from '../views/SalesMap.vue';
import { VIcon } from '@vonage/vivid-vue';

const analyticsStore = useAnalyticsStore();
const loading = ref(true);
const error = ref<string | null>(null);

onMounted(async () => {
  try {
    loading.value = true;
    error.value = null;
    await Promise.all([
      analyticsStore.fetchAnalyticsSummary(),
      analyticsStore.fetchLast12MonthsSales(),
      analyticsStore.fetchDivisionPerformance(),
      analyticsStore.fetchSalesByProduct()
    ]);
  } catch (err: any) {
    error.value = err.message || 'Failed to fetch analytics data.';
    console.error('Dashboard mount error:', err);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped>
.dashboard {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

.dashboard-header {
  margin-bottom: 2rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 0.5rem 0;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #6b7280;
  margin: 0;
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.kpi-card {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 1.5rem;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.kpi-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.kpi-icon {
  font-size: 3rem;
  opacity: 0.8;
}

.kpi-value {
  font-size: 2rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 0.25rem 0;
}

.kpi-label {
  font-size: 0.9rem;
  color: #6b7280;
  margin: 0;
  font-weight: 500;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2rem;
  margin-bottom: 3rem;
}

.charts-grid-two {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
  margin-bottom: 3rem;
}


.chart-card {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
 
  width: 100%;
}

@media (max-width: 1200px) {
  .charts-grid-two {
    grid-template-columns: 1fr;
    
  }
}

@media (max-width: 768px) {
  .dashboard {
    padding: 1rem;
  }

  .chart-card {
    padding: 1rem;
   
    margin-bottom: 1.5rem;
  }

  .performance-details {
    flex-direction: column;
    gap: 0.5rem;
  }

  .performance-item {
    flex-direction: row;
    justify-content: space-between;
  }
}

.chart-card {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;

  width: 100%;
}

.chart-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 1.5rem 0;
}

.performance-section {
  margin-bottom: 3rem;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 1.5rem 0;
}

.performance-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.performance-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.performance-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.division-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.performance-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}

.performance-badge.success {
  background: #d1fae5;
  color: #065f46;
}

.performance-badge.warning {
  background: #fef3c7;
  color: #92400e;
}

.performance-badge.danger {
  background: #fee2e2;
  color: #991b1b;
}

.performance-details {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.performance-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.label {
  font-size: 0.8rem;
  color: #6b7280;
  font-weight: 500;
}

.value {
  font-size: 0.9rem;
  color: #1f2937;
  font-weight: 600;
}

.progress-bar {
  height: 8px;
  background: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  transition: width 0.5s ease;
}

.progress-fill.success {
  background: #10b981;
}

.progress-fill.warning {
  background: #f59e0b;
}

.progress-fill.danger {
  background: #ef4444;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.action-card {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-decoration: none;
  color: inherit;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  text-align: center;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.action-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.action-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 0.5rem 0;
}

.action-description {
  font-size: 0.9rem;
  color: #6b7280;
  margin: 0;
}
</style>