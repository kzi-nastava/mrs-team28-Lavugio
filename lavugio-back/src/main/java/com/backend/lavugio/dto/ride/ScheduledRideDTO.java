package com.backend.lavugio.dto.ride;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.ride.Ride;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledRideDTO {
    @NotNull
    private Long rideId;

    @NotBlank
    private String startAddress;

    @NotBlank
    private String endAddress;

    @PastOrPresent
    private LocalDateTime scheduledTime;

    @NotEmpty
    private List<CoordinatesDTO> checkpoints;

    @Positive
    private Float price;

    @Positive
    private Float distance;

    @NotNull
    private RideStatus status;

    private boolean isPanicked;

    public ScheduledRideDTO(Ride ride, List<CoordinatesDTO> checkpoints, String start, String end) {
        this.rideId = ride.getId();
        this.startAddress = start;
        this.endAddress = end;
        this.scheduledTime = ride.getStartDateTime();
        this.checkpoints = checkpoints;
        this.price = ride.getPrice();
        this.distance = ride.getDistance();
        this.status = ride.getRideStatus();
        this.isPanicked = ride.isHasPanic();
    }

    public ScheduledRideDTO(Ride ride, RideRequestDTO request){
        this.rideId = ride.getId();
        this.startAddress = request.getStartAddress().getAddress();
        this.endAddress = request.getEndAddress().getAddress();
        this.scheduledTime = ride.getStartDateTime();
        this.checkpoints = new ArrayList<>();
        List<RideDestinationDTO> destinations = request.getDestinations();
        for (RideDestinationDTO destination : destinations){
            CoordinatesDTO coords = new CoordinatesDTO(destination.getLocation().getLatitude(), destination.getLocation().getLongitude());
            this.checkpoints.add(coords);
        }
        this.price = ride.getPrice();
        this.distance = ride.getDistance();
        this.status = ride.getRideStatus();
        this.isPanicked = ride.isHasPanic();
    }
}
