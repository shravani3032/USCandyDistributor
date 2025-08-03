<template>
  <div class="products-view">
    <div class="page-header">
      <h1 class="page-title">Product Catalog</h1>
      <p class="page-subtitle">Browse and manage candy products</p>
    </div>

 
    <DataTable
      :data="products"
      :columns="columns"
      :currentPage="currentPage"
      :pageSize="pageSize"
      :totalElements="totalElements"
      :totalPages="totalPages"
      @add="openAddModal"
      @edit="editProduct"
      @delete="deleteProduct"
      @page-change="handlePageChange"
      @page-size-change="handlePageSizeChange"
      @search="handleSearch"
      @sort="handleSort"
    >
      <template #cell-unitPrice="{ value }">
        <span class="currency">${{ formatCurrency(value) }}</span>
      </template>
      <template #cell-unitCost="{ value }">
        <span class="cost">${{ formatCurrency(value) }}</span>
      </template>
    </DataTable>


    <VDialog
      v-model:open="isDialogOpen"
      modal
      :headline="showAddModal ? 'Add New Product' : 'Edit Product'"
      class="mydialog"
    >
      <template #body>
        <form @submit.prevent="submitForm">
          <VLayout direction="column" gap="300" padding="400">
            <VTextField
              v-model="formData.id"
              label="Product ID"
              placeholder="Enter product ID"
              maxlength="20"
              char-count
              required
              :disabled="showEditModal"
              :error-text="getProductIdErrorText"
              :success-text="productIdExists === false ? 'Product ID is available' : ''"
            />

            <VTextField
              v-model="formData.name"
              label="Product Name"
              placeholder="Enter product name"
              maxlength="94"
              char-count
              required
              :error-text="getProductNameErrorText"
              :success-text="productNameExists === false ? 'Product name is available' : ''"
            />

            <VLayout direction="row" gap="300">
              <VSelect
                v-model="formData.division"
                label="Division"
                placeholder="Select Division"
                required
              >
                <VOption
                  v-for="item in targets"
                  :key="item.division"
                  :value="item.division"
                  :text="item.division"
                />
              </VSelect>

              <VSelect
                v-model="formData.factory"
                label="Factory"
                placeholder="Select Factory"
                required
              >
                <VOption
                  v-for="item in factories"
                  :key="item.factory"
                  :value="item.factory"
                  :text="item.factory"
                />
              </VSelect>
            </VLayout>

            <VLayout direction="row" gap="300">
              <VNumberField
                v-model.number="formData.unitPrice"
                label="Unit Price ($)"
                min="0"
                step="0.01"
                placeholder="0.00"
                required
              />
              <VNumberField
                v-model.number="formData.unitCost"
                label="Unit Cost ($)"
                min="0"
                step="0.01"
                placeholder="0.00"
                required
              />
            </VLayout>
          </VLayout>
        </form>
      </template>

      <template #action-items>
        <VLayout justify="end" gap="200">
          <VButton
            appearance="outlined"
            label="Cancel"
            @click="closeModals"
          />
          <VButton
            appearance="filled"
            type="submit"
            :label="isSubmitting ? 'Saving...' : (showAddModal ? 'Add Product' : 'Update Product')"
            :disabled="isSubmitting || !isFormValid"
            @click="submitForm"
          />
        </VLayout>
      </template>
    </VDialog>
  </div>
</template>


<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue';
import debounce from 'lodash.debounce';
import { productsApi } from '../api/products';
import { factoriesApi } from '../api/factories';
import { targetsApi } from '../api/targets';
import DataTable from '../components/UI/DataTable.vue';
import type { CandyProduct, CandyFactory, Targets } from '../types';
import {
  VDialog,
  VTextField,
  VNumberField,
  VSelect,
  VOption,
  VLayout,
  VButton
} from '@vonage/vivid-vue';

// Table columns
const columns = [
  { key: 'id', label: 'Product ID', type: 'text', sortable: true },
  { key: 'name', label: 'Product Name', type: 'text', sortable: true },
  { key: 'division', label: 'Division', type: 'text', sortable: true },
  { key: 'factory', label: 'Factory', type: 'text', sortable: true },
  { key: 'unitPrice', label: 'Unit Price', type: 'currency', sortable: true },
  { key: 'unitCost', label: 'Unit Cost', type: 'currency', sortable: true }
];


const products = ref<CandyProduct[]>([]);
const factories = ref<CandyFactory[]>([]);
const targets = ref<Targets[]>([]);
const isSubmitting = ref(false);


const currentPage = ref(0);
const pageSize = ref(10);
const totalElements = ref(0);
const totalPages = ref(0);


const currentSortKey = ref('');
const currentSortOrder = ref<'asc' | 'desc'>('asc');
const currentSearchTerm = ref('');


const showAddModal = ref(false);
const showEditModal = ref(false);
const dialogMode = computed(() => showAddModal.value ? 'add' : 'edit');
const isDialogOpen = computed({
  get: () => showAddModal.value || showEditModal.value,
  set: val => { if (!val) closeModals(); }
});

const editingProduct = ref<CandyProduct | null>(null);

interface ProductFormData {
  id: string;
  name: string;
  division: string;
  factory: string;
  unitPrice: number;
  unitCost: number;
}

const formData = reactive<ProductFormData>({
  id: '',
  name: '',
  division: '',
  factory: '',
  unitPrice: 0,
  unitCost: 0
});


