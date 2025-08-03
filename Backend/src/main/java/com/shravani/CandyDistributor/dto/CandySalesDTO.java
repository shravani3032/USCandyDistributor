package com.shravani.CandyDistributor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class CandySalesDTO {
//    @NotBlank(message = "Sales ID cannot be blank")
    @JsonProperty("salesId")
    private Integer salesId;

    @NotBlank(message = "Order ID cannot be blank")
    @JsonProperty("orderId")
    private String orderId;

    @NotNull(message = "Order Date is required")
    @JsonProperty("orderDate")
    private Date orderDate;

    @NotNull(message = "Ship Date is required")
    @JsonProperty("shipDate")
    private Date shipDate;

    @NotBlank(message = "Ship Mode is required")
    @JsonProperty("shipMode")
    private String shipMode;

    @NotBlank(message = "Customer ID is required")
    @JsonProperty("customerId")
    private String customerId;

    @NotBlank(message = "Country/Region is required")
    @JsonProperty("countryRegion")
    private String countryRegion;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State/Province is required")
    @JsonProperty("stateProvince")
    private String stateProvince;

    @NotBlank(message = "Postal Code is required")
    @JsonProperty("postalCode")
    private String postalCode;

    @NotBlank(message = "Division is required")
    private String division;

    @NotBlank(message = "Region is required")
    private String region;

    @NotBlank(message = "Product ID is required")
    @JsonProperty("productId")
    private String productId;

    @NotBlank(message = "Product Name is required")
    @JsonProperty("productName")
    private String productName;

    @NotNull(message = "Sales value is required")
    @PositiveOrZero(message = "Sales cannot be negative")
    private Double sales;

    @NotNull(message = "Units is required")
    @Positive(message = "Units must be positive")
    private Integer units;

    @NotNull(message = "Gross Profit is required")
    @JsonProperty("grossProfit")
    private Double grossProfit;

    @NotNull(message = "Cost is required")
    private Double cost;
}
