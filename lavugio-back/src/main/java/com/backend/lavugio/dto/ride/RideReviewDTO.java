package com.backend.lavugio.dto.ride;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RideReviewDTO {
    @Max(value = 5)
    @Min(value = 1)
    private int driverRating;

    @Max(value = 5)
    @Min(value = 1)
    private int vehicleRating;

    private String comment;
}
