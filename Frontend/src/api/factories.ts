import axios from 'axios';
import type { CandyFactory } from '../types';

interface PaginatedFactoryResponse {
  content: CandyFactory[];
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

export const factoriesApi = {
  // Get factories with full pagination, search, and sort support
  async getFactories(
    page: number = 0, 
    size: number = 10, 
    searchTerm: string = '', 
    sortKey: string = '', 
    sortOrder: 'asc' | 'desc' = 'asc'
  ): Promise<PaginatedFactoryResponse> {
    try {
      // Build query parameters
      //Creates an object that helps us build query parameters for a URL
      const params = new URLSearchParams();
      params.append('page', page.toString());
      params.append('size', size.toString());
      
      //GET /factories?factory=Choco
      if (searchTerm.trim()) {
        params.append('factory', searchTerm.trim());
      }
      
      //If a sort key is given (e.g., "latitude"), add it along with the order ("asc" or "desc").
      //sort=latitude,desc
      if (sortKey.trim()) {
        params.append('sort', `${sortKey},${sortOrder}`);
      }
      
      console.log(`Fetching factories: page=${page}, size=${size}, search=${searchTerm}, sort=${sortKey},${sortOrder}`);
      //http://localhost:8080/api/factories?page=0&size=10&factory=Choco&sort=latitude,asc
      const response = await api.get(`/factories?${params.toString()}`);
      console.log('Factories response:', response.data);
      //Sends the fetched data (list of factories + pagination info) back to the Vue component.
      return response.data;
    } catch (error) {
      console.error('Error fetching factories:', error);
      throw error;
    }
  },

  // Get all factories without pagination (for dropdowns, etc.)
  async getAllFactories(): Promise<PaginatedFactoryResponse> {
    try {
      console.log(`Fetching ALL factories`);
      const response = await api.get('/factories');
      console.log('Factories response:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error fetching factories:', error);
      throw error;
    }
  },

  // Get single factory by ID
  async getFactory(id: string): Promise<CandyFactory> {
    try {
      const response = await api.get(`/factories/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching factory:', error);
      throw error;
    }
  },

  // Create new factory
  async createFactory(factory: Omit<CandyFactory, 'id'>): Promise<CandyFactory> {
    try {
      const response = await api.post('/factories', factory);
      return response.data;
    } catch (error) {
      console.error('Error creating factory:', error);
      throw error;
    }
  },

  // Update existing factory
  async updateFactory(id: string, factory: Omit<CandyFactory, 'id'>): Promise<CandyFactory> {
    try {
      const response = await api.put(`/factories/${id}`, factory);
      return response.data;
    } catch (error) {
      console.error('Error updating factory:', error);
      throw error;
    }
  },

  // Delete factory
  async deleteFactory(id: string): Promise<void> {
    try {
      await api.delete(`/factories/${id}`);
    } catch (error) {
      console.error('Error deleting factory:', error);
      throw error;
    }
  },
};