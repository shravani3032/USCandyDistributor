package com.shravani.CandyDistributor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "shipment_mode")
@Getter
@Setter
public class ShipmentMode {
    @Id
    @Column(name = "shipID")
    private Long shipId;

    @Column(name = "ship_mode", nullable = false)
    private String shipMode;
}
