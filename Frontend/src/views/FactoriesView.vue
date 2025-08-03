<template>
  <div class="factories-view">
    <div class="page-header">
      <h1 class="page-title">Candy Factories</h1>
      <p class="page-subtitle">Manage factory locations and information</p>
    </div>

    <DataTable
      :data="factories"
      :columns="columns"
      :currentPage="currentPage"
      :pageSize="pageSize"
      :totalElements="totalElements"
      :totalPages="totalPages"
      @add="openDialog('add')"
      @edit="openDialog('edit', $event)"
      @delete="deleteFactory"
      @page-change="handlePageChange"
      @page-size-change="handlePageSizeChange"
      @search="handleSearch"
      @sort="handleSort"
    >
      <template #cell-totalSales="{ value }">
        <span class="currency">${{ formatCurrency(value || 0) }}</span>
      </template>
      <template #cell-productsCount="{ value }">
        <span class="badge">{{ value || 0 }} products</span>
      </template>
      <template #cell-latitude="{ value }">
        <span class="coordinate">{{ formatCoordinate(value) }}</span>
      </template>
      <template #cell-longitude="{ value }">
        <span class="coordinate">{{ formatCoordinate(value) }}</span>
      </template>
    </DataTable>

    <VDialog
      :open="showDialog"
      :headline="dialogMode === 'add' ? 'Add New Factory' : 'Edit Factory'"
      class="mydialog"
      modal
      @close="closeDialog"
    >
      <template #body>
        <form @submit.prevent="submitForm">
          <VLayout direction="column" gutters="large-block" class="dialog-body">
            <VLayout direction="column" column-basis="block" gap="medium">
              <VTextField
                v-model="formData.factory"
                label="Factory Name"
                placeholder="Enter factory name"
                maxlength="94"
                char-count
                :disabled="dialogMode === 'edit'"
                required
                :error-text="factoryExists === true ? 'This factory already exists' : ''"
                :success-text="factoryExists === false ? 'Factory is available' : ''"
              />

              <VLayout gap="medium">
                <VNumberField
                  v-model.number="formData.latitude"
                  label="Latitude"
                  placeholder="0.00"
                  :min="-90"
                  :max="90"
                  :step="0.01"
                  required
                />
                <VNumberField
                  v-model.number="formData.longitude"
                  label="Longitude"
                  placeholder="0.00"
                  :min="-180"
                  :max="180"
                  :step="0.01"
                  required
                />
              </VLayout>
            </VLayout>
          </VLayout>
        </form>
      </template>

      <template #action-items>
        <VLayout justify="end" gap="200">
          <VButton appearance="outlined" label="Cancel" @click="closeDialog" />
          <VButton
            appearance="filled"
            type="submit"
            :label="isSubmitting ? 'Saving...' : (dialogMode === 'add' ? 'Add Factory' : 'Update Factory')"
            :disabled="isSubmitting || !isFormValid || (dialogMode === 'add' && factoryExists === true)"
            @click="submitForm"
          />
        </VLayout>
      </template>
    </VDialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed, watch } from 'vue';
import debounce from 'lodash.debounce';
import { factoriesApi } from '../api/factories';
import DataTable from '../components/UI/DataTable.vue';
import type { CandyFactory } from '../types';
import {
  VDialog,
  VButton,
  VTextField,
  VNumberField,
  VLayout,
  VSelect,
  VOption,
} from '@vonage/vivid-vue';

type Column = {
  key: string;
  label: string;
  type?: 'text' | 'number' | 'currency' | 'date';
  sortable?: boolean;
};

interface FactoryFormData {
  factory: string;
  latitude: number;
  longitude: number;
}

const factories = ref<CandyFactory[]>([]);
const isSubmitting = ref(false);
const factoryExists = ref<boolean | null>(null);

const currentPage = ref(0);
const pageSize = ref(10);
const totalElements = ref(0);
const totalPages = ref(0);

const currentSortKey = ref('');
const currentSortOrder = ref<'asc' | 'desc'>('asc');
const currentSearchTerm = ref('');

const showDialog = ref(false);
const dialogMode = ref<'add' | 'edit'>('add');
const editingFactory = ref<CandyFactory | null>(null);

const formData = reactive<FactoryFormData>({
  factory: '',
  latitude: 0,
  longitude: 0,
});

const columns: Column[] = [
  { key: 'factory', label: 'Factory Name', type: 'text', sortable: true },
  { key: 'latitude', label: 'Latitude', type: 'number', sortable: true },
  { key: 'longitude', label: 'Longitude', type: 'number', sortable: true },
];

// ✅ Validation Computed
const isFormValid = computed(() => {
  return (
    formData.factory.trim().length >= 3 &&
    formData.latitude >= -90 &&
    formData.latitude <= 90 &&
    formData.longitude >= -180 &&
    formData.longitude <= 180
  );
});

// ✅ Factory name check (only on add)
watch(
  () => formData.factory,
  debounce(async (newName) => {
    if (dialogMode.value !== 'add' || newName.trim().length < 3) {
      factoryExists.value = null;
      return;
    }
    try {
      const response = await factoriesApi.getFactories(0, 5, newName.trim(), '', 'asc');
      factoryExists.value = response.content.some(
        (f: CandyFactory) => f.factory.toLowerCase() === newName.trim().toLowerCase()
      );
    } catch (error) {
      console.error('Validation error:', error);
      factoryExists.value = null;
    }
  }, 400)
);

// ✅ Error Text
const getFactoryNameErrorText = computed(() => {
  const name = formData.factory.trim();
  if (name.length === 0) return 'Factory name is required';
  if (name.length < 3) return 'Must be at least 3 characters';
  if (factoryExists.value === true) return 'This factory already exists';
  return '';
});

function formatCurrency(value: number): string {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(value);
}

function formatCoordinate(value: number): string {
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 6,
    maximumFractionDigits: 6,
  }).format(value);
}

function resetFormData(): void {
  Object.assign(formData, { factory: '', latitude: 0, longitude: 0 });
  factoryExists.value = null;
}

function openDialog(mode: 'add' | 'edit', factory?: CandyFactory): void {
  dialogMode.value = mode;
  resetFormData();
  if (mode === 'edit' && factory) {
    editingFactory.value = factory;
    Object.assign(formData, factory);
  }
  showDialog.value = true;
}

function closeDialog(): void {
  showDialog.value = false;
  editingFactory.value = null;
  resetFormData();
}

async function deleteFactory(factory: CandyFactory): Promise<void> {
  if (!factory.factory) return;
  const confirmed = confirm(`Are you sure you want to delete "${factory.factory}"?`);
  if (!confirmed) return;
  try {
    await factoriesApi.deleteFactory(factory.factory);
    await loadData();
  } catch (error) {
    console.error('Delete error:', error);
    alert('Failed to delete factory. Please try again.');
  }
}

async function submitForm(): Promise<void> {
  if (!isFormValid.value || isSubmitting.value) return;

  if (dialogMode.value === 'add' && factoryExists.value === true) {
    alert('Factory already exists. Please use a unique name.');
    return;
  }

  isSubmitting.value = true;

  try {
    formData.latitude = +formData.latitude.toFixed(6);
    formData.longitude = +formData.longitude.toFixed(6);

    if (dialogMode.value === 'add') {
      await factoriesApi.createFactory(formData as CandyFactory);
    } else if (editingFactory.value?.factory) {
      await factoriesApi.updateFactory(editingFactory.value.factory, formData);
    }

    closeDialog();
    await loadData();
  } catch (error) {
    console.error('Save error:', error);
    alert('Failed to save factory. Please try again.');
  } finally {
    isSubmitting.value = false;
  }
}

async function loadData(): Promise<void> {
  try {
    const response = await factoriesApi.getFactories(
      currentPage.value,
      pageSize.value,
      currentSearchTerm.value,
      currentSortKey.value,
      currentSortOrder.value
    );
    factories.value = response.content || [];
    totalElements.value = response.totalElements;
    totalPages.value = response.totalPages;
  } catch (error) {
    console.error('Load error:', error);
    alert('Failed to load factories.');
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

onMounted(() => {
  loadData();
});
</script>
<style scoped>
.factories-view {
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

.coordinate {
  font-family: 'Courier New', monospace;
  font-weight: 500;
  color: #6366f1;
}

.badge {
  background: #dbeafe;
  color: #1d4ed8;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.dialog-body {
  padding: 1.5rem;
}
</style>