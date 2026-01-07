package com.backend.lavugio.dto;

import com.backend.lavugio.model.enums.RideStatus;
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

    private CoordinatesDTO startCoordinates;

    private CoordinatesDTO endCoordinates;

    private RideStatus status;

    private String driverName;

    private String startAddress;

    private String endAddress;

    private String departureTime;
}