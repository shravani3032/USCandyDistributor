package com.shravani.CandyDistributor.repository;

import com.shravani.CandyDistributor.dto.CandySalesAnalyticDTO;
import com.shravani.CandyDistributor.dto.SalesLocationDTO;
import com.shravani.CandyDistributor.model.CandySales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CandySalesAnalyticsRepository extends JpaRepository<CandySales, String> {

    @Query("SELECT cs.region, SUM(cs.sales) FROM CandySales cs GROUP BY cs.region")
    List<Object[]> getTotalSalesByRegion();

    // Custom query using @Query for total sales by product in range
    @Query("SELECT cs.productId, cs.productName, SUM(cs.sales) " +
            "FROM CandySales cs " +
            "WHERE cs.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY cs.productId, cs.productName")
    List<Object[]> getTotalSalesByProductInRange(Date startDate, Date endDate);

    @Query("SELECT YEAR(cs.orderDate), MONTH(cs.orderDate), SUM(cs.sales) " +
            "FROM CandySales cs " +
            "WHERE cs.orderDate >= :startDate " +
            "GROUP BY YEAR(cs.orderDate), MONTH(cs.orderDate) " +
            "ORDER BY YEAR(cs.orderDate) DESC, MONTH(cs.orderDate) DESC")
    List<Object[]> getLast12MonthsSales(@Param("startDate") Date startDate);


    @Query("SELECT cs.productName, SUM(cs.sales) " +
            "FROM CandySales cs " +
            "GROUP BY cs.productName " +
            "ORDER BY SUM(cs.sales) DESC LIMIT 10")
    List<Object[]> getSalesByProduct();

    @Query("SELECT new com.shravani.CandyDistributor.dto.CandySalesAnalyticDTO(cs.region, cs.productId, SUM(cs.sales), SUM(cs.units)) " +
            "FROM CandySales cs GROUP BY cs.region, cs.productId")
    List<CandySalesAnalyticDTO> getRegionProductSalesSummary();

    @Query("SELECT cs.division, SUM(cs.sales) as sum FROM CandySales cs GROUP BY cs.division ORDER BY sum DESC")
    List<Object[]> findTotalSalesByDivision();

    @Query("SELECT cs.division, SUM(cs.sales) FROM CandySales cs WHERE cs.division = :division GROUP BY cs.division")
    List<Object[]> findTotalSalesBySpecificDivision(@Param("division") String division);

//    @Query("SELECT SUM(cs.sales), COUNT(cs.orderId), AVG(cs.sales) FROM CandySales cs")
//    List<Object[]> getKpiValues();

    @Query("SELECT " +
            "ROUND(SUM(cs.sales), 2), " +
            "COUNT(cs), " +
            "(SELECT COALESCE(SUM(ct.target), 0) FROM CandyTarget ct), " +
            "CASE WHEN (SELECT SUM(ct.target) FROM CandyTarget ct) > 0 " +
            "THEN ROUND(SUM(cs.sales) * 100.0 / (SELECT SUM(ct.target) FROM CandyTarget ct), 2) " +
            "ELSE 0.0 END " +
            "FROM CandySales cs")
    List<Object[]> getSalesSummaryJPQL();

    @Query(value = """
        SELECT new com.shravani.CandyDistributor.dto.SalesLocationDTO(u.city,u.zip,u.lat, u.lng, ROUND(SUM(s.sales), 2))
        FROM CandySales s
        JOIN UsZip u ON s.postalCode = u.zip
        GROUP BY u.city,u.lat, u.lng,u.zip
        ORDER BY SUM(s.sales) DESC
       """)
    Page<SalesLocationDTO> findTopSalesByLocation(Pageable pageable);
}
