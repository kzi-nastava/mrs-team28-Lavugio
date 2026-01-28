package com.backend.lavugio.dto.user;

import com.backend.lavugio.model.enums.VehicleType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverRegistrationDTO extends UserRegistrationDTO {

    @NotEmpty(message = "Vehicle make cannot be empty")
    private String vehicleMake;

    @NotEmpty(message = "Vehicle model cannot be empty")
    private String vehicleModel;

    @NotEmpty(message = "License plate cannot be empty")
    private String licensePlate;

    @NotEmpty(message = "Vehicle color cannot be empty")
    private String vehicleColor;

    private VehicleType vehicleType;

    @Positive(message = "Passanger seats must be positive")
    @Max(value = 30, message = "Passanger seats cannot exceed 30")
    private int passangerSeats;

    private boolean babyFriendly = false;

    private boolean petFriendly = false;
}
