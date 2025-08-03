package com.shravani.CandyDistributor.repository;

import com.shravani.CandyDistributor.model.CandyProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandyProductRepository extends JpaRepository<CandyProduct, String> {
    Page<CandyProduct> findByNameContainingIgnoreCase(String name, Pageable pageable);
    boolean existsById(String id);
}
