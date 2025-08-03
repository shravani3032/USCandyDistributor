<template>
    <div class="sales-map-container">
     
        <div class="map-controls">
            <div class="control-group">
                <label class="control-label">Locations per page:</label>
                <select v-model="pageSize" @change="onPageSizeChange" class="page-size-select">
                    <option value="5">5</option>
                    <option value="10">10</option>
                    <option value="20">20</option>
                    <option value="50">50</option>
                </select>
            </div>

            <div class="pagination-info" v-if="paginationData">
                Showing {{ ((paginationData.number) * paginationData.size) + 1 }}-{{ Math.min((paginationData.number +
                    1) * paginationData.size, paginationData.totalElements) }}
                of {{ paginationData.totalElements }} locations
            </div>
        </div>

        <div ref="mapContainer" class="map-container"></div>

       
        <div class="pagination-controls" v-if="paginationData && paginationData.totalPages > 1">
            <button @click="goToPage(0)" :disabled="paginationData.first" class="pagination-btn" title="First page">
                ⏮️
            </button>

            <button @click="goToPage(currentPage - 1)" :disabled="paginationData.first" class="pagination-btn"
                title="Previous page">
                ⏪
            </button>

            <div class="page-numbers">
                <button v-for="page in visiblePages" :key="page" @click="goToPage(page - 1)"
                    :class="['page-number', { 'active': page - 1 === currentPage }]">
                    {{ page }}
                </button>
            </div>

            <button @click="goToPage(currentPage + 1)" :disabled="paginationData.last" class="pagination-btn"
                title="Next page">
                ⏩
            </button>

            <button @click="goToPage(paginationData.totalPages - 1)" :disabled="paginationData.last"
                class="pagination-btn" title="Last page">
                ⏭️
            </button>
        </div>

        
        <div v-if="loading" class="loading-overlay">
            <div class="loading-spinner"></div>
            <p>Loading locations...</p>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';
import L from 'leaflet';

interface SalesLocation {
    city: string;
    zip: string;
    lat: number;
    lng: number;
    totalSales: number;
}

interface PaginationData {
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
    numberOfElements: number;
    first: boolean;
    last: boolean;
    empty: boolean;
}

interface ApiResponse {
    content: SalesLocation[];
    pageable: any;
    totalPages: number;
    totalElements: number;
    last: boolean;
    size: number;
    number: number;
    numberOfElements: number;
    first: boolean;
    empty: boolean;
}

interface Props {
    title?: string;
}

const props = withDefaults(defineProps<Props>(), {
    title: 'Sales Locations'
});

const mapContainer = ref<HTMLElement>();
const locations = ref<SalesLocation[]>([]);
const paginationData = ref<PaginationData | null>(null);
const currentPage = ref(0);
const pageSize = ref(10);
const loading = ref(false);

let map: L.Map | null = null;
let markersLayer: L.LayerGroup | null = null;

const fetchLocations = async (page: number = 0, size: number = 10) => {
    try {
        loading.value = true;
        const response = await fetch(`http://localhost:8080/api/analytics/sales-by-location?size=${size}&page=${page}`);

        if (!response.ok) {
            throw new Error('Failed to fetch locations');
        }

        const data: ApiResponse = await response.json();

        locations.value = data.content;
        paginationData.value = {
            totalPages: data.totalPages,
            totalElements: data.totalElements,
            size: data.size,
            number: data.number,
            numberOfElements: data.numberOfElements,
            first: data.first,
            last: data.last,
            empty: data.empty
        };

        currentPage.value = page;

        // Update markers after fetching data
        updateMarkers();

    } catch (error) {
        console.error('Error fetching locations:', error);
    } finally {
        loading.value = false;
    }
};

const goToPage = (page: number) => {
    if (page >= 0 && page < (paginationData.value?.totalPages || 0)) {
        fetchLocations(page, pageSize.value);
    }
};

const onPageSizeChange = () => {
    // Reset to first page when changing page size
    fetchLocations(0, pageSize.value);
};

// Computed property for visible page numbers
const visiblePages = computed(() => {
    if (!paginationData.value) return [];

    const totalPages = paginationData.value.totalPages;
    const current = currentPage.value + 1; // Convert to 1-based
    const pages: number[] = [];

    // Show max 5 page numbers
    const maxVisible = 5;
    let start = Math.max(1, current - Math.floor(maxVisible / 2));
    let end = Math.min(totalPages, start + maxVisible - 1);

    // Adjust start if we're near the end
    if (end - start + 1 < maxVisible) {
        start = Math.max(1, end - maxVisible + 1);
    }

    for (let i = start; i <= end; i++) {
        pages.push(i);
    }

    return pages;
});

const initializeMap = () => {
    if (!mapContainer.value) return;

    // Create map
    map = L.map(mapContainer.value).setView([39.8283, -98.5795], 4); // Center of US

    // Add tile layer
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
    }).addTo(map);

    // Create markers layer
    markersLayer = L.layerGroup().addTo(map);

    // Initial data fetch
    fetchLocations(0, pageSize.value);
};

