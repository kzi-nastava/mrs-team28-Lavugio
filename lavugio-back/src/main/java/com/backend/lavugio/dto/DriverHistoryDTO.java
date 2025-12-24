package com.backend.lavugio.dto;

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
public class DriverHistoryDTO {
    private Long rideId;
    private String startAddress;
    private String endAddress;
    private String startDate;
    private String endDate;

    public DriverHistoryDTO(Ride ride){
        this.rideId = ride.getId();
        this.startAddress = ride.getStartDateTime().toString();
        this.endAddress = ride.getEndDateTime().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        this.startDate = ride.getStartDateTime().format(formatter);
        this.endDate = ride.getEndDateTime().format(formatter);
    }
}
