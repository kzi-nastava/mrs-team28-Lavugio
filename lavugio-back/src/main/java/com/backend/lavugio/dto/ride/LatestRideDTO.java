package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.enums.RideStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LatestRideDTO {
    @NotNull
    Long rideId;

    @NotNull(message = "Ride status must not be null")
    RideStatus status;
}
