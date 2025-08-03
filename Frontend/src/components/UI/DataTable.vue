<template>
  <div class="data-table">
    <div class="table-controls">
      <VTextField v-if="showSearch" v-model="internalSearchTerm" placeholder="Search..." @input="emitSearch"
        class="search-input" />

      <VButton label="Add New" appearance="filled" density="compact" class="add-button" @click="$emit('add')">
        <VIcon slot="icon" name="plus-solid" />
      </VButton>
    </div>

    <VDataGrid class="vivid-table" selection-mode="single-row">
      <VDataGridRow v-for="(item, index) in allRows" :key="item.id || `row-${index}`"
        :row-type="item.isHeader ? 'header' : undefined">

        <VDataGridCell v-for="column in columns" :key="column.key"
          :cell-type="item.isHeader ? 'columnheader' : undefined"
          :sort-direction="item.isHeader && showSort && column.sortable ? (sortKey === column.key ? sortOrder : 'none') : undefined"
          @click="item.isHeader && showSort && column.sortable && handleSort(column.key)"
          :class="{ 'sortable-header': item.isHeader && showSort && column.sortable }">

          <template v-if="item.isHeader">
            {{ column.label }}
          </template>

          <template v-else>
            <slot :name="`cell-${column.key}`" :value="getNestedValue(item, column.key)" :item="item">
              {{ formatValue(getNestedValue(item, column.key), column.type) }}
            </slot>
          </template>
        </VDataGridCell>

        <VDataGridCell :cell-type="item.isHeader ? 'columnheader' : undefined"
          :class="item.isHeader ? 'actions-header' : 'actions-cell'">

          <template v-if="item.isHeader">
            Actions
          </template>

          <template v-else>
            <div class="action-buttons">
              <VButton variant="primary" density="compact" class="action-btn edit-btn" @click="handleEdit(item)"
                title="Edit" icon="compose-solid" />
              <VButton variant="error" density="compact" class="action-btn delete-btn" @click="handleDelete(item)"
                title="Delete" icon="delete-solid" />
            </div>
          </template>
        </VDataGridCell>
      </VDataGridRow>
    </VDataGrid>

    <div v-if="showPagination" class="table-footer">
      <div class="pagination-info">
        Showing {{ totalElements === 0 ? 0 : (currentPage * pageSize) + 1 }} to {{ Math.min((currentPage + 1) *
          pageSize, totalElements) }} of {{ totalElements }} entries
      </div>

      <VPagination :total="totalPages" :selected-index="currentPage" nav-icons @pagination-change="onPaginationChange"
        class="pagination-bar" />

      <div class="page-size-select">
        <span>Items per page:</span>
        <select v-model="internalPageSize" @change="emitPageSizeChange" class="page-size-dropdown">
          <option :value="5">5</option>
          <option :value="10">10</option>
          <option :value="20">20</option>
          <option :value="50">50</option>
        </select>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';

import { VDataGrid, VDataGridRow, VDataGridCell, VPagination, VButton, VTextField, VIcon } from '@vonage/vivid-vue';

interface Column {
  key: string;
  label: string;
  type?: 'text' | 'number' | 'currency' | 'date';
  sortable?: boolean;
}

interface Props {
  data: any[];
  columns: Column[];
 
  currentPage?: number;
  pageSize?: number;
  totalElements?: number;
  totalPages?: number;
  sortKey?: string;
  sortOrder?: 'asc' | 'desc';
  searchTerm?: string;
  
  showPagination?: boolean;
  showSearch?: boolean;
  showSort?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  currentPage: 0,
  pageSize: 10,
  totalElements: 0,
  totalPages: 0,
  sortKey: '',
  sortOrder: 'asc',
  searchTerm: '',
  showPagination: true, 
  showSearch: true,     
  showSort: true,       
});

const emit = defineEmits(['add', 'edit', 'delete', 'page-change', 'page-size-change', 'search', 'sort']);

const internalSearchTerm = ref(props.searchTerm);
const internalPageSize = ref(props.pageSize);


watch(() => props.searchTerm, (val) => {
  internalSearchTerm.value = val;
});

watch(() => props.pageSize, (val) => {
  internalPageSize.value = val;
});

const sortKey = computed(() => props.sortKey);
const sortOrder = computed(() => props.sortOrder);


const allRows = computed(() => {
  const headerRow = {
    isHeader: true,
    id: 'header-row'
  };

  return [headerRow, ...props.data];
});

function getNestedValue(obj: any, path: string) {
  return path.split('.').reduce((current, key) => current?.[key], obj);
}

function formatValue(value: any, type?: string) {
  if (value === null || value === undefined) return '';

  switch (type) {
    case 'currency':
      return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
      }).format(value);
    case 'number':
      return new Intl.NumberFormat('en-US').format(value);
    case 'date':
      return new Date(value).toLocaleDateString();
    default:
      return value;
  }
}


function emitSearch() {

  if (props.showSearch) {
    emit('search', internalSearchTerm.value);
  }
}

function emitPageSizeChange() {
  
  if (props.showPagination) {
    emit('page-size-change', internalPageSize.value);
  }
}

function onPaginationChange(event: CustomEvent) {
 
  if (props.showPagination) {
    const newPage = event.detail.selectedIndex;
    if (newPage >= 0 && newPage < (props.totalPages || 0)) { 
      emit('page-change', newPage);
    }
  }
}

function handleSort(key: string) {
  
  if (props.showSort) {
    emit('sort', key);
  }
}

function handleEdit(rowData: any) {
  emit('edit', rowData);
}

function handleDelete(rowData: any) {
  emit('delete', rowData);
}
</script>

<style scoped>
.data-table {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.table-controls {
  padding: 1.5rem;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.search-input {
  width: 100%;
  max-width: 300px;
}

.add-button {
  min-width: 120px;
}

.vivid-table {
  width: 100%;
  overflow-x: auto;
}

.sortable-header {
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.sortable-header:hover {
  background-color: var(--vvd-color-neutral-50);
}

.sort-indicator {
  margin-left: 0.5rem;
  color: #3b82f6;
  font-weight: bold;
}

/* Actions column header and cell */
.actions-header {
  width: 120px;
  min-width: 120px;
  text-align: center;
}

.actions-cell {
  width: 120px;
  min-width: 120px;
  text-align: center;
  vertical-align: middle;
}


.action-buttons {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  justify-content: center;
  flex-wrap: nowrap;
  height: 100%;
}

.action-btn {
  width: 36px;
  height: 36px;
  min-width: 36px;
  max-width: 36px;
  padding: 0 !important;
  border-radius: 6px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  border: 1px solid #d1d5db !important;
  flex-shrink: 0;
  background-color: #f9fafb !important;
  color: #6b7280 !important;
}

.action-btn:hover {
  background-color: #f3f4f6 !important;
  border-color: #9ca3af !important;
  color: #374151 !important;
}

/* Ensure consistent row and cell alignment */
.data-row {
  border-bottom: 1px solid #e5e7eb;
}

.data-cell {
  padding: 0.75rem 1rem;
  vertical-align: middle;
  border-right: 1px solid #f3f4f6;
}

.data-cell:last-child {
  border-right: none;
}

/* Table footer styling */
.table-footer {
  padding: 1rem 1.5rem;
  background: #f9fafb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-top: 1px solid #e5e7eb;
  flex-wrap: wrap;
  gap: 1rem;
}

.pagination-info {
  color: #6b7280;
  font-size: 0.9rem;
  min-width: 200px;
}

.pagination-bar {
  flex: 1;
  justify-content: center;
}

.page-size-select {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #6b7280;
}

.page-size-dropdown {
  padding: 0.4rem 0.75rem;
  border-radius: 6px;
  border: 1px solid #d1d5db;
  background: white;
  font-size: 0.9rem;
  cursor: pointer;
  transition: border-color 0.2s ease;
}

.page-size-dropdown:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

/* Responsive design */
@media (max-width: 768px) {
  .table-controls {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
  }

  .search-input {
    max-width: none;
  }

  .table-footer {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .pagination-info {
    min-width: auto;
    order: 1;
  }

  .pagination-bar {
    order: 2;
  }

  .page-size-select {
    order: 3;
  }

  .actions-cell {
    width: 100px;
    min-width: 100px;
    padding: 0.5rem;
  }

  .action-buttons {
    gap: 0.25rem;
  }

  .action-btn {
    width: 32px;
    height: 32px;
    min-width: 32px;
    max-width: 32px;
  }
}


@media (max-width: 1024px) {
  .vivid-table {
    font-size: 0.9rem;
  }
}
</style>