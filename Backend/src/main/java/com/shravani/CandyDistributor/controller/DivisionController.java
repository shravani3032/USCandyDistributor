package com.shravani.CandyDistributor.controller;

import com.shravani.CandyDistributor.dto.DivisionDTO;
import com.shravani.CandyDistributor.model.Division;
import com.shravani.CandyDistributor.service.DivisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/divisions")
@RequiredArgsConstructor
public class DivisionController {
    private final DivisionService service;

    @Operation(summary = "Get all product divisions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of product divisions",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public List<DivisionDTO> getAllDivisions() {
        return service.getAllDivisions();
    }

    @GetMapping("/{id}")
    public DivisionDTO getDivision(@PathVariable Long id) {
        return service.getDivisionById(id);
    }

    @PostMapping
    public DivisionDTO createDivision(@RequestBody @Valid DivisionDTO dto) {
        return service.createDivision(dto);
    }

    @PutMapping("/{divisionId}")
    public ResponseEntity<DivisionDTO> updateDivision(@PathVariable Long divisionId, @RequestBody @Valid DivisionDTO divisionDTO) {
        divisionDTO.setDivisionId(divisionId);
        DivisionDTO updated = service.updateDivision(divisionDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteDivision(@PathVariable Long id) {
        service.deleteDivision(id);
    }
}

