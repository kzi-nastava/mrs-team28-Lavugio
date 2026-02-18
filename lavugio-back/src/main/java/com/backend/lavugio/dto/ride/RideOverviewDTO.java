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
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideOverviewDTO {
    @NotNull
    private Long rideId;

    @NotNull
    private Long driverId;

    @Positive
    private double price;

    @NotEmpty
    private CoordinatesDTO[] checkpoints;

    @NotNull
    private RideStatus status;

    @NotBlank
    private String driverName;

    @NotBlank
    private String startAddress;

    private String endAddress;

    @PastOrPresent
    @NotNull
    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    private boolean isReviewed;

    private boolean isReported;
    
    private boolean hasPanic;

    public RideOverviewDTO(Ride ride, List<CoordinatesDTO> coordinates, String start, String end, boolean isReviewed, boolean isReported) {
        this.rideId = ride.getId();
        this.driverId = ride.getDriver().getId();
        this.price = Math.round(ride.getPrice() * 100.0) / 100.0;
        this.checkpoints = coordinates.toArray(new CoordinatesDTO[0]);
        this.status = ride.getRideStatus();
        this.driverName = ride.getDriver().getName();
        this.startAddress = start;
        this.endAddress = end;
        this.departureTime = ride.getStartDateTime();
        this.arrivalTime = ride.getEndDateTime();
        this.isReviewed = isReviewed;
        this.isReported = isReported;
        this.hasPanic = ride.isHasPanic();
    }
}