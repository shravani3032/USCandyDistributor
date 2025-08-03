package com.shravani.CandyDistributor.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DivisionDTO {

    @NotNull(message = "Division ID is required")
    private Long divisionId;

    @NotBlank(message = "Division name cannot be blank")
    private String division;
}