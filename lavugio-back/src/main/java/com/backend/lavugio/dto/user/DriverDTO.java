package com.backend.lavugio.dto.user;

import com.backend.lavugio.dto.vehicle.VehicleDTO;
import com.backend.lavugio.model.enums.VehicleType;
import com.backend.lavugio.model.vehicle.Vehicle;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriverDTO extends UserDTO {
    private boolean active;

    private String vehicleLicensePlate;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleColor;
    private VehicleType vehicleType;
    private boolean vehicleBabyFriendly;
    private boolean vehiclePetFriendly;
}
