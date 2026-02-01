package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.enums.VehicleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestDTO {
    @NotNull(message = "Destinations are required")
    @Size(min = 2, message = "At least start and end destinations are required")
    @Valid
    private List<RideDestinationDTO> destinations;

    private List<String> passengerEmails;
    
    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    private boolean babyFriendly;
    private boolean petFriendly;

    @FutureOrPresent(message = "Scheduled time must be in the future or present")
    private LocalDateTime scheduledTime;
    private boolean scheduled;

    public RideDestinationDTO getStartAddress() {
        return destinations.getFirst();
    }

    private int estimatedDurationSeconds;

    private int price;
    private int distance;
}
