<template>
  <div id="app">
    <AppHeader />
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import AppHeader from '@/components/Layout/AppHeader.vue';
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  line-height: 1.6;
  color: #1f2937;
  background: #f8fafc;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding-top: 1rem;
}

/* Global utility classes */
.text-center {
  text-align: center;
}

.text-left {
  text-align: left;
}

.text-right {
  text-align: right;
}

.font-weight-bold {
  font-weight: 700;
}

.font-weight-medium {
  font-weight: 500;
}

.color-primary {
  color: #3b82f6;
}

.color-success {
  color: #10b981;
}

.color-warning {
  color: #f59e0b;
}

.color-danger {
  color: #ef4444;
}

.bg-primary {
  background: #3b82f6;
}

.bg-success {
  background: #10b981;
}

.bg-warning {
  background: #f59e0b;
}

.bg-danger {
  background: #ef4444;
}

/* Responsive breakpoints */
@media (max-width: 640px) {
  .hide-mobile {
    display: none !important;
  }
}

@media (min-width: 641px) {
  .show-mobile {
    display: none !important;
  }
}

/* Loading states */
.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  color: #6b7280;
}

.loading::after {
  content: '';
  width: 20px;
  height: 20px;
  border: 2px solid #e5e7eb;
  border-top: 2px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-left: 0.5rem;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

/* Error states */
.error {
  background: #fee2e2;
  color: #991b1b;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #fecaca;
}

/* Success states */
.success {
  background: #d1fae5;
  color: #065f46;
  padding: 1rem;
  border-radius: 8px;
  border: 1px solid #a7f3d0;
}

/* Focus styles for accessibility */
/* *:focus {
  outline: 2px solid #f63b3b;
  outline-offset: 2px;
} */

button:focus,
input:focus,
select:focus,
textarea:focus {
  outline: none;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  border-color: #3b82f6;
}

/* Smooth transitions */
* {
  transition-property: background-color, border-color, color, fill, stroke, opacity, box-shadow, transform;
  transition-duration: 200ms;
  transition-timing-function: ease;
}
</style>