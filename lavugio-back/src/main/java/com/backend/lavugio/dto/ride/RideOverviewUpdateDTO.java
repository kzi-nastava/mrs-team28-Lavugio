package com.backend.lavugio.dto.ride;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.model.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RideOverviewUpdateDTO {
    String endAddress;

    CoordinatesDTO destinationCoordinates;

    LocalDateTime departureTime;

    LocalDateTime arrivalTime;

    RideStatus status;

    Double price;
}
