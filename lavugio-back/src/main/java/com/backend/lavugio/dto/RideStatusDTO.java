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

    private double currentLatitude;
    private double currentLongitude;

    private double startLatitude;
    private double startLongitude;

    private double destinationLatitude;
    private double destinationLongitude;

    private double remainingTimeSeconds;
}