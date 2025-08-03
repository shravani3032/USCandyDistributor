package com.shravani.CandyDistributor.repository;

import com.shravani.CandyDistributor.dto.ProductSalesDTO;
import com.shravani.CandyDistributor.dto.RegionSalesDTO;
import com.shravani.CandyDistributor.model.CandySales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query; // Import Query
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
// No need to extend CandySalesRepositoryCustom anymore
public interface CandySalesRepository extends JpaRepository<CandySales, Integer>, JpaSpecificationExecutor<CandySales> {

    Page<CandySales> findByRegionAndOrderDateBetween(String region, Date startDate, Date endDate, Pageable pageable);
    Page<CandySales> findByRegion(String region, Pageable pageable);

    // Custom query using @Query for total sales by region
    @Query("SELECT new com.shravani.CandyDistributor.dto.RegionSalesDTO(c.region, SUM(c.sales)) " +
            "FROM CandySales c GROUP BY c.region")
    List<RegionSalesDTO> getTotalSalesByRegion();

    @Query("SELECT new com.shravani.CandyDistributor.dto.ProductSalesDTO(c.productId, SUM(c.sales)) " +
            "FROM CandySales c WHERE c.orderDate BETWEEN :start AND :end GROUP BY c.productId")
    List<ProductSalesDTO> getTotalSalesByProductInRange(Date start, Date end);

}