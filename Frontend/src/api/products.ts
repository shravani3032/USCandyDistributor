import type { CandyProduct } from '../types/index';
import axios from 'axios';


interface PaginatedProductsResponse {
  content: CandyProduct[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  
}

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

export const productsApi = {

  async getProducts(
    page: number = 0, 
    size: number = 10, 
    searchTerm: string = '', 
    sortKey: string = '', 
    sortOrder: 'asc' | 'desc' = 'asc'
  ): Promise<PaginatedProductsResponse> {
    try {
      let url = `products?page=${page}&size=${size}`;
      if (searchTerm) {
        url += `&name=${searchTerm}`; 
      }
      if (sortKey) {
        url += `&sort=${sortKey},${sortOrder}`; 
      }
      console.log(`Fetching products: ${url}`);
      const response = await api.get(url);
      console.log('Products response:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error fetching products:', error);
      throw error;
    }
  },


  async getProduct(id: String): Promise<CandyProduct> {
    try {
      const response = await api.get(`products/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching product:', error);
      throw error;
    }
  },

  async checkProductIdExists(id: string): Promise<boolean> {
  try {
    const response = await api.get(`products/check-id`, {
      params: { id }
    });
    return response.data.exists === true;
  } catch (error) {
    console.error('Error checking product ID existence:', error);
    return false; 
  }
},


  async createProduct(product: Omit<CandyProduct, 'id'>): Promise<CandyProduct> {
    try {
      const response = await api.post('products', product);
      return response.data;
    } catch (error) {
      console.error('Error creating product:', error);
      throw error;
    }
  },


  async updateProduct(id: string, product: Omit<CandyProduct, 'id'>): Promise<CandyProduct> {
    try {
      const productWithId = { ...product, id }; 
      const response = await api.put(`products/${id}`, productWithId);
      return response.data;
    } catch (error) {
      console.error('Error updating product:', error);
      throw error;
    }
  },


  async deleteProduct(id: string): Promise<void> {
    try {
      await api.delete(`products/${id}`);
    } catch (error) {
      console.error('Error deleting product:', error);
      throw error;
    }
  },
};