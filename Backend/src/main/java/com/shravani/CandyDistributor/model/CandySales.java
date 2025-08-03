package com.shravani.CandyDistributor.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "candy_sales")
@Getter
@Setter
public class CandySales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // <--- ADD THIS LINE FOR AUTO-INCREMENT
    @Column(name = "sales_id") // Remove nullable = false if it's auto-generated
    private Integer salesId; // Remove @JsonProperty here, it's not needed for internal model

    @Column(name = "order_id", nullable = false)
    private String orderId;


    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "order_date", nullable = false)
    private Date orderDate;

    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "ship_date", nullable = false)
    private Date shipDate;

    @Column(name = "ship_mode", nullable = false)
    private String shipMode;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "country/region", nullable = false)
    private String countryRegion;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state/province", nullable = false)
    private String stateProvince;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(name = "division", nullable = false)
    private String division;

    @Column(name = "region", nullable = false)
    private String region;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "sales", nullable = false)
    private Double sales;

    @Column(name = "units", nullable = false)
    private Integer units;

    @Column(name = "gross_profit", nullable = false)
    private Double grossProfit;

    @Column(name = "cost", nullable = false)
    private Double cost;
}