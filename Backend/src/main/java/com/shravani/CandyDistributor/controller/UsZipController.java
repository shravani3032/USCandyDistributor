package com.shravani.CandyDistributor.controller;

import com.shravani.CandyDistributor.dto.UsZipDTO;
import com.shravani.CandyDistributor.model.UsZip;
import com.shravani.CandyDistributor.service.UsZipService;
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
@RequestMapping("/api/uszips")
@RequiredArgsConstructor
public class UsZipController {
    private final UsZipService service;

    @Operation(summary = "Get all US ZIP entries (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of ZIP entries",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public Page<UsZipDTO> getAllZips(@ParameterObject Pageable pageable) {
        return service.getAllZips(null, pageable);
    }


    @Operation(summary = "Get zip details by zip code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zip code details",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Zip code not found")
    })
    @GetMapping("/{zip}")
    public UsZipDTO getZip(@PathVariable @Parameter(description = "ZIP code") String zip) {
        return service.getZipById(zip);
    }

    @Operation(summary = "Create a new ZIP entry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ZIP created successfully",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public UsZipDTO createZip(@RequestBody @Valid UsZipDTO dto) {
        return service.createZip(dto);
    }

    @Operation(summary = "Update a ZIP entry by zip code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ZIP updated successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "ZIP not found")
    })
    @PutMapping("/{zip}")
    public ResponseEntity<UsZipDTO> updateZip(
            @PathVariable @Parameter(description = "ZIP code to update") String zip,
            @RequestBody @Valid UsZipDTO zipDTO) {
        zipDTO.setZip(zip);
        UsZipDTO updated = service.updateZip(zipDTO);
        return ResponseEntity.ok(updated);
    }


    @Operation(summary = "Delete a ZIP entry by zip code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ZIP deleted successfully"),
            @ApiResponse(responseCode = "404", description = "ZIP not found")
    })
    @DeleteMapping("/{zip}")
    public void deleteZip(@PathVariable @Parameter(description = "ZIP code to delete") String zip) {
        service.deleteZip(zip);
    }
}
