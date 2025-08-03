package com.shravani.CandyDistributor.controller;

import com.shravani.CandyDistributor.dto.CandyFactoryDTO;
import com.shravani.CandyDistributor.service.CandyFactoryService; // Make sure service import is correct
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/factories")
@RequiredArgsConstructor
public class CandyFactoryController {

    private final CandyFactoryService service;
    @Operation(summary = "Get all candy factories (paginated) with optional search by factory name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of candy factories",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping // Added a path for clarity
    public Page<CandyFactoryDTO> getFactories(
            @ParameterObject Pageable pageable,
            @Parameter(description = "Search term for candy factory name (case-insensitive)", example = "Sweet")
            @RequestParam(required = false) String factory) { // <--- Added @RequestParam for the search term
        return service.getAllFactories(factory, pageable);
    }

    @GetMapping("/{factory}")
    public CandyFactoryDTO getFactory(@PathVariable String factory) {
        return service.getFactoryById(factory);
    }

    @PostMapping
    public CandyFactoryDTO createFactory(@RequestBody @Valid CandyFactoryDTO dto) {
        return service.createFactory(dto);
    }
    @PutMapping("/{factory}")
    public ResponseEntity<CandyFactoryDTO> updateFactory(@PathVariable String factory, @RequestBody @Valid CandyFactoryDTO factoryDTO)
    {
        factoryDTO.setFactory(factory);
        CandyFactoryDTO updated = service.updateFactory(factoryDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{factory}")
    public void deleteFactory(@PathVariable String factory) {
        service.deleteFactory(factory);
    }
}
