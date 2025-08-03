package com.shravani.CandyDistributor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Total Sales per Product within a date range")
public class ProductSalesDTO {
    @Schema(description = "Product name", example = "CHO-NUT-13000")
    private String productId;

    @Schema(description = "Total sales for this product", example = "7890.12")
    private Double totalSales;
}

