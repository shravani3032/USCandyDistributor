<template>
    <div class="targets-view">
        <div class="page-header">
            <h1 class="page-title">Candy Targets</h1>
            <p class="page-subtitle">Manage division targets and information</p>
        </div>

        <DataTable :data="targets" :columns="columns" :currentPage="currentPage" :pageSize="pageSize"
            :totalElements="totalElements" :totalPages="totalPages" @add="openDialog('add')"
            @edit="openDialog('edit', $event)" @delete="deleteTarget" @page-change="handlePageChange"
            @page-size-change="handlePageSizeChange" @search="handleSearch" @sort="handleSort">
            <template #cell-target="{ value }">
                <span class="currency">${{ formatCurrency(value || 0) }}</span>
            </template>
        </DataTable>

        <VDialog :open="showDialog" :headline="dialogMode === 'add' ? 'Add New Target' : 'Edit Target'" class="mydialog"
            modal @close="closeDialog">
            <template #body>
                <VLayout direction="column" gutters="large" class="dialog-body">
                    <form @submit.prevent="submitForm">
                        <VLayout direction="column" column-basis="block" gap="medium">
                            <VTextField label="Division Name" placeholder="Enter division name"
                                v-model="formData.division" :disabled="dialogMode === 'edit'" required
                                :error-text="divisionExists === true ? 'This division already exists' : ''"
                                :success-text="divisionExists === false ? 'Division is available' : ''" />

                            <VNumberField label="Target" placeholder="0" :model-value="formData.target.toString()"
                                @update:model-value="(val) => formData.target = parseFloat(val) || 0" :min="0"
                                required />
                        </VLayout>
                    </form>
                </VLayout>
            </template>

            <template #action-items>
                <VButton appearance="outlined" label="Cancel" @click="closeDialog" />
                <VButton appearance="filled"
                    :label="isSubmitting ? 'Saving...' : (dialogMode === 'add' ? 'Add Target' : 'Update Target')"
                    :disabled="isSubmitting || !isFormValid || (dialogMode === 'add' && divisionExists === true)"
                    @click="submitForm" />
            </template>
        </VDialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, computed, watch } from 'vue';
import debounce from 'lodash.debounce';
import { targetsApi } from '../api/targets';
import DataTable from '../components/UI/DataTable.vue';
import type { Targets } from '../types';
import { VDialog, VButton, VTextField, VNumberField, VLayout } from '@vonage/vivid-vue';

type Column = {
    key: string;
    label: string;
    type?: 'text' | 'number' | 'currency' | 'date';
    sortable?: boolean;
};

interface TargetFormData {
    division: string;
    target: number;
}

const targets = ref<Targets[]>([]);
const isSubmitting = ref(false);
const divisionExists = ref<boolean | null>(null);

const currentPage = ref(0);
const pageSize = ref(10);
const totalElements = ref(0);
const totalPages = ref(0);

const currentSortKey = ref('');
const currentSortOrder = ref<'asc' | 'desc'>('asc');
const currentSearchTerm = ref('');

const showDialog = ref(false);
const dialogMode = ref<'add' | 'edit'>('add');
const editingTarget = ref<Targets | null>(null);

const formData = reactive<TargetFormData>({
    division: '',
    target: 0,
});

const columns: Column[] = [
    { key: 'division', label: 'Division Name', type: 'text', sortable: true },
    { key: 'target', label: 'Target', type: 'currency', sortable: true },
];

const isFormValid = computed(() => {
    return (
        formData.division.trim() !== '' &&
        formData.target >= 0 &&
        !isNaN(formData.target)
    );
});

// Fixed debounced division validation - only check for add mode and exclude current division in edit mode
const checkDivisionExists = debounce(async (newName: string) => {
    if (newName.trim().length < 3) {
        divisionExists.value = null;
        return;
    }

    try {
        // For edit mode, don't validate if it's the same division name
        if (dialogMode.value === 'edit' && editingTarget.value?.division === newName.trim()) {
            divisionExists.value = null;
            return;
        }

        const response = await targetsApi.getTargets(0, 5, newName.trim(), '', 'asc');

        // Check if any exact matches exist
        const exactMatch = response.content?.some(target =>
            target.division.toLowerCase() === newName.trim().toLowerCase()
        );

        divisionExists.value = exactMatch || false;
    } catch (error) {
        console.error('Validation error:', error);
        divisionExists.value = null;
    }
}, 400);

watch(
    () => formData.division,
    (newName) => {
        if (dialogMode.value === 'add') {
            checkDivisionExists(newName);
        } else {
            divisionExists.value = null;
        }
    }
);

function formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', {
        minimumFractionDigits: 0,
        maximumFractionDigits: 0,
    }).format(value);
}

function resetFormData(): void {
    formData.division = '';
    formData.target = 0;
    divisionExists.value = null;
}

function openDialog(mode: 'add' | 'edit', target?: Targets): void {
    dialogMode.value = mode;
    resetFormData();

    if (mode === 'edit' && target) {
        editingTarget.value = target;
        // Use Object.assign to properly copy values
        formData.division = target.division;
        formData.target = target.target;
    } else {
        editingTarget.value = null;
    }

    showDialog.value = true;
}

function closeDialog(): void {
    showDialog.value = false;
    editingTarget.value = null;
    resetFormData();
}

async function deleteTarget(target: Targets): Promise<void> {
    if (!target.division) return;

    const confirmed = confirm(`Are you sure you want to delete "${target.division}"?`);
    if (!confirmed) return;

    try {
        await targetsApi.deleteTarget(target.division);
        await loadData();
    } catch (error) {
        console.error('Delete error:', error);
        alert('Failed to delete target. Please try again.');
    }
}

async function submitForm(): Promise<void> {
    if (!isFormValid.value || isSubmitting.value) return;

    // Additional validation for add mode
    if (dialogMode.value === 'add' && divisionExists.value === true) {
        return;
    }

    isSubmitting.value = true;

    try {
        const targetData: Targets = {
            division: formData.division.trim(),
            target: Number(formData.target)
        };

        if (dialogMode.value === 'add') {
            await targetsApi.createTarget(targetData);
        } else if (editingTarget.value?.division) {
            await targetsApi.updateTarget(editingTarget.value.division, targetData);
        }

        closeDialog();
        await loadData();
    } catch (error) {
        console.error('Save error:', error);
        alert('Failed to save target. Please try again.');
    } finally {
        isSubmitting.value = false;
    }
}

async function loadData(): Promise<void> {
    try {
        const response = await targetsApi.getTargets(
            currentPage.value,
            pageSize.value,
            currentSearchTerm.value,
            currentSortKey.value,
            currentSortOrder.value
        );

        targets.value = response.content || [];
        totalElements.value = response.totalElements;
        totalPages.value = response.totalPages;
    } catch (error) {
        console.error('Load error:', error);
        alert('Failed to load targets.');
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
.targets-view {
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