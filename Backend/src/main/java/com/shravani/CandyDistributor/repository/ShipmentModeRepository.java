package com.shravani.CandyDistributor.repository;

import com.shravani.CandyDistributor.model.ShipmentMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipmentModeRepository extends JpaRepository<ShipmentMode, Long> {
}