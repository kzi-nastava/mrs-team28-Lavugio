package com.backend.lavugio.dto.user;

import com.backend.lavugio.model.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverUpdateRequestDTO {
    @Valid
    private AccountUpdateDTO profile;

    @NotBlank(message = "Vehicle make cannot be empty")
    private String vehicleMake;
    @NotBlank(message = "Vehicle model cannot be empty")
    private String vehicleModel;
    @NotBlank(message = "Vehicle license plate cannot be empty")
    private String vehicleLicensePlate;
    @Positive(message = "Vehicle seats must be positive number")
    @Max(value = 30, message = "Vehicle seats cannot exceed 30")
    private int vehicleSeats;
    
    private boolean vehiclePetFriendly;
    private boolean vehicleBabyFriendly;

    @NotBlank(message = "Vehicle color cannot be empty")
    private String vehicleColor;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
}
