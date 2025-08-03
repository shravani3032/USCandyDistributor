package com.shravani.CandyDistributor.model;

import com.shravani.CandyDistributor.Converter.CountyWeightsConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "uszips")
@Getter
@Setter
public class UsZip {
    @Id
    @Column(name = "zip", nullable = false)
    private String zip;

    @Column(name = "lat", nullable = false)
    private Double lat;

    @Column(name = "lng", nullable = false)
    private Double lng;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "state_id", nullable = false)
    private String stateId;

    @Column(name = "state_name", nullable = false)
    private String stateName;

    @Column(name = "zcta", nullable = false)
    private String zcta;

    @Column(name = "parent_zcta", nullable = true)
    private String parentZcta;

    @Column(name = "population", nullable = false)
    private Integer population;

    @Column(name = "density", nullable = false)
    private Double density;

    @Column(name = "county_fips", nullable = false)
    private String countyFips;

    @Column(name = "county_name", nullable = false)
    private String countyName;

    @Convert(converter = CountyWeightsConverter.class)
    @Column(name = "county_weights", columnDefinition = "JSON", nullable = false)
    private Map<String, Double> countyWeights;

    @Column(name = "county_names_all", nullable = false)
    private String countyNamesAll;

    @Column(name = "county_fips_all", nullable = false)
    private String countyFipsAll;

    @Column(name = "imprecise", nullable = false)
    private Boolean imprecise;

    @Column(name = "military", nullable = false)
    private Boolean military;

    @Column(name = "timezone", nullable = false)
    private String timezone;
}