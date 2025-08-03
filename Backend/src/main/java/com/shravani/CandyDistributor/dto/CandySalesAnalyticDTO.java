package com.shravani.CandyDistributor.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandySalesAnalyticDTO {
    private String region;
    private String productId;
    private double totalSales;
    private long totalUnits;
}
