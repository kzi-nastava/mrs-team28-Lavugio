package com.backend.lavugio.dto.ride;

import com.backend.lavugio.dto.user.DriverDTO;
import com.backend.lavugio.dto.user.UserDTO;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.enums.VehicleType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RideResponseDTO {
    private Long id;
    private RideStatus status;
    private List<RideDestinationDTO> stops;
    private float price;
    private float distance;
    private Integer estimatedDuration;
    private DriverDTO driver;
    private UserDTO creator;
    private List<UserDTO> passengers;
    private LocalDateTime scheduledTime;
    private VehicleType vehicleType;
    private boolean babyFriendly;
    private boolean petFriendly;
}
