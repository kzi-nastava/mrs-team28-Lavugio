package com.backend.lavugio.dto.user;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.route.RideDestination;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverHistoryDetailedDTO {
    private String startDateTime;
    private String endDateTime;
    private String startLocation;
    private String destinationLocation;
    private double price;
    private boolean isCancelled;
    private boolean hasPanic;
    private List<PassengerTableRowDTO> passengers;
    private CoordinatesDTO startCoordinates;
    private CoordinatesDTO destinationCoordinates;

    public DriverHistoryDetailedDTO(Ride ride, RideDestination startLocation, RideDestination destinationLocation){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        this.startDateTime = ride.getStartDateTime().format(formatter);
        this.endDateTime = ride.getEndDateTime() != null ? ride.getEndDateTime().format(formatter) : "not finished";
        this.startLocation = startLocation.getAddress().toString();
        this.destinationLocation = destinationLocation.getAddress().toString();
        this.price = ride.getPrice();
        this.isCancelled = ride.getRideStatus() == RideStatus.CANCELLED;
        this.hasPanic = ride.isHasPanic();
        this.startCoordinates = new CoordinatesDTO(startLocation.getAddress());
        this.destinationCoordinates = new CoordinatesDTO(destinationLocation.getAddress());
    }
}
