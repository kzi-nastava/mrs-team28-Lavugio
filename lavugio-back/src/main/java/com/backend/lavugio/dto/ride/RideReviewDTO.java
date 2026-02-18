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
    @Min(value = 0, message = "Driver rating must be at least 0")
    @Max(value = 5, message = "Driver rating must be at most 5")
    private int driverRating;
    @Min(value = 0, message = "Vehicle rating must be at least 0")
    @Max(value = 5, message = "Vehicle rating must be at most 5")
    private int vehicleRating;
    private String comment;
}
