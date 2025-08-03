package com.shravani.CandyDistributor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UsZipDTO {

    @NotBlank(message = "Zip code cannot be blank")
    private String zip;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double lat;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double lng;

    @NotBlank(message = "City cannot be blank")
    private String city;

    @NotBlank(message = "State ID cannot be blank")
    private String stateId;

    @NotBlank(message = "State Name cannot be blank")
    private String stateName;

    @NotBlank(message = "ZCTA cannot be blank")
    private String zcta;

    private String parentZcta;

    @NotNull(message = "Population is required")
    @PositiveOrZero(message = "Population cannot be negative")
    private Integer population;

    @NotNull(message = "Density is required")
    @PositiveOrZero(message = "Density cannot be negative")
    private Double density;

    @NotBlank(message = "County FIPS cannot be blank")
    private String countyFips;

    @NotBlank(message = "County Name cannot be blank")
    private String countyName;

    @Schema(hidden = true)
    @NotNull(message = "County Weights map is required")
    private Map<String, Double> countyWeights;
    @NotBlank(message = "County Names All cannot be blank")
    private String countyNamesAll;

    @NotBlank(message = "County FIPS All cannot be blank")
    private String countyFipsAll;

    @NotNull(message = "Imprecise flag is required")
    private Boolean imprecise;

    @NotNull(message = "Military flag is required")
    private Boolean military;

    @NotBlank(message = "Timezone cannot be blank")
    private String timezone;
}