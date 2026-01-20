package com.backend.lavugio.dto.pricing;

import com.backend.lavugio.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePricingDTO {
    VehicleType vehicleType;
    Double newPricing;
}
