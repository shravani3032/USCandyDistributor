package com.shravani.CandyDistributor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "divisions")
@Getter
@Setter
public class Division {
    @Id
    @Column(name = "divisionID")
    private Long divisionId;

    @Column(name = "division", nullable = false)
    private String division;
}