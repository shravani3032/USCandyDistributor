package com.shravani.CandyDistributor.controller;

import com.shravani.CandyDistributor.dto.CandyProductDTO;
import com.shravani.CandyDistributor.service.CandyProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class CandyProductController {

    private final CandyProductService service;

    @GetMapping("/{id}")
    public CandyProductDTO getProduct(@PathVariable String id) {
        return service.getProductById(id);
    }

    @Operation(summary = "Get all products (paginated) with optional name search")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public Page<CandyProductDTO> getAllProducts(
            @RequestParam(required = false) String name, // Add this line for the search term
            @ParameterObject Pageable pageable) {
        return service.getAllProducts(name, pageable);
    }

    @GetMapping("/check-id")
    public ResponseEntity<Map<String, Boolean>> checkProductIdExists(@RequestParam String id) {
        boolean exists = service.existsById(id);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }

    @PostMapping
    public ResponseEntity<CandyProductDTO> createProduct(@RequestBody @Valid CandyProductDTO dto) {
        CandyProductDTO createdProduct = service.createProduct(dto);
        return ResponseEntity.ok(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandyProductDTO> updateProduct(@PathVariable String id, @RequestBody @Valid CandyProductDTO productDTO) {
        productDTO.setId(id); // Ensure ID consistency
        CandyProductDTO updatedProduct = service.updateProduct(productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        service.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully.");
    }
}
