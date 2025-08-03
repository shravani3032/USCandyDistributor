package com.shravani.CandyDistributor.service;

import com.shravani.CandyDistributor.dto.UsZipDTO;
import com.shravani.CandyDistributor.exception.ResourceNotFoundException;
import com.shravani.CandyDistributor.model.UsZip;
import com.shravani.CandyDistributor.repository.UsZipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsZipService {
    private final UsZipRepository repository;

//    public List<UsZipDTO> getAllZips() {
//        return repository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
//    }

    public Page<UsZipDTO> getAllZips(String stateId, Pageable pageable) {
        Page<UsZip> zips;
        if (stateId != null && !stateId.isBlank()) {
            zips = repository.findByStateIdContainingIgnoreCase(stateId, pageable);
        } else {
            zips = repository.findAll(pageable);
        }
        return zips.map(this::convertToDTO);
    }


    public UsZipDTO getZipById(String zip) {
        UsZip usZip = repository.findById(zip).orElseThrow(() -> new RuntimeException("Zip not found"));
        return convertToDTO(usZip);
    }

    public UsZipDTO createZip(UsZipDTO dto) {
        UsZip usZip = convertToEntity(dto);
        UsZip saved = repository.save(usZip);
        return convertToDTO(saved);
    }

    public void deleteZip(String zip) {
        repository.deleteById(zip);
    }

    private UsZipDTO convertToDTO(UsZip entity) {
        UsZipDTO dto = new UsZipDTO();
        dto.setZip(entity.getZip());
        dto.setLat(entity.getLat());
        dto.setLng(entity.getLng());
        dto.setCity(entity.getCity());
        dto.setStateId(entity.getStateId());
        dto.setStateName(entity.getStateName());
        dto.setZcta(entity.getZcta());
        dto.setParentZcta(entity.getParentZcta());
        dto.setPopulation(entity.getPopulation());
        dto.setDensity(entity.getDensity());
        dto.setCountyFips(entity.getCountyFips());
        dto.setCountyName(entity.getCountyName());
        dto.setCountyWeights(entity.getCountyWeights());
        dto.setCountyNamesAll(entity.getCountyNamesAll());
        dto.setCountyFipsAll(entity.getCountyFipsAll());
        dto.setImprecise(entity.getImprecise());
        dto.setMilitary(entity.getMilitary());
        dto.setTimezone(entity.getTimezone());
        return dto;
    }

    private UsZip convertToEntity(UsZipDTO dto) {
        UsZip entity = new UsZip();
        entity.setZip(dto.getZip());
        entity.setLat(dto.getLat());
        entity.setLng(dto.getLng());
        entity.setCity(dto.getCity());
        entity.setStateId(dto.getStateId());
        entity.setStateName(dto.getStateName());
        entity.setZcta(dto.getZcta());
        entity.setParentZcta(dto.getParentZcta());
        entity.setPopulation(dto.getPopulation());
        entity.setDensity(dto.getDensity());
        entity.setCountyFips(dto.getCountyFips());
        entity.setCountyName(dto.getCountyName());
        entity.setCountyWeights(dto.getCountyWeights());
        entity.setCountyNamesAll(dto.getCountyNamesAll());
        entity.setCountyFipsAll(dto.getCountyFipsAll());
        entity.setImprecise(dto.getImprecise());
        entity.setMilitary(dto.getMilitary());
        entity.setTimezone(dto.getTimezone());
        return entity;
    }
    public UsZipDTO updateZip(UsZipDTO dto) {
        UsZip existing = repository.findById(dto.getZip()).orElseThrow(() -> new ResourceNotFoundException("Zip not found"));
        existing.setLat(dto.getLat());
        existing.setLng(dto.getLng());
        existing.setCity(dto.getCity());
        existing.setStateId(dto.getStateId());
        existing.setStateName(dto.getStateName());
        existing.setZcta(dto.getZcta());
        existing.setParentZcta(dto.getParentZcta());
        existing.setPopulation(dto.getPopulation());
        existing.setDensity(dto.getDensity());
        existing.setCountyFips(dto.getCountyFips());
        existing.setCountyName(dto.getCountyName());
        existing.setCountyWeights(dto.getCountyWeights());
        existing.setCountyNamesAll(dto.getCountyNamesAll());
        existing.setCountyFipsAll(dto.getCountyFipsAll());
        existing.setImprecise(dto.getImprecise());
        existing.setMilitary(dto.getMilitary());
        existing.setTimezone(dto.getTimezone());
        repository.save(existing);
        return convertToDTO(existing);
    }

}

