package com.backend.lavugio.dto.user;

import com.backend.lavugio.model.ride.Ride;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private Long rideId;

    @NotBlank
    private String startAddress;

    @NotBlank
    private String endAddress;

    @NotBlank
    private String startDate;

    @NotBlank
    private String endDate;

    public DriverHistoryDTO(Ride ride){
        this.rideId = ride.getId();
        this.startAddress = ride.getCheckpoints().getFirst().getAddress().toString();
        this.endAddress = ride.getCheckpoints().getLast().getAddress().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        this.startDate = ride.getStartDateTime().format(formatter);
        if (ride.getEndDateTime()!=null){
            this.endDate = ride.getEndDateTime().format(formatter);
        } else{
            this.endDate = "";
        }
    }
}
