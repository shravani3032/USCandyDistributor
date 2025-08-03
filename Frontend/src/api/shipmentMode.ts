// services/shipmentModeService.ts
import axios from 'axios';
import type { ShipmentMode } from '../types'; // Assuming ShipmentMode is defined in types.ts

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

export const shipmentModeService = {
  // Get all shipment modes
  async getShipmentModes(): Promise<ShipmentMode[]> {
    try {
      console.log(`Fetching shipment modes:`);
      const response = await api.get<ShipmentMode[]>('shipmentModes');
      console.log('Shipment modes response:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error fetching shipment modes:', error);
      throw error;
    }
  },

  // Get a single shipment mode by ID
  async getShipmentMode(id: number): Promise<ShipmentMode> {
    try {
      const response = await api.get<ShipmentMode>(`shipmentModes/${id}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching shipment mode:', error);
      throw error;
    }
  },

  // Create a new shipment mode
  async createShipmentMode(shipmentMode: Omit<ShipmentMode, 'shipId'>): Promise<ShipmentMode> {
    try {
      const response = await api.post<ShipmentMode>('shipmentModes', shipmentMode);
      return response.data;
    } catch (error) {
      console.error('Error creating shipment mode:', error);
      throw error;
    }
  },

  // Update an existing shipment mode
  async updateShipmentMode(id: number, shipmentMode: Omit<ShipmentMode, 'shipId'>): Promise<ShipmentMode> {
    try {
      const response = await api.put<ShipmentMode>(`shipmentModes/${id}`, shipmentMode);
      return response.data;
    } catch (error) {
      console.error('Error updating shipment mode:', error);
      throw error;
    }
  },

  // Delete a shipment mode by ID
  async deleteShipmentMode(id: number): Promise<void> {
    try {
      await api.delete(`shipmentModes/${id}`);
    } catch (error) {
      console.error('Error deleting shipment mode:', error);
      throw error;
    }
  },
};