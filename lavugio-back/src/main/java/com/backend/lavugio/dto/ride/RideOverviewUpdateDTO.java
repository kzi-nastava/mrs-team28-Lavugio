package com.backend.lavugio.dto.ride;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.ride.Ride;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
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

    @Valid
    @NotNull
    CoordinatesDTO destinationCoordinates;

    @PastOrPresent
    @NotNull
    LocalDateTime departureTime;

    LocalDateTime arrivalTime;

    @NotNull
    RideStatus status;

    @NotNull
    @Positive
    Double price;

    public RideOverviewUpdateDTO(String endAddress, CoordinatesDTO destinationCoordinates, Ride ride){
        this.endAddress = endAddress;
        this.destinationCoordinates = destinationCoordinates;
        this.departureTime = ride.getStartDateTime();
        this.arrivalTime = ride.getEndDateTime();
        this.status = ride.getRideStatus();
        this.price = (double) ride.getPrice();
    }

    public RideOverviewUpdateDTO(Ride ride){
        this.endAddress = ride.getEndAddress();
        this.destinationCoordinates = new CoordinatesDTO(ride.getCheckpoints().getLast().getAddress());
        this.departureTime = ride.getStartDateTime();
        this.arrivalTime = ride.getEndDateTime();
        this.status = ride.getRideStatus();
        this.price = (double) ride.getPrice();
    }
}
