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
    private String start;
    private String end;
    private String departure;
    private String destination;
    private double price;
    private boolean cancelled;
    private boolean panic;
    private List<PassengerTableRowDTO> passengers;
    private CoordinatesDTO[] checkpoints;

    public DriverHistoryDetailedDTO(Ride ride, RideDestination departure, RideDestination destination, CoordinatesDTO[] checkpoints){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        this.start = ride.getStartDateTime().format(formatter);
        this.end = ride.getEndDateTime() != null ? ride.getEndDateTime().format(formatter) : "not finished";
        this.departure = departure.getAddress().toString();
        this.destination = destination.getAddress().toString();
        this.price = ride.getPrice();
        this.cancelled = ride.getRideStatus() == RideStatus.CANCELLED;
        this.panic = ride.isHasPanic();
        this.checkpoints = checkpoints;
    }
}