const updateMarkers = () => {
    if (!map || !markersLayer) return;

    // Clear existing markers
    markersLayer.clearLayers();

    if (!locations.value || locations.value.length === 0) return;

    // Find min and max sales for scaling
    const salesValues = locations.value.map(loc => loc.totalSales);
    const minSales = Math.min(...salesValues);
    const maxSales = Math.max(...salesValues);

    // Add markers for each location
    locations.value.forEach((location, index) => {
        // Scale marker size based on sales (min: 15px, max: 30px)
        const normalizedSize = salesValues.length > 1 ?
            ((location.totalSales - minSales) / (maxSales - minSales)) : 0.5;
        const markerSize = 18 + (normalizedSize * 15);

        // Calculate global rank (for display purposes)
        const globalRank = (currentPage.value * pageSize.value) + index + 1;

        // Create custom icon with size based on sales
        const customIcon = L.divIcon({
            html: `
        <div style="
          width: ${markerSize}px;
          height: ${markerSize}px;
          background: linear-gradient(135deg, #3b82f6, #1d4ed8);
          border-radius: 50%;
          border: 2px solid white;
          box-shadow: 0 2px 8px rgba(0,0,0,0.3);
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          font-weight: bold;
          font-size: ${Math.max(9, markerSize * 0.35)}px;
        ">
          ${globalRank}
        </div>
      `,
            className: 'custom-marker',
            iconSize: [markerSize, markerSize],
            iconAnchor: [markerSize / 2, markerSize / 2]
        });

        // Create marker
        const marker = L.marker([location.lat, location.lng], { icon: customIcon });

        // Add popup with more detailed information
        marker.bindPopup(`
      <div style="text-align: center; padding: 8px; min-width: 200px;">
        <strong style="color: #1f2937; font-size: 14px;">Rank #${globalRank}</strong><br>
        <div style="margin: 8px 0; padding: 4px; background: #f3f4f6; border-radius: 4px;">
          <strong style="color: #374151;">${location.city}</strong><br>
          <small style="color: #6b7280;">ZIP: ${location.zip}</small>
        </div>
        <div style="background: #10b981; color: white; padding: 6px; border-radius: 4px; margin: 4px 0;">
          <strong>${location.totalSales.toLocaleString('en-US', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        })}</strong>
        </div>
        <small style="color: #9ca3af;">
          ${location.lat.toFixed(4)}, ${location.lng.toFixed(4)}
        </small>
      </div>
    `);

        // Add to markers layer
        markersLayer!.addLayer(marker);
    });

    // Fit map to show all markers with padding
    if (locations.value.length > 0) {
        const group = new L.featureGroup(markersLayer.getLayers());
        map.fitBounds(group.getBounds().pad(0.1));
    }
};

// Watch for location changes
// watch(() => props.locations, updateMarkers, { deep: true });

onMounted(() => {
    // Small delay to ensure DOM is ready
    setTimeout(initializeMap, 100);
});

onUnmounted(() => {
    if (map) {
        map.remove();
        map = null;
    }
});
</script>

<style scoped>
.sales-map-container {
    width: 100%;
    height: 100%;
    min-height: 500px;
    display: flex;
    flex-direction: column;
    position: relative;
}

.map-controls {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem 0;
    border-bottom: 1px solid #e5e7eb;
    margin-bottom: 1rem;
}

.control-group {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.control-label {
    font-size: 0.875rem;
    font-weight: 500;
    color: #374151;
}

.page-size-select {
    padding: 0.375rem 0.75rem;
    border: 1px solid #d1d5db;
    border-radius: 0.375rem;
    font-size: 0.875rem;
    background-color: white;
    cursor: pointer;
    transition: border-color 0.2s ease;
}

.page-size-select:hover {
    border-color: #3b82f6;
}

.page-size-select:focus {
    outline: none;
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.pagination-info {
    font-size: 0.875rem;
    color: #6b7280;
    font-weight: 500;
}

.map-container {
    flex: 1;
    width: 100%;
    border-radius: 8px;
    overflow: hidden;
    min-height: 350px;
}

.pagination-controls {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 0.5rem;
    padding: 1rem 0;
    border-top: 1px solid #e5e7eb;
    margin-top: 1rem;
}

.pagination-btn {
    padding: 0.5rem 0.75rem;
    border: 1px solid #d1d5db;
    background-color: white;
    color: #374151;
    border-radius: 0.375rem;
    cursor: pointer;
    transition: all 0.2s ease;
    font-size: 0.875rem;
    min-width: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.pagination-btn:hover:not(:disabled) {
    background-color: #f3f4f6;
    border-color: #9ca3af;
}

.pagination-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.page-numbers {
    display: flex;
    gap: 0.25rem;
}

.page-number {
    padding: 0.5rem 0.75rem;
    border: 1px solid #d1d5db;
    background-color: white;
    color: #374151;
    border-radius: 0.375rem;
    cursor: pointer;
    transition: all 0.2s ease;
    font-size: 0.875rem;
    min-width: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.page-number:hover {
    background-color: #f3f4f6;
    border-color: #9ca3af;
}

.page-number.active {
    background-color: #3b82f6;
    border-color: #3b82f6;
    color: white;
}

.loading-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(255, 255, 255, 0.9);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    z-index: 1000;
    border-radius: 8px;
}

.loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #e5e7eb;
    border-top: 4px solid #3b82f6;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 1rem;
}

@keyframes spin {
    0% {
        transform: rotate(0deg);
    }

    100% {
        transform: rotate(360deg);
    }
}

.loading-overlay p {
    margin: 0;
    color: #6b7280;
    font-weight: 500;
}

/* Mobile responsiveness */
@media (max-width: 768px) {
    .map-controls {
        flex-direction: column;
        gap: 1rem;
        align-items: stretch;
    }

    .pagination-controls {
        flex-wrap: wrap;
        gap: 0.25rem;
    }

    .pagination-btn,
    .page-number {
        min-width: 35px;
        padding: 0.375rem 0.5rem;
    }

    .pagination-info {
        text-align: center;
        font-size: 0.8rem;
    }
}

/* Leaflet popup styling */
:global(.leaflet-popup-content-wrapper) {
    border-radius: 8px;
}

:global(.leaflet-popup-content) {
    margin: 8px 12px;
}

:global(.custom-marker) {
    background: transparent !important;
    border: none !important;
}
</style>