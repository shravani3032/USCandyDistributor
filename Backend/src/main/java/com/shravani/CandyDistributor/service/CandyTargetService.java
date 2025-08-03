package com.shravani.CandyDistributor.service;

import com.shravani.CandyDistributor.dto.CandyTargetDTO;
import com.shravani.CandyDistributor.exception.ResourceNotFoundException;
import com.shravani.CandyDistributor.model.CandyTarget;
import com.shravani.CandyDistributor.repository.CandyTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CandyTargetService {
    private final CandyTargetRepository repository;

//    public List<CandyTargetDTO> getAllTargets() {
//        return repository.findAll().stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//    }

    public Page<CandyTargetDTO> getAllTargets(String division, Pageable pageable) {
        Page<CandyTarget> targets;
        if (division != null && !division.isBlank()) {
            targets = repository.findByDivisionContainingIgnoreCase(division, pageable);
        } else {
            targets = repository.findAll(pageable);
        }
        return targets.map(this::convertToDTO);
    }

    public CandyTargetDTO getTargetByDivision(String division) {
        CandyTarget target = repository.findById(division)
                .orElseThrow(() -> new RuntimeException("Target not found"));
        return convertToDTO(target);
    }

    public CandyTargetDTO createTarget(CandyTargetDTO dto) {
        CandyTarget target = convertToEntity(dto);
        CandyTarget saved = repository.save(target);
        return convertToDTO(saved);
    }

    public void deleteTarget(String division) {
        repository.deleteById(division);
    }

    private CandyTargetDTO convertToDTO(CandyTarget entity) {
        CandyTargetDTO dto = new CandyTargetDTO();
        dto.setDivision(entity.getDivision());
        dto.setTarget(entity.getTarget());
        return dto;
    }

    private CandyTarget convertToEntity(CandyTargetDTO dto) {
        CandyTarget entity = new CandyTarget();
        entity.setDivision(dto.getDivision());
        entity.setTarget(dto.getTarget());
        return entity;
    }
    public CandyTargetDTO updateTarget(CandyTargetDTO dto) {
        CandyTarget existing = repository.findById(dto.getDivision()).orElseThrow(() -> new ResourceNotFoundException("Target not found"));
        existing.setTarget(dto.getTarget());
        repository.save(existing);
        return convertToDTO(existing);
    }
}
