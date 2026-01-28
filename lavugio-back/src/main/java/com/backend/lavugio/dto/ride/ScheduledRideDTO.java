package com.backend.lavugio.dto.ride;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.ride.Ride;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    private Float distance;
    private RideStatus status;
    private boolean isPanicked;

    public ScheduledRideDTO(Ride ride, List<CoordinatesDTO> checkpoints, String start, String end) {
        this.rideId = ride.getId();
        this.startAddress = start;
        this.endAddress = end;
        this.scheduledTime = ride.getStartDateTime();
        this.checkpoints = checkpoints.toArray(new CoordinatesDTO[0]);
        this.price = ride.getPrice();
        this.distance = ride.getDistance();
        this.status = ride.getRideStatus();
        this.isPanicked = ride.isHasPanic();
    }
}