const productIdExists = ref<boolean | null>(null);
const productNameExists = ref<boolean | null>(null);


const isFormValid = computed(() => {
  return (
    formData.id.trim().length >= 3 &&
    formData.name.trim().length >= 3 &&
    formData.division !== '' &&
    formData.factory !== '' &&
    formData.unitPrice >= 0 &&
    formData.unitCost >= 0 &&
    (dialogMode.value === 'edit' || productIdExists.value !== true) &&
    productNameExists.value !== true
  );
});

const getProductIdErrorText = computed(() => {
  const id = formData.id.trim();
  if (id.length === 0) return 'Product ID is required';
  if (id.length < 3) return 'Must be at least 3 characters';
  if (productIdExists.value === true) return 'This Product ID already exists';
  return '';
});

const getProductNameErrorText = computed(() => {
  const name = formData.name.trim();
  if (name.length === 0) return 'Product name is required';
  if (name.length < 3) return 'Must be at least 3 characters';
  if (productNameExists.value === true) return 'This Product Name already exists';
  return '';
});


watch(
  () => formData.id,
  debounce(async (newId: string) => {
    if (dialogMode.value !== 'add' || newId.trim().length < 3) {
      productIdExists.value = null;
      return;
    }
    try {
      productIdExists.value = await productsApi.checkProductIdExists(newId.trim());
    } catch {
      productIdExists.value = null;
    }
  }, 400)
);

watch(
  () => formData.name,
  debounce(async (newName: string) => {
    if (dialogMode.value !== 'add' || newName.trim().length < 3) {
      productNameExists.value = null;
      return;
    }
    try {
      const response = await productsApi.getProducts(0, 5, newName.trim(), 'name', 'asc');
      productNameExists.value = response.content?.some(
        p => p.name.toLowerCase() === newName.trim().toLowerCase()
      ) ?? false;
    } catch {
      productNameExists.value = null;
    }
  }, 400)
);


function resetForm() {
  Object.assign(formData, {
    id: '',
    name: '',
    division: '',
    factory: '',
    unitPrice: 0,
    unitCost: 0
  });
  productIdExists.value = null;
  productNameExists.value = null;
}

function openAddModal() {
  resetForm();
  showAddModal.value = true;
}

function editProduct(product: CandyProduct) {
  editingProduct.value = product;
  Object.assign(formData, product);
  showEditModal.value = true;
}

function closeModals() {
  showAddModal.value = false;
  showEditModal.value = false;
  editingProduct.value = null;
  resetForm();
}


async function submitForm() {
  if (!isFormValid.value || isSubmitting.value) return;

  if (dialogMode.value === 'add' && productIdExists.value === true) {
    alert('Cannot add: Product ID already exists.');
    return;
  }

  isSubmitting.value = true;
  try {
    if (dialogMode.value === 'add') {
      await productsApi.createProduct(formData as CandyProduct);
    } else if (editingProduct.value?.id) {
      await productsApi.updateProduct(editingProduct.value.id, formData);
    }
    closeModals();
    loadData();
  } catch (error) {
    console.error('Submit error:', error);
    alert('Failed to save product. Please try again.');
  } finally {
    isSubmitting.value = false;
  }
}


async function deleteProduct(product: CandyProduct) {
  if (!product.id) return;
  const confirmDelete = confirm(`Are you sure you want to delete "${product.name}"?`);
  if (!confirmDelete) return;
  try {
    await productsApi.deleteProduct(product.id);
    loadData();
  } catch (error) {
    console.error('Delete error:', error);
    alert('Failed to delete product.');
  }
}


async function loadData() {
  try {
    const [productsRes, factoriesRes, targetsRes] = await Promise.all([
      productsApi.getProducts(currentPage.value, pageSize.value, currentSearchTerm.value, currentSortKey.value, currentSortOrder.value),
      factoriesApi.getAllFactories(),
      targetsApi.getAllTargets()
    ]);
    products.value = productsRes.content || [];
    totalElements.value = productsRes.totalElements;
    totalPages.value = productsRes.totalPages;
    factories.value = factoriesRes.content || [];
    targets.value = targetsRes.content || [];
  } catch (error) {
    console.error('Load error:', error);
    alert('Failed to load data.');
  }
}


function handlePageChange(page: number) {
  currentPage.value = page;
  loadData();
}

function handlePageSizeChange(size: number) {
  pageSize.value = size;
  currentPage.value = 0;
  loadData();
}

function handleSearch(term: string) {
  currentSearchTerm.value = term;
  currentPage.value = 0;
  loadData();
}

function handleSort(key: string) {
  if (currentSortKey.value === key) {
    currentSortOrder.value = currentSortOrder.value === 'asc' ? 'desc' : 'asc';
  } else {
    currentSortKey.value = key;
    currentSortOrder.value = 'asc';
  }
  loadData();
}


function formatCurrency(value: number): string {
  return new Intl.NumberFormat('en-US', {
    style: 'decimal',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(value);
}

onMounted(() => loadData());
</script>


<style scoped>
/* Keep existing styles */
.products-view {
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

.cost {
  font-weight: 600;
  color: #ef4444;
}

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
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
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
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-input:disabled {
  background-color: #f9fafb;
  color: #6b7280;
  cursor: not-allowed;
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
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 768px) {
  .products-view {
    padding: 1rem;
  }

  .modal {
    margin: 0.5rem;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;
  }
}
</style>