package com.backend.lavugio.dto;

import com.backend.lavugio.model.ride.Ride;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledRideDTO {
    private Long rideId;
    private String startAddress;
    private String endAddress;
    private String scheduledTime;
}
