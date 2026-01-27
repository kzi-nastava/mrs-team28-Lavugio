package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LatestRideDTO {
    Long rideId;
    RideStatus status;
}
