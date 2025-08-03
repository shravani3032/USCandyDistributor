package com.shravani.CandyDistributor.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Total Sales per Region")
public class RegionSalesDTO {
    @Schema(description = "Region name", example = "West")
    private String region;

    @Schema(description = "Total sales amount in this region", example = "12345.67")
    private Double totalSales;
}
