package com.shravani.CandyDistributor.controller;

import com.shravani.CandyDistributor.dto.CandyTargetDTO;
import com.shravani.CandyDistributor.model.CandyTarget;
import com.shravani.CandyDistributor.service.CandyTargetService;
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

import java.util.List;

@RestController
@RequestMapping("/api/targets")
@RequiredArgsConstructor
public class CandyTargetController {
    private final CandyTargetService service;

    @Operation(summary = "Get all targets (paginated) with optional search by division")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of targets",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public Page<CandyTargetDTO> getAllTargets(
            @RequestParam(required = false) String division, // Add this line
            @ParameterObject Pageable pageable) {
        return service.getAllTargets(division, pageable); // Pass the division parameter
    }

    @GetMapping("/{division}")
    public CandyTargetDTO getTarget(@PathVariable String division) {
        return service.getTargetByDivision(division);
    }

    @PostMapping
    public CandyTargetDTO createTarget(@RequestBody @Valid CandyTargetDTO dto) {
        return service.createTarget(dto);
    }

    @PutMapping("/{division}")
    public ResponseEntity<CandyTargetDTO> updateTarget(@PathVariable String division, @RequestBody @Valid CandyTargetDTO targetDTO) {
        targetDTO.setDivision(division);
        CandyTargetDTO updated = service.updateTarget(targetDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{division}")
    public void deleteTarget(@PathVariable String division) {
        service.deleteTarget(division);
    }
}