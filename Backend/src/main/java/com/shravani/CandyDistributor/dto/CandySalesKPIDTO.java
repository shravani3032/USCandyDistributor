package com.shravani.CandyDistributor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandySalesKPIDTO {
    private Double totalSales;
    private Long totalOrders;
    private Double averageSales;
}


