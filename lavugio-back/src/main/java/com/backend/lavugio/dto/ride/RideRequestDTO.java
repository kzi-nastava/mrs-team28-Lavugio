package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.vehicle.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestDTO {
    private BaseRideRequestDTO baseRideRequestDTO;

    private List<String> passengerEmails;

    private boolean babyFriendly;
    private boolean petFriendly;

    private LocalDateTime scheduledTime;

    private boolean scheduled;

    public RideRequstDestinationDTO getStartAddress() {
        return baseRideRequestDTO.getStops().getFirst();
    }
}
