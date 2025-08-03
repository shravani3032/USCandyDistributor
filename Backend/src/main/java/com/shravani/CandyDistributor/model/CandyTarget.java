package com.shravani.CandyDistributor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "candy_targets")
@Getter
@Setter
public class CandyTarget {
    @Id
    @Column(name = "division", nullable = false)
    private String division;

    @Column(name = "target", nullable = false)
    private Integer target;
}
