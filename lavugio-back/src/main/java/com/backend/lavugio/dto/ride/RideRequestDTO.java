package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.vehicle.VehicleType;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestDTO {
    private List<RideDestinationDTO> stops;

    private List<String> passengerEmails;
    private VehicleType vehicleType;

    private boolean babyFriendly;
    private boolean petFriendly;

    private LocalDateTime scheduledTime;
    private boolean scheduled;

    public RideDestinationDTO getStartAddress() {
        return stops.getFirst();
    }
}
