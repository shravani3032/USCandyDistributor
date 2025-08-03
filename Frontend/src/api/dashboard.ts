// src/stores/analytics.ts

import { defineStore } from 'pinia';
import axios from 'axios';


interface AnalyticsSummary {
    totalSales: number;
    salesCount: number;
    totalTarget: number;
    percentageGoalAchieved: number;
}


interface DivisionPerformance {
    division: string;
    totalSales: number;
    target: number;
    performancePercentage: number;
}


interface Last12MonthsSales {
    month: string;
    totalSales: number;
}


interface SalesByProduct {
    productName: string;
    totalSales: number;
}


interface AnalyticsState {
    analytics: any; 
    summary: AnalyticsSummary;
    divisionPerformance: DivisionPerformance[];
    last12MonthsSales: Last12MonthsSales[];
    salesByProduct: SalesByProduct[]; 
}

export const useAnalyticsStore = defineStore('analytics', {
    state: (): AnalyticsState => ({
        analytics: {}, 
        summary: {
            totalSales: 0,
            salesCount: 0,
            totalTarget: 0,
            percentageGoalAchieved: 0,
        },
        divisionPerformance: [],
        last12MonthsSales: [],
        salesByProduct: [], 
    }),
    actions: {
        async fetchAnalytics() {
            
        },
        async fetchAnalyticsSummary() {
            try {
                const response = await axios.get('http://localhost:8080/api/analytics/summary');
                this.summary = response.data;
            } catch (error) {
                console.error('Error fetching analytics summary:', error);
                this.summary = {
                    totalSales: 0,
                    salesCount: 0,
                    totalTarget: 0,
                    percentageGoalAchieved: 0,
                };
                throw error;
            }
        },
        async fetchDivisionPerformance() {
            try {
                const response = await axios.get('http://localhost:8080/api/analytics/division-performance');
                this.divisionPerformance = response.data;
            } catch (error) {
                console.error('Error fetching division performance:', error);
                this.divisionPerformance = []; // Reset to empty array on error
                throw error; // Re-throw to allow component to catch
            }
        },
        async fetchLast12MonthsSales() {
            try {
                const response = await axios.get('http://localhost:8080/api/analytics/last-12-months-sales');
                this.last12MonthsSales = response.data;
            } catch (error) {
                console.error('Error fetching last 12 months sales:', error);
                this.last12MonthsSales = []; 
                throw error;
            }
        },
        async fetchSalesByProduct() { 
            try {
                const response = await axios.get('http://localhost:8080/api/analytics/sales-by-product');
                this.salesByProduct = response.data;
            } catch (error) {
                console.error('Error fetching sales by product:', error);
                this.salesByProduct = []; 
                throw error;
            }
        },
    },
});