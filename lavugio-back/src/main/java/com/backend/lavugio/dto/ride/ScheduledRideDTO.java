package com.backend.lavugio.dto.ride;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.model.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledRideDTO {
    private Long rideId;
    private String startAddress;
    private String endAddress;
    private LocalDateTime scheduledTime;
    private CoordinatesDTO[] checkpoints;
    private Float price;
    private RideStatus status;
    private boolean isPanicked;
}
