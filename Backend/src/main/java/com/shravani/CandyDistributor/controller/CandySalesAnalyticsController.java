package com.shravani.CandyDistributor.controller;

import com.shravani.CandyDistributor.dto.*;
import com.shravani.CandyDistributor.service.CandySalesAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class CandySalesAnalyticsController {
    private final CandySalesAnalyticsService service;


    @GetMapping("/last-12-months-sales")
    public List<CandySalesMonthlyDTO> getMonthlySales2024() {
        return service.getLast12MonthsSalesSummary();
    }


    @GetMapping("/sales-by-product")
    public List<CandySalesByProductDTO> getSalesByProductWithYear() {
        return service.getSalesByProduct();
    }

    @GetMapping("/sales-by-region")
    public List<CandySalesAnalyticDTO> getRegionProductSalesSummary() {
        return service.getRegionProductSalesSummary();
    }

    @GetMapping("/division-performance")
    public List<DivisionPerformanceDTO> getAllDivisionPerformance() {
        return service.getAllDivisionPerformance();
    }

    @GetMapping("/division-performance/{division}")
    public DivisionPerformanceDTO getSpecificDivisionPerformance(@PathVariable String division) {
        return service.getSpecificDivisionPerformance(division);
    }

    @GetMapping("/summary")
    public ResponseEntity<SalesSummaryDTO> getSalesSummary() {
        return ResponseEntity.ok(service.getSalesSummary()); // Correctly calling on the injected service instance
    }
    @GetMapping("/sales-by-location")
    public ResponseEntity<Page<SalesLocationDTO>> getTopSalesLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) { // Default page 0, default size 10 (can be adjusted)
        return ResponseEntity.ok(service.getTopSalesLocations(page, size));
    }
}
