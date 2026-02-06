package com.backend.lavugio.dto.pricing;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PricingDTO {

    @NotNull(message = "Standard price cannot be null")
    @Positive(message = "Standard price must be greater than 0")
    private Double standard;

    @NotNull(message = "Luxury price cannot be null")
    @Positive(message = "Luxury price must be greater than 0")
    private Double luxury;

    @NotNull(message = "Combi price cannot be null")
    @Positive(message = "Combi price must be greater than 0")
    private Double combi;

    @NotNull(message = "Kilometer price cannot be null")
    @Positive(message = "Kilometer price must be greater than 0")
    private Double kilometer;
}