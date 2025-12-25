package com.backend.lavugio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetRideReviewDTO {
    private Long reviewId;
    private int vehicleRating;
    private int driverRating;
    private String comment;
}
