package com.backend.lavugio.dto.user;

import com.backend.lavugio.model.ride.Ride;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserHistoryDTO {
    private Long rideId;
    private String startAddress;
    private String endAddress;
    private String startDate;
    private String endDate;

    public UserHistoryDTO(Ride ride) {
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
    }
}
