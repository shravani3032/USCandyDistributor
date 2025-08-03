import axios from 'axios';
import type { CandySale } from '../types';

interface PaginatedCandySaleResponse {
  content: CandySale[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  // Add any other pagination properties your API returns
}

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

export const candySalesApi = {
  // Get candy sales with full pagination, search, and sort support
  async getCandySales(
    page: number = 0, 
    size: number = 10, 
    searchTerm: string = '', 
    sortKey: string = '', 
    sortOrder: 'asc' | 'desc' = 'asc'
  ): Promise<PaginatedCandySaleResponse> {
    try {
      // Build query parameters
      const params = new URLSearchParams();
      params.append('page', page.toString());
      params.append('size', size.toString());
      
      if (searchTerm.trim()) {
        // Assuming 'productName' or 'city' is a common search field for CandySale
        params.append('search', searchTerm.trim()); 
      }
      
      if (sortKey.trim()) {
        params.append('sort', `${sortKey},${sortOrder}`);
      }
      
      console.log(`Fetching candy sales: page=${page}, size=${size}, search=${searchTerm}, sort=${sortKey},${sortOrder}`);
      const response = await api.get(`/sales?${params.toString()}`);
      console.log('Candy sales response:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error fetching candy sales:', error);
      throw error;
    }
  },

  // Get all candy sales without pagination (for dropdowns, etc.)
  async getAllCandySales(): Promise<PaginatedCandySaleResponse> {
    try {
      console.log(`Fetching ALL candy sales`);
      const response = await api.get('/sales');
      console.log('Candy sales response:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error fetching candy sales:', error);
      throw error;
    }
  },

  // Get single candy sale by ID
  async getCandySale(id: number): Promise<CandySale> { // Changed id type to number
    try {
      const response = await api.get(`/sales/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching candy sale:', error);
      throw error;
    }
  },

  // Create new candy sale
  async createCandySale(candySale: Omit<CandySale, 'salesId'>): Promise<CandySale> {
    try {
      const response = await api.post('/sales', candySale);
      return response.data;
    } catch (error) {
      console.error('Error creating candy sale:', error);
      throw error;
    }
  },

  // Update existing candy sale
  async updateCandySale(id: number, candySale: Omit<CandySale, 'salesId'>): Promise<CandySale> { // Changed id type to number
    try {
      const response = await api.put(`/sales/${id}`, candySale);
      return response.data;
    } catch (error) {
      console.error('Error updating candy sale:', error);
      throw error;
    }
  },

  // Delete candy sale
  async deleteCandySale(id: number): Promise<void> { // Changed id type to number
    try {
      await api.delete(`/sales/${id}`);
    } catch (error) {
      console.error('Error deleting candy sale:', error);
      throw error;
    }
  },
};