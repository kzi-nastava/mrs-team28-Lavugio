package com.backend.lavugio.dto.ride;

import com.backend.lavugio.dto.CoordinatesDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinishRideDTO {
    private Long rideId;
    private CoordinatesDTO finalDestination;
    private String endTime;
    private boolean finishedEarly;
    private Double distance;
}
