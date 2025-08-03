package com.shravani.CandyDistributor.service;

import com.shravani.CandyDistributor.dto.*;
import com.shravani.CandyDistributor.model.CandyTarget;
import com.shravani.CandyDistributor.repository.CandySalesAnalyticsRepository;
import com.shravani.CandyDistributor.repository.CandyTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandySalesAnalyticsService {
    private final CandySalesAnalyticsRepository repository;
    private final CandyTargetRepository candyTargetRepository;


    public List<CandySalesMonthlyDTO> getLast12MonthsSalesSummary() {
        // Start from the first day of the current month
        LocalDate today = LocalDate.now().withDayOfMonth(1);
        // Get YearMonth 11 months ago (to include 12 months total)
        YearMonth oneYearAgo = YearMonth.from(today.minusMonths(11));

        // Convert YearMonth to Date for JPQL parameter
        Date startDate = java.sql.Date.valueOf(oneYearAgo.atDay(1));

        // Fetch existing sales data from the repository
        List<Object[]> rawData = repository.getLast12MonthsSales(startDate);

        // Convert rawData into a map for fast lookup (YearMonth -> totalSales)
        Map<YearMonth, Double> salesMap = rawData.stream()
                .collect(Collectors.toMap(
                        obj -> YearMonth.of((int) obj[0], (int) obj[1]),
                        obj -> ((Number) obj[2]).doubleValue()
                ));

        // Prepare the final result list including 0.00 for missing months
        List<CandySalesMonthlyDTO> finalList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            YearMonth ym = oneYearAgo.plusMonths(i);
            double rawSales = salesMap.getOrDefault(ym, 0.0);
            double sales = BigDecimal.valueOf(rawSales)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            String label = String.format("%s %d",
                    ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                    ym.getYear());

            finalList.add(new CandySalesMonthlyDTO(label, sales));
        }

        return finalList;
    }





    public List<CandySalesByProductDTO> getSalesByProduct() {
        List<Object[]> rawData = repository.getSalesByProduct();

        return rawData.stream().map(obj -> {
            CandySalesByProductDTO dto = new CandySalesByProductDTO();
            dto.setProductName((String) obj[0]);

            double totalSales = ((Number) obj[1]).doubleValue(); // safer than cast to (Double)
            double roundedSales = BigDecimal.valueOf(totalSales)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            dto.setTotalSales(roundedSales);
            return dto;
        }).toList();
    }


    public List<CandySalesAnalyticDTO> getRegionProductSalesSummary() {
        return repository.getRegionProductSalesSummary();
    }

    public List<DivisionPerformanceDTO> getAllDivisionPerformance() {
        List<Object[]> salesData = repository.findTotalSalesByDivision();
        List<CandyTarget> targets = candyTargetRepository.findAll();

        List<DivisionPerformanceDTO> result = new ArrayList<>();

        for (Object[] row : salesData) {
            String division = (String) row[0];
            Double totalSales = (Double) row[1];

            CandyTarget target = targets.stream()
                    .filter(t -> t.getDivision().equalsIgnoreCase(division))
                    .findFirst()
                    .orElse(null);

            if (target != null) {
                double performancePercentage = (totalSales / target.getTarget()) * 100;
                result.add(new DivisionPerformanceDTO(division, totalSales, target.getTarget(), performancePercentage));
            }
        }
        return result;
    }

    public DivisionPerformanceDTO getSpecificDivisionPerformance(String division) {
        List<Object[]> salesData = repository.findTotalSalesBySpecificDivision(division);

        if (salesData.isEmpty()) {
            throw new RuntimeException("No sales data found for division: " + division);
        }

        Object[] row = salesData.get(0);
        Double totalSales = (Double) row[1];

        CandyTarget target = candyTargetRepository.findByDivision(division)
                .orElseThrow(() -> new RuntimeException("No target data found for division: " + division));

        double performancePercentage = (totalSales / target.getTarget()) * 100;
        return new DivisionPerformanceDTO(division, totalSales, target.getTarget(), performancePercentage);
    }

//    public CandySalesKPIDTO getKpiValues() {
//        List<Object[]> results = repository.getKpiValues();
//
//
//        if (results.isEmpty() || results.getFirst().length < 3) {
//            throw new RuntimeException("Unexpected result format from getKpiValues query.");
//        }
//
//        Object[] row = results.getFirst();
//        Double totalSales = row[0] != null ? ((Number) row[0]).doubleValue() : 0.0;
//        Long totalOrders = row[1] != null ? ((Number) row[1]).longValue() : 0L;
//        Double averageSales = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
//
//        return new CandySalesKPIDTO(totalSales, totalOrders, averageSales);
//    }

    public SalesSummaryDTO getSalesSummary() {
        List<Object[]> results = repository.getSalesSummaryJPQL();

        if (results.isEmpty()) {
            return new SalesSummaryDTO(); // Return empty DTO with default values
        }

        Object[] row = results.get(0);
        SalesSummaryDTO dto = new SalesSummaryDTO();

        dto.setTotalSales(row[0] != null ? ((Number) row[0]).doubleValue() : 0.0);
        dto.setSalesCount(row[1] != null ? ((Number) row[1]).longValue() : 0L);
        dto.setTotalTarget(row[2] != null ? ((Number) row[2]).doubleValue() : 0.0);
        dto.setPercentageGoalAchieved(row[3] != null ? ((Number) row[3]).doubleValue() : 0.0);

        return dto;
    }
    public Page<SalesLocationDTO> getTopSalesLocations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findTopSalesByLocation(pageable);
    }
}
