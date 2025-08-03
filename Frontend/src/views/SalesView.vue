<template>
  <div class="sales-view">
    <div class="page-header">
      <h1 class="page-title">Sales Records</h1>
      <p class="page-subtitle">Track and manage sales transactions</p>
    </div>

    <DataTable :data="sales" :columns="columns" :currentPage="currentPage" :pageSize="pageSize"
      :totalElements="totalElements" :totalPages="totalPages" @add="openAddModal" @edit="editSale" @delete="deleteSale"
      @page-change="handlePageChange" @page-size-change="handlePageSizeChange" @search="handleSearch"
      @sort="handleSort">
      <template #cell-sales="{ value }">
        <span class="currency">${{ formatCurrency(value || 0) }}</span>
      </template>
      <template #cell-orderDate="{ value }">
        <span class="date">{{ formatDate(value) }}</span>
      </template>
      <template #cell-shipDate="{ value }">
        <span class="date">{{ formatDate(value) }}</span>
      </template>
    </DataTable>

    <div v-if="showAddModal || showEditModal" class="modal-overlay" @click="closeModals">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h2 class="modal-title">
            {{ showAddModal ? 'Add New Sale' : 'Edit Sale' }}
          </h2>
          <button @click="closeModals" class="close-button" type="button">✕</button>
        </div>

        <form @submit.prevent="submitForm" class="modal-form">
          <div class="form-row" v-if="showEditModal">
            <div class="form-group">
              <label for="salesId">Sale ID</label>
              <input v-model.number="formData.salesId" type="number" id="salesId" class="form-input" readonly />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label for="productName">Product Name</label>
              <select v-model="formData.productName" id="productName" required class="form-input">
                <option value="">Select Product</option>
                <option v-for="productItem in products" :key="productItem.id" :value="productItem.name">
                  {{ productItem.name }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label for="division">Division</label>
              <select v-model="formData.division" id="division" required class="form-input">
                <option value="">Select Division</option>
                <option v-for="divisionItem in divisions" :key="divisionItem.division" :value="divisionItem.division">
                  {{ divisionItem.division }}
                </option>
              </select>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="city">City</label>
              <input v-model="formData.city" type="text" id="city" required class="form-input"
                placeholder="Enter city" />
            </div>
            <div class="form-group">
              <label for="region">Region</label>
              <input v-model="formData.region" type="text" id="region" required class="form-input"
                placeholder="Enter region" />
            </div>
          </div>

          <div class="form-group">
            <label for="customerId">Customer ID</label>
            <input v-model="formData.customerId" type="text" id="customerId" required class="form-input"
              placeholder="Enter customer ID" />
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="units">Units</label>
              <input v-model.number="formData.units" type="number" id="units" required min="0" class="form-input" />
            </div>

            <div class="form-group">
              <label for="sales">Sales</label>
              <input v-model.number="formData.sales" type="number" id="sales" step="0.01" required class="form-input" />
            </div>

            <div class="form-group">
              <label for="cost">Cost</label>
              <input v-model.number="formData.cost" type="number" id="cost" step="0.01" required class="form-input" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="orderId">Order ID</label>
              <input v-model="formData.orderId" type="text" id="orderId" required class="form-input"
                placeholder="Enter order ID" />
            </div>
            <div class="form-group">
              <label for="orderDate">Order Date</label>
              <input v-model="formData.orderDate" type="date" id="orderDate" required class="form-input" />
            </div>
            <div class="form-group">
              <label for="shipDate">Ship Date</label>
              <input v-model="formData.shipDate" type="date" id="shipDate" required class="form-input" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="shipMode">Ship Mode</label>
              <select v-model="formData.shipMode" id="shipMode" required class="form-input">
                <option value="">Select Ship Mode</option>
                <option v-for="shipModeItem in shipmentModes" :key="shipModeItem.shipId" :value="shipModeItem.shipMode">
                  {{ shipModeItem.shipMode }}
                </option>
              </select>
            </div>
            <div class="form-group">
              <label for="countryRegion">Country/Region</label>
              <input v-model="formData.countryRegion" type="text" id="countryRegion" required class="form-input"
                placeholder="Enter country/region" />
            </div>
            <div class="form-group">
              <label for="stateProvince">State/Province</label>
              <input v-model="formData.stateProvince" type="text" id="stateProvince" required class="form-input"
                placeholder="Enter state/province" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="postalCode">Postal Code</label>
              <input v-model="formData.postalCode" type="text" id="postalCode" class="form-input"
                placeholder="Enter postal code" />
            </div>
            <div class="form-group">
              <label for="grossProfit">Gross Profit</label>
              <input v-model.number="formData.grossProfit" type="number" id="grossProfit" step="0.01" required
                class="form-input" />
            </div>
          </div>

          <div class="form-actions">
            <button type="button" @click="closeModals" class="cancel-button">
              Cancel
            </button>
            <button type="submit" class="submit-button" :disabled="isSubmitting">
              {{ isSubmitting ? 'Saving...' : (showAddModal ? 'Add Sale' : 'Update Sale') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue';
import { candySalesApi } from '../api/sales';
import { targetsApi } from '../api/targets'; // Changed import
import { shipmentModeService } from '../api/shipmentMode';
import { productsApi } from '../api/products';
import DataTable from '../components/UI/DataTable.vue';
import type { CandySale, Targets, ShipmentMode, CandyProduct } from '../types'; // Changed import

// Column type definition
type Column = {
  key: string;
  label: string;
  type?: 'text' | 'number' | 'currency' | 'date';
  sortable?: boolean;
};

// Reactive data
const sales = ref<CandySale[]>([]);
const divisions = ref<Targets[]>([]); // Changed type to Targets[]
const shipmentModes = ref<ShipmentMode[]>([]);
const products = ref<CandyProduct[]>([]);
const isSubmitting = ref(false);

// Pagination state
const currentPage = ref(0);
const pageSize = ref(10);
const totalElements = ref(0);
const totalPages = ref(0);

// Sorting and Searching State
const currentSortKey = ref('');
const currentSortOrder = ref<'asc' | 'desc'>('asc');
const currentSearchTerm = ref('');

// Table columns configuration
const columns: Column[] = [
  { key: 'salesId', label: 'Sale ID', type: 'number', sortable: true },
  { key: 'productName', label: 'Product', type: 'text', sortable: true },
  { key: 'division', label: 'Division', type: 'text', sortable: true },
  { key: 'city', label: 'City', type: 'text', sortable: true },
  { key: 'region', label: 'Region', type: 'text', sortable: true },
  { key: 'customerId', label: 'Customer ID', type: 'text', sortable: true },
  { key: 'units', label: 'Units', type: 'number', sortable: true },
  { key: 'sales', label: 'Sales', type: 'currency', sortable: true },
  { key: 'cost', label: 'Cost', type: 'currency', sortable: true },
  { key: 'orderId', label: 'Order ID', type: 'text', sortable: true },
  { key: 'orderDate', label: 'Order Date', type: 'date', sortable: true },
  { key: 'shipDate', label: 'Ship Date', type: 'date', sortable: true },
  { key: 'shipMode', label: 'Ship Mode', type: 'text', sortable: true },
  { key: 'countryRegion', label: 'Country/Region', type: 'text', sortable: true },
  { key: 'stateProvince', label: 'State/Province', type: 'text', sortable: true },
  { key: 'postalCode', label: 'Postal Code', type: 'text', sortable: true },
  { key: 'productId', label: 'Product ID', type: 'text', sortable: true },
  { key: 'grossProfit', label: 'Gross Profit', type: 'currency', sortable: true },
];

// Modal state
const showAddModal = ref(false);
const showEditModal = ref(false);
const editingSale = ref<CandySale | null>(null);

// Form data interface matching CandySale
interface SaleFormData {
  salesId?: number;
  city: string;
  division: string;
  region: string;
  sales: number;
  units: number;
  cost: number;
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

const formData = reactive<SaleFormData>({
  salesId: undefined,
  city: '',
  division: '',
  region: '',
  sales: 0,
  units: 0,
  cost: 0,
  orderId: '',
  orderDate: new Date().toISOString().split('T')[0],
  shipDate: new Date().toISOString().split('T')[0],
  shipMode: '',
  customerId: '',
  countryRegion: '',
  stateProvince: '',
  postalCode: '',
  productId: '',
  productName: '',
  grossProfit: 0,
});

// Computed properties
const isFormValid = computed(() => {
  return formData.city.trim() !== '' &&
    formData.division.trim() !== '' &&
    formData.region.trim() !== '' &&
    formData.sales >= 0 &&
    formData.units >= 0 &&
    formData.cost >= 0 &&
    formData.orderId.trim() !== '' &&
    formData.orderDate.trim() !== '' &&
    formData.shipDate.trim() !== '' &&
    formData.shipMode.trim() !== '' &&
    formData.customerId.trim() !== '' &&
    formData.countryRegion.trim() !== '' &&
    formData.stateProvince.trim() !== '' &&
    formData.productName.trim() !== '' &&
    formData.grossProfit >= 0;
});

// Helper functions
function formatCurrency(value: number): string {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(value);
}

function formatDate(dateString: string): string {
  if (!dateString) return '';
  const date = new Date(dateString);
  if (isNaN(date.getTime())) return '';
  return new Intl.DateTimeFormat('en-US', { year: 'numeric', month: 'short', day: 'numeric' }).format(date);
}

function resetForm(): void {
  Object.assign(formData, {
    salesId: undefined,
    city: '',
    division: '',
    region: '',
    sales: 0,
    units: 0,
    cost: 0,
    orderId: '',
    orderDate: new Date().toISOString().split('T')[0],
    shipDate: new Date().toISOString().split('T')[0],
    shipMode: '',
    customerId: '',
    countryRegion: '',
    stateProvince: '',
    postalCode: '',
    productId: '',
    productName: '',
    grossProfit: 0,
  });
}

function closeModals(): void {
  showAddModal.value = false;
  showEditModal.value = false;
  editingSale.value = null;
  resetForm();
}

function openAddModal(): void {
  resetForm();
  showAddModal.value = true;
}

function editSale(sale: CandySale): void {
  if (!sale.salesId) {
    console.error('Sale ID is required for editing');
    return;
  }

  editingSale.value = sale;
  Object.assign(formData, {
    salesId: sale.salesId,
    city: sale.city,
    division: sale.division,
    region: sale.region,
    sales: sale.sales,
    units: sale.units,
    cost: sale.cost,
    orderId: sale.orderId,
    orderDate: sale.orderDate,
    shipDate: sale.shipDate,
    shipMode: sale.shipMode,
    customerId: sale.customerId,
    countryRegion: sale.countryRegion,
    stateProvince: sale.stateProvince,
    postalCode: sale.postalCode,
    productId: sale.productId,
    productName: sale.productName,
    grossProfit: sale.grossProfit,
  });
  showEditModal.value = true;
}

async function deleteSale(sale: CandySale): Promise<void> {
  if (!sale.salesId) {
    console.error('Sale ID is required for deletion');
    return;
  }

  const confirmed = confirm(`Are you sure you want to delete sale record with ID "${sale.salesId}"?`);
  if (!confirmed) return;

  try {
    await candySalesApi.deleteCandySale(sale.salesId);
    loadData();
  } catch (error) {
    console.error('Delete error:', error);
    alert('Failed to delete sale record. Please try again.');
  }
}

async function submitForm(): Promise<void> {
  if (!isFormValid.value || isSubmitting.value) {
    return;
  }

  isSubmitting.value = true;

  try {
    // Before submitting, find the corresponding productId based on the selected productName
    const selectedProduct = products.value.find(p => p.name === formData.productName);
    if (selectedProduct) {
      formData.productId = selectedProduct.id;
    } else {
      console.error('Selected product name does not have a matching ID.');
      alert('Selected product is invalid. Please choose from the list.');
      isSubmitting.value = false;
      return;
    }

    if (showAddModal.value) {
      const payload = { ...formData };
      delete payload.salesId;
      await candySalesApi.createCandySale(payload as Omit<CandySale, 'salesId'>);
    } else if (editingSale.value?.salesId) {
      await candySalesApi.updateCandySale(
        editingSale.value.salesId,
        formData as CandySale
      );
    }
    closeModals();
    loadData();
  } catch (error) {
    console.error('Save error:', error);
    alert('Failed to save sale record. Please try again.');
  } finally {
    isSubmitting.value = false;
  }
}

async function loadData(): Promise<void> {
  try {
    const [salesResponse, divisionsResponse, shipmentModesResponse, productsResponse] = await Promise.all([
      candySalesApi.getCandySales(
        currentPage.value,
        pageSize.value,
        currentSearchTerm.value,
        currentSortKey.value,
        currentSortOrder.value
      ),
      targetsApi.getAllTargets(), // Changed API call
      shipmentModeService.getShipmentModes(),
      productsApi.getProducts(0, 1000)
    ]);

    sales.value = salesResponse.content || [];
    totalElements.value = salesResponse.totalElements;
    totalPages.value = salesResponse.totalPages;
    divisions.value = divisionsResponse.content || []; // Adjusted assignment to get content array
    shipmentModes.value = shipmentModesResponse || [];
    products.value = productsResponse.content || [];
  } catch (error) {
    console.error('Failed to load data:', error);
    alert('Failed to load sales data. Please refresh the page.');
  }
}

function handlePageChange(page: number): void {
  currentPage.value = page;
  loadData();
}

function handlePageSizeChange(size: number): void {
  pageSize.value = size;
  currentPage.value = 0;
  loadData();
}

function handleSearch(searchTerm: string): void {
  currentSearchTerm.value = searchTerm;
  currentPage.value = 0;
  loadData();
}

function handleSort(key: string): void {
  if (currentSortKey.value === key) {
    currentSortOrder.value = currentSortOrder.value === 'asc' ? 'desc' : 'asc';
  } else {
    currentSortKey.value = key;
    currentSortOrder.value = 'asc';
  }
  loadData();
}

// Lifecycle
onMounted(() => {
  loadData();
});
</script>

<style scoped>
.sales-view {
  max-width: 1400px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  margin-bottom: 2rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1f2937;
  margin: 0 0 0.5rem 0;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #6b7280;
  margin: 0;
}

.currency {
  font-weight: 600;
  color: #059669;
}

.date {
  font-weight: 500;
  color: #6b7280;
}


/* Modal styles - reuse from previous components */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 25px rgba(0, 0, 0, 0.2);
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  margin: 1rem;
}

.modal-header {
  padding: 1.5rem;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #6b7280;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.close-button:hover {
  background: #f3f4f6;
}

.modal-form {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
}

label {
  display: block;
  font-weight: 500;
  color: #374151;
  margin-bottom: 0.5rem;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.9rem;
  transition: border-color 0.2s ease;
}

.form-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-input[readonly] {
  background: #f9fafb;
  color: #6b7280;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid #e5e7eb;
}

.cancel-button {
  padding: 0.75rem 1.5rem;
  border: 1px solid #d1d5db;
  background: white;
  color: #374151;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-button:hover {
  background: #f9fafb;
  border-color: #9ca3af;
}

.submit-button {
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #3b82f6, #1d4ed8);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.submit-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.submit-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 768px) {
  .sales-view {
    padding: 1rem;
  }

  .modal {
    margin: 0.5rem;
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>