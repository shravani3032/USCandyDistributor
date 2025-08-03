package com.shravani.CandyDistributor.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.*;

@Getter
@Setter
public class CandyProductDTO {
    @NotBlank(message = "Product ID cannot be blank")
    private String id;

    @NotBlank(message = "Product name cannot be blank")
    @Pattern(regexp = ".*\\D.*", message = "Product name cannot be only numbers")
    private String name;

    @NotBlank(message = "Division cannot be blank")
    private String division;

    @NotBlank(message = "Factory cannot be blank")
    private String factory;

    @NotNull(message = "Unit price is required")
    @PositiveOrZero(message = "Unit price must be zero or positive")
    private Double unitPrice;

    @NotNull(message = "Unit cost is required")
    @PositiveOrZero(message = "Unit cost must be zero or positive")
    private Double unitCost;
}