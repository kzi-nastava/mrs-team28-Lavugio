package com.backend.lavugio.dto.pricing;

import com.backend.lavugio.model.enums.VehicleType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePricingDTO {
    @NotNull(message = "Vehicle type must not be null")
    VehicleType vehicleType;
    @NotNull(message = "New pricing must not be null")
    Double newPricing;
}
