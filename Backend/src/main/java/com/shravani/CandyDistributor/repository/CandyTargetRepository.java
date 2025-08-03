package com.shravani.CandyDistributor.repository;

import com.shravani.CandyDistributor.model.CandyTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandyTargetRepository extends JpaRepository<CandyTarget, String> {

    // For partial + paginated search
    Page<CandyTarget> findByDivisionContainingIgnoreCase(String division, Pageable pageable);

    // For exact match (used in analytics service)
    Optional<CandyTarget> findByDivision(String division);
}
