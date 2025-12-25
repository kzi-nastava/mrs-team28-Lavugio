package com.backend.lavugio.dto.user;

import com.backend.lavugio.model.vehicle.VehicleType;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverRegistrationDTO extends UserRegistrationDTO {
    @NotEmpty
    private String licenseNumber;

    @NotEmpty
    private String licensePlate;

    @NotEmpty
    private String vehicleMake;

    @NotEmpty
    private String vehicleModel;

    private String vehicleColor;
    private VehicleType vehicleType;
    private boolean babyFriendly;
    private boolean petFriendly;
}
