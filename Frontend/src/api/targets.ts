import axios from 'axios';
import type { Targets } from '../types';

interface PaginatedTargetResponse {
  content: Targets[];
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

export const targetsApi = {
  // Get targets with full pagination, search, and sort support
  async getTargets(
    page: number = 0, 
    size: number = 10, 
    searchTerm: string = '', 
    sortKey: string = '', 
    sortOrder: 'asc' | 'desc' = 'asc'
  ): Promise<PaginatedTargetResponse> {
    try {
      // Build query parameters
      const params = new URLSearchParams();
      params.append('page', page.toString());
      params.append('size', size.toString());
      
      if (searchTerm.trim()) {
        params.append('division', searchTerm.trim());
      }
      
      if (sortKey.trim()) {
        params.append('sort', `${sortKey},${sortOrder}`);
      }
      
      console.log(`Fetching targets: page=${page}, size=${size}, search=${searchTerm}, sort=${sortKey},${sortOrder}`);
      const response = await api.get(`/targets?${params.toString()}`);
      console.log('Targets response:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error fetching targets:', error);
      throw error;
    }
  },

  // Get all targets without pagination (for dropdowns, etc.)
  async getAllTargets(): Promise<PaginatedTargetResponse> {
    try {
      console.log(`Fetching ALL targets`);
      const response = await api.get('/targets');
      console.log('Targets response:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error fetching targets:', error);
      throw error;
    }
  },

  // Get single target by division
  async getTarget(division: string): Promise<Targets> {
    try {
      const response = await api.get(`/targets/${division}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching target:', error);
      throw error;
    }
  },

  // Create new target
  async createTarget(target: Targets): Promise<Targets> {
    try {
      const response = await api.post('/targets', target);
      return response.data;
    } catch (error) {
      console.error('Error creating target:', error);
      throw error;
    }
  },

  // Update existing target
  async updateTarget(division: string, target: Targets): Promise<Targets> {
    try {
      const response = await api.put(`/targets/${division}`, target);
      return response.data;
    } catch (error) {
      console.error('Error updating target:', error);
      throw error;
    }
  },

  // Delete target
  async deleteTarget(division: string): Promise<void> {
    try {
      await api.delete(`/targets/${division}`);
    } catch (error) {
      console.error('Error deleting target:', error);
      throw error;
    }
  },
};