package com.shravani.CandyDistributor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DivisionPerformanceDTO {
    private String division;
    private Double totalSales;
    private Integer target;
    private Double performancePercentage;
}