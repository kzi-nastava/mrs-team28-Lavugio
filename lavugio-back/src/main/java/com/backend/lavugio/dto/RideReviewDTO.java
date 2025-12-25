package com.backend.lavugio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RideReviewDTO {
    private int driverRating;
    private int vehicleRating;
    private String comment;
}
