package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.enums.VehicleType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RideEstimateRequestDTO {
    @NotEmpty(message = "Vehicle type is required")
    private String selectedVehicleType;
    
    @NotNull(message = "Distance is required")
    @Positive(message = "Distance must be positive")
    private float distanceMeters;
}
