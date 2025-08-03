export interface CandyFactory {
  factory: string;
  latitude: number;
  longitude: number;
}

export interface CandyProduct {
  id: string;
  name: string;
  division: string;
  unitPrice: number;
  unitCost: number;
  factory: string;
}

export interface CandySale {
  city: string;
  division: string;
  region: string;
  sales: number;
  units: number;
  cost: number;
  salesId: number;
  orderId: string;
  orderDate: string;
  shipDate: string;
  shipMode: string;
  customerId: string;
  countryRegion: string;
  stateProvince: string;
  postalCode: string;
  productId: string;
  productName: string;
  grossProfit: number;
}

export interface CandyTarget {
  id: number;
  divisionName: string;
  targetAmount: number;
  period: string;
  year: number;
}
export interface Targets {
  division: string;
  target: number;
}

export interface ShipmentMode {
  shipId: number;
  shipMode: string;
}

export interface USZip {
  zip: string;
  city: string;
  state: string;
  latitude: number;
  longitude: number;
}

export interface DashboardSummary {
  totalSales: number;
  salesCount: number;
  totalTarget: number;
  percentageGoalAchieved: number;
}

