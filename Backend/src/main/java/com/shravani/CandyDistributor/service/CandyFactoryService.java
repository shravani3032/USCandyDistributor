package com.shravani.CandyDistributor.service;

import com.shravani.CandyDistributor.dto.CandyFactoryDTO;
import com.shravani.CandyDistributor.exception.ResourceNotFoundException;
import com.shravani.CandyDistributor.model.CandyFactory;
import com.shravani.CandyDistributor.repository.CandyFactoryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandyFactoryService {
    private final CandyFactoryRepository repository;



    public CandyFactoryDTO getFactoryById(String factory_name) {
        CandyFactory factory = repository.findById(factory_name).orElseThrow(() -> new RuntimeException("Factory not found"));
        return convertToDTO(factory);
    }

    public Page<CandyFactoryDTO> getAllFactories(String factory, Pageable pageable) {
        Page<CandyFactory> factories;
        if (factory != null && !factory.isBlank()) {
            factories = repository.findByFactoryContainingIgnoreCase(factory, pageable);
        } else {
            factories = repository.findAll(pageable);
        }
        return factories.map(this::convertToDTO);
    }

    public CandyFactoryDTO createFactory(CandyFactoryDTO dto) {
        CandyFactory factory = convertToEntity(dto);
        CandyFactory saved = repository.save(factory);
        return convertToDTO(saved);
    }

    public void deleteFactory(String factory_name) {
        repository.deleteById(factory_name);
    }

    private CandyFactoryDTO convertToDTO(CandyFactory entity) {
        CandyFactoryDTO dto = new CandyFactoryDTO();
        dto.setFactory(entity.getFactory());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        return dto;
    }

    private CandyFactory convertToEntity(CandyFactoryDTO dto) {
        CandyFactory entity = new CandyFactory();
        entity.setFactory(dto.getFactory());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        return entity;
    }
    public CandyFactoryDTO updateFactory(CandyFactoryDTO dto) {
        CandyFactory existing = repository.findById(dto.getFactory()).orElseThrow(() -> new ResourceNotFoundException("Factory not found"));
        existing.setFactory(dto.getFactory());
        existing.setLatitude(dto.getLatitude());
        existing.setLongitude(dto.getLongitude());
        repository.save(existing);
        return convertToDTO(existing);
    }
}
