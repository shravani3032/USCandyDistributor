package com.shravani.CandyDistributor.dto;

import lombok.AllArgsConstructor; // Add this import
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor // Add this annotation
public class SalesLocationDTO {
    @NotNull(message = "City Name cannot be null")
    private String city;

    @NotNull(message = "Zip code cannot be null")
    private String zip;

    @DecimalMin(value = "-90.0", inclusive = true, message = "Latitude must be >= -90.0")
    @DecimalMax(value = "90.0", inclusive = true, message = "Latitude must be <= 90.0")
    private double lat;

    @DecimalMin(value = "-180.0", inclusive = true, message = "Longitude must be >= -180.0")
    @DecimalMax(value = "180.0", inclusive = true, message = "Longitude must be <= 180.0")
    private double lng;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total sales cannot be negative")
    private double totalSales;
}