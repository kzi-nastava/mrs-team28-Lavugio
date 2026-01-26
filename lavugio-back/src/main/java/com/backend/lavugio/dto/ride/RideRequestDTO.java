package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.enums.VehicleType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestDTO {
    private List<RideDestinationDTO> destinations;

    private List<String> passengerEmails;
    private VehicleType vehicleType;

    private boolean babyFriendly;
    private boolean petFriendly;

    private LocalDateTime scheduledTime;
    private boolean scheduled;

    public RideDestinationDTO getStartAddress() {
        return destinations.getFirst();
    }
}
