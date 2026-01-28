package com.backend.lavugio.dto.pricing;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KilometerPricingDTO {
    @NotNull(message = "New pricing must not be null")
    Double newPricing;
}
