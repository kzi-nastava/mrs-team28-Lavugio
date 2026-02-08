package com.backend.lavugio.dto.user;

import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminHistoryDTO {
    private Long rideId;
    private String startAddress;
    private String endAddress;
    private String startDate;
    private String endDate;
    private double price;
    private boolean cancelled;
    private String cancelledBy;
    private boolean panic;

    public AdminHistoryDTO(Ride ride) {
        this.rideId = ride.getId();
        this.startAddress = ride.getCheckpoints().getFirst().getAddress().toString();
        this.endAddress = ride.getCheckpoints().getLast().getAddress().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        this.startDate = ride.getStartDateTime().format(formatter);
        if (ride.getEndDateTime() != null) {
            this.endDate = ride.getEndDateTime().format(formatter);
        } else {
            this.endDate = "";
        }
        this.price = ride.getPrice();
        this.cancelled = ride.getRideStatus() == RideStatus.CANCELLED;
        this.cancelledBy = null; // Not tracked in current model
        this.panic = ride.isHasPanic();
    }
}
