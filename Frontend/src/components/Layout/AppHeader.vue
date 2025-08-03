<template>
  <VHeader class="vivid-header">
    <!-- Left side brand -->
    <div class="brand-title">
      <VIcon name="vonage-color" size="large" class="brand-icon" />
      Candy Distributor
    </div>

    <!-- Right side nav items -->
    <template #action-items>
      <VNavItem v-for="link in navLinks" :key="link.path" :text="link.label" :icon="link.icon"
        :current="$route.path === link.path" @click="navigate(link.path)" />
    </template>
  </VHeader>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router';
import { VHeader, VNavItem, VIcon } from '@vonage/vivid-vue';

const router = useRouter();
const $route = useRoute();

// ✅ Supported vivid-vue icons
const navLinks = [
  { path: '/', label: 'Dashboard', icon: 'chart-line' },
  { path: '/factories', label: 'Factories', icon: 'company-line' },
  { path: '/products', label: 'Products', icon: 'packet-2-line' },
  { path: '/sales', label: 'Sales', icon: 'credit-card' },
  { path: '/targets', label: 'Targets', icon: 'calendar-clock-line' }
];

function navigate(path: string) {
  if ($route.path !== path) {
    router.push(path);
  }
}
</script>

<style scoped>
.vivid-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100vw;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.brand-title {
  font-size: 1.6rem;
  font-weight: 700;
  color: rgb(16, 5, 5);
  white-space: nowrap;
}
</style>
