package com.shravani.CandyDistributor.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandyTargetDTO {

    @NotBlank(message = "Division cannot be blank")
    private String division;

    @NotNull(message = "Target value is required")
    @Positive(message = "Target must be a positive number")
    private Integer target;
}
