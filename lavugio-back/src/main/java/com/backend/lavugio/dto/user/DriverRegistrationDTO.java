package com.backend.lavugio.dto.user;

import com.backend.lavugio.model.enums.VehicleType;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverRegistrationDTO extends UserRegistrationDTO {

    @NotEmpty
    private String vehicleMake;

    @NotEmpty
    private String vehicleModel;

    @NotEmpty
    private String licenseNumber;

    @NotEmpty
    private String licensePlate;

    @NotEmpty
    private String vehicleColor;

    @NotEmpty
    private VehicleType vehicleType;

    @NotEmpty
    private int passangerSeats;

    private boolean babyFriendly = false;

    private boolean petFriendly = false;
}
