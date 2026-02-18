package com.backend.lavugio.dto.user;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.route.RideDestination;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverHistoryDetailedDTO {
    @NotBlank
    private String start;

    @NotBlank
    private String end;

    @NotBlank
    private String departure;

    @NotBlank
    private String destination;

    @Positive
    private double price;

    private boolean cancelled;

    private boolean panic;

    @NotEmpty
    private List<PassengerTableRowDTO> passengers;

    @NotEmpty
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

    public DriverHistoryDetailedDTO(Ride ride){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        this.start = ride.getStartDateTime().format(formatter);
        this.end = ride.getEndDateTime() != null ? ride.getEndDateTime().format(formatter) : "not finished";
        this.departure = ride.getStartAddress();
        this.destination = ride.getEndAddress();
        this.price = ride.getPrice();
        this.cancelled = ride.getRideStatus() == RideStatus.CANCELLED;
        this.panic = ride.isHasPanic();
        this.checkpoints = ride.getCheckpoints().stream()
                .sorted(Comparator.comparing(RideDestination::getDestinationOrder))
                .map(checkpoint -> new CoordinatesDTO(
                        checkpoint.getAddress().getLatitude(),
                        checkpoint.getAddress().getLongitude()
                ))
                .toArray(CoordinatesDTO[]::new);
    }
}
