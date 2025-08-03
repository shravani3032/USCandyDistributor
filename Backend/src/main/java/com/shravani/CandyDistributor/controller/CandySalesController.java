package com.shravani.CandyDistributor.controller;

import com.shravani.CandyDistributor.dto.CandySalesDTO;
import com.shravani.CandyDistributor.dto.ProductSalesDTO;
import com.shravani.CandyDistributor.dto.RegionSalesDTO;
import com.shravani.CandyDistributor.model.CandySales;
import com.shravani.CandyDistributor.service.CandySalesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class CandySalesController {
    private final CandySalesService service;
    @Operation(summary = "Get all sales (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of sales",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public Page<CandySalesDTO> getAllSales(
            @ParameterObject Pageable pageable,
            @Parameter(description = "Search term to filter sales by various fields")
            @RequestParam(required = false) String search,
            @Parameter(description = "Region to filter sales by")
            @RequestParam(required = false) String region,
            @Parameter(description = "Start date for order date range (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @Parameter(description = "End date for order date range (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return service.getAllSales(search, region, startDate, endDate, pageable);
    }

    @Operation(summary = "Get sale by sales ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the sale"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @GetMapping("/{salesId}")
    public CandySalesDTO getSale(
            @Parameter(description = "Sales ID of the sale to retrieve")
            @PathVariable Integer salesId) {
        return service.getSaleById(salesId);
    }

    @Operation(summary = "Create a new sale entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sale created successfully",
                    content = @Content(schema = @Schema(implementation = CandySalesDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<CandySalesDTO> createSale(
            @RequestBody @Valid CandySalesDTO dto) {
        CandySalesDTO created = service.createSale(dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Update a sale by sales ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale updated successfully"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @PutMapping("/{salesId}")
    public ResponseEntity<CandySalesDTO> updateSale(
            @Parameter(description = "Sales ID of the sale to update")
            @PathVariable Integer salesId,
            @RequestBody @Valid CandySalesDTO saleDTO) {
        saleDTO.setSalesId(salesId);
        CandySalesDTO updated = service.updateSale(saleDTO);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a sale by sales ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Sale not found")
    })
    @DeleteMapping("/{salesId}")
    public ResponseEntity<String> deleteSale(
            @Parameter(description = "Sales ID of the sale to delete")
            @PathVariable Integer salesId) {
        service.deleteSale(salesId);
        return ResponseEntity.ok("Sale deleted successfully.");
    }


    @GetMapping("/totalsalesbyregion")
    @Operation(summary = "Get total sales grouped by region")
    public List<RegionSalesDTO> getTotalSalesByRegion() {
        return service.getTotalSalesByRegion();
    }

    @GetMapping("/totalsalesbyproduct")
    @Operation(summary = "Get total sales grouped by product in a date range")
    public List<ProductSalesDTO> getTotalSalesByProductInRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate
    ) {
        return service.getTotalSalesByProductInRange(startDate, endDate);
    }

}



