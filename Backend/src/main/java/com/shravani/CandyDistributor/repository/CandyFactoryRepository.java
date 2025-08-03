package com.shravani.CandyDistributor.repository;


import com.shravani.CandyDistributor.dto.CandyFactoryDTO;
import com.shravani.CandyDistributor.model.CandyFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandyFactoryRepository extends JpaRepository<CandyFactory, String> {
    Page<CandyFactory> findByFactoryContainingIgnoreCase(String factory, Pageable pageable);
}
