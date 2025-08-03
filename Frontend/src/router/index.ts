import { createRouter, createWebHistory } from 'vue-router';
import Dashboard from '../views/Dashboard.vue';
import FactoriesView from '../views/FactoriesView.vue';
import ProductsView from '../views/ProductsView.vue';
import SalesView from '../views/SalesView.vue';
import TargetsView from '../views/TargetsView.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Dashboard',
      component: Dashboard
    },
    {
      path: '/factories',
      name: 'Factories',
      component: FactoriesView
    },
    {
      path: '/products',
      name: 'Products',
      component: ProductsView
    },
    {
      path: '/sales',
      name: 'Sales',
      component: SalesView
    },
    {
      path: '/targets',
      name: 'Targets',
      component: TargetsView
    }
  ]
});

export default router;