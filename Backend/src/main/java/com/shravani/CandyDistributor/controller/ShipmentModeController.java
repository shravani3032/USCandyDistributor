package com.shravani.CandyDistributor.controller;

import com.shravani.CandyDistributor.dto.ShipmentModeDTO;
import com.shravani.CandyDistributor.model.ShipmentMode;
import com.shravani.CandyDistributor.service.ShipmentModeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@RestController
@RequestMapping("/api/shipmentModes")
@RequiredArgsConstructor
public class ShipmentModeController {
    private final ShipmentModeService service;

    @Operation(summary = "Get all shipment modes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of shipment modes",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<ShipmentModeDTO> getAllShipmentModes() {
        return service.getAllShipmentModes();
    }

    @GetMapping("/{id}")
    public ShipmentModeDTO getShipmentMode(@PathVariable Long id) {
        return service.getShipmentModeById(id);
    }

    @PostMapping
    public ShipmentModeDTO createShipmentMode(@RequestBody @Valid ShipmentModeDTO dto) {
        return service.createShipmentMode(dto);
    }
    @PutMapping("/{shipId}")
    public ResponseEntity<ShipmentModeDTO> updateShipmentMode(@PathVariable Long shipId, @RequestBody @Valid ShipmentModeDTO shipmentModeDTO) {
        shipmentModeDTO.setShipId(shipId);
        ShipmentModeDTO updated = service.updateShipmentMode(shipmentModeDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteShipmentMode(@PathVariable Long id) {
        service.deleteShipmentMode(id);
    }
}

