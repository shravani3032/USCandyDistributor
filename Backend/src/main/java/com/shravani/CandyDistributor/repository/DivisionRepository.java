package com.shravani.CandyDistributor.repository;

import com.shravani.CandyDistributor.model.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Long> {
}

