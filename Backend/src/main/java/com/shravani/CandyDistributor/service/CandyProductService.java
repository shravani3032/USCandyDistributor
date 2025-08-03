package com.shravani.CandyDistributor.service;

import com.shravani.CandyDistributor.dto.CandyProductDTO;
import com.shravani.CandyDistributor.exception.ResourceNotFoundException;
import com.shravani.CandyDistributor.model.CandyProduct;
import com.shravani.CandyDistributor.repository.CandyProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CandyProductService {

    private final CandyProductRepository repository;



    public CandyProductDTO getProductById(String id) {
        CandyProduct product = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return convertToDTO(product);
    }

    public Page<CandyProductDTO> getAllProducts(String name, Pageable pageable) {
        Page<CandyProduct> products;
        if (name != null && !name.isBlank()) {
            products = repository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            products = repository.findAll(pageable);
        }
        return products.map(this::convertToDTO);
    }

    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    public CandyProductDTO createProduct(CandyProductDTO dto) {
        CandyProduct product = convertToEntity(dto);
        return convertToDTO(repository.save(product));
    }

    public CandyProductDTO updateProduct(CandyProductDTO dto) {
        CandyProduct existing = repository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        existing.setName(dto.getName());
        existing.setDivision(dto.getDivision());
        existing.setFactory(dto.getFactory());
        existing.setUnitPrice(dto.getUnitPrice());
        existing.setUnitCost(dto.getUnitCost());

        return convertToDTO(repository.save(existing));
    }

    public void deleteProduct(String id) {
        CandyProduct product = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        repository.delete(product);
    }

    private CandyProductDTO convertToDTO(CandyProduct entity) {
        CandyProductDTO dto = new CandyProductDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDivision(entity.getDivision());
        dto.setFactory(entity.getFactory());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setUnitCost(entity.getUnitCost());
        return dto;
    }

    private CandyProduct convertToEntity(CandyProductDTO dto) {
        CandyProduct entity = new CandyProduct();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDivision(dto.getDivision());
        entity.setFactory(dto.getFactory());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setUnitCost(dto.getUnitCost());
        return entity;
    }
}
