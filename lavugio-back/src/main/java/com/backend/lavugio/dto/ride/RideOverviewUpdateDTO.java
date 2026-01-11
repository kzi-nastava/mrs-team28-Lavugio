package com.backend.lavugio.dto.ride;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.model.enums.RideStatus;

import java.time.LocalDateTime;

public class RideOverviewUpdateDTO {
    String destination;

    CoordinatesDTO destinationCoordinates;

    LocalDateTime departureTime;

    LocalDateTime arrivalTime;

    RideStatus status;

    Double price;
}
