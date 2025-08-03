package com.shravani.CandyDistributor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandySalesByProductDTO {
    private String productName;
    private Double totalSales;
}

