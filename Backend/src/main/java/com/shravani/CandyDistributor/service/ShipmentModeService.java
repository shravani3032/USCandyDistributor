package com.shravani.CandyDistributor.service;

import com.shravani.CandyDistributor.dto.ShipmentModeDTO;
import com.shravani.CandyDistributor.exception.ResourceNotFoundException;
import com.shravani.CandyDistributor.model.ShipmentMode;
import com.shravani.CandyDistributor.repository.ShipmentModeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentModeService {
    private final ShipmentModeRepository repository;

    public List<ShipmentModeDTO> getAllShipmentModes() {
        return repository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ShipmentModeDTO getShipmentModeById(Long id) {
        ShipmentMode shipmentMode = repository.findById(id).orElseThrow(() -> new RuntimeException("Shipment Mode not found"));
        return convertToDTO(shipmentMode);
    }

    public ShipmentModeDTO createShipmentMode(ShipmentModeDTO dto) {
        ShipmentMode shipmentMode = convertToEntity(dto);
        ShipmentMode saved = repository.save(shipmentMode);
        return convertToDTO(saved);
    }

    public void deleteShipmentMode(Long id) {
        repository.deleteById(id);
    }

    private ShipmentModeDTO convertToDTO(ShipmentMode entity) {
        ShipmentModeDTO dto = new ShipmentModeDTO();
        dto.setShipId(entity.getShipId());
        dto.setShipMode(entity.getShipMode());
        return dto;
    }

    private ShipmentMode convertToEntity(ShipmentModeDTO dto) {
        ShipmentMode entity = new ShipmentMode();
        entity.setShipId(dto.getShipId());
        entity.setShipMode(dto.getShipMode());
        return entity;
    }
    public ShipmentModeDTO updateShipmentMode(ShipmentModeDTO dto) {
        ShipmentMode existing = repository.findById(dto.getShipId()).orElseThrow(() -> new ResourceNotFoundException("Shipment Mode not found"));
        existing.setShipMode(dto.getShipMode());
        repository.save(existing);
        return convertToDTO(existing);
    }
}
