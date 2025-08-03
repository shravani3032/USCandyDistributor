package com.shravani.CandyDistributor.service;

import com.shravani.CandyDistributor.dto.DivisionDTO;
import com.shravani.CandyDistributor.exception.ResourceNotFoundException;
import com.shravani.CandyDistributor.model.Division;
import com.shravani.CandyDistributor.repository.DivisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DivisionService {
    private final DivisionRepository repository;

    public List<DivisionDTO> getAllDivisions() {
        return repository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public DivisionDTO getDivisionById(Long id) {
        Division division = repository.findById(id).orElseThrow(() -> new RuntimeException("Division not found"));
        return convertToDTO(division);
    }

    public DivisionDTO createDivision(DivisionDTO dto) {
        Division division = convertToEntity(dto);
        Division saved = repository.save(division);
        return convertToDTO(saved);
    }

    public void deleteDivision(Long id) {
        repository.deleteById(id);
    }

    private DivisionDTO convertToDTO(Division entity) {
        DivisionDTO dto = new DivisionDTO();
        dto.setDivisionId(entity.getDivisionId());
        dto.setDivision(entity.getDivision());
        return dto;
    }

    private Division convertToEntity(DivisionDTO dto) {
        Division entity = new Division();
        entity.setDivisionId(dto.getDivisionId());
        entity.setDivision(dto.getDivision());
        return entity;
    }
    public DivisionDTO updateDivision(DivisionDTO dto) {
        Division existing = repository.findById(dto.getDivisionId()).orElseThrow(() -> new ResourceNotFoundException("Division not found"));
        existing.setDivision(dto.getDivision());
        repository.save(existing);
        return convertToDTO(existing);
    }
}
