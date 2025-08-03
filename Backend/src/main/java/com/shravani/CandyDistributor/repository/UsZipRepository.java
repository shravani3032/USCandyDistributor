package com.shravani.CandyDistributor.repository;

import com.shravani.CandyDistributor.model.UsZip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsZipRepository extends JpaRepository<UsZip, String> {
    Page<UsZip> findByStateIdContainingIgnoreCase(String stateId, Pageable pageable);
}