package com.shravani.CandyDistributor.dto;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class SalesSummaryDTO {

    @DecimalMin(value = "0.0", inclusive = true, message = "Total sales cannot be negative")
    private double totalSales;

    @Min(value = 0, message = "Sales count cannot be negative")
    private long salesCount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total target cannot be negative")
    private double totalTarget;

    @DecimalMin(value = "0.0", inclusive = true, message = "Percentage goal achieved cannot be negative")
    private double percentageGoalAchieved;
}
