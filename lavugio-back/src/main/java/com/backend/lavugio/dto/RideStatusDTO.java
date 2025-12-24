package com.backend.lavugio.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideStatusDTO {
    private Long rideId;

    private CoordinatesDTO driverCoordinates;

    private CoordinatesDTO rideStartCoordinates;

    private CoordinatesDTO rideEndCoordinates;

    private double remainingTimeSeconds;
}