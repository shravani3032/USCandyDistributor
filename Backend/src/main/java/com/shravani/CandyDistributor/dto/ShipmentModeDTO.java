package com.shravani.CandyDistributor.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShipmentModeDTO {

    @NotNull(message = "Ship ID is required")
    private Long shipId;

    @NotBlank(message = "Ship mode cannot be blank")
    private String shipMode;
}
