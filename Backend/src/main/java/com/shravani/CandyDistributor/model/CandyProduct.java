package com.shravani.CandyDistributor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Table(name = "candy_products")
@Data
public class CandyProduct {
    @Id
    @Column(name = "product_id")
    private String id;

    @Column(name = "product_name")
    @Pattern(regexp = ".*[a-zA-Z]+.*", message = "Product name must contain at least one alphabet character")
    private String name;

    @Column(name = "division")
    private String division;

    @Column(name = "factory")
    private String factory;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "unit_cost")
    private Double unitCost;
}
