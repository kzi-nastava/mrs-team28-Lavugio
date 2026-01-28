package com.backend.lavugio.dto.ride;

import com.backend.lavugio.dto.CoordinatesDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PanicNotificationDTO {
    private Long rideId;
    private Long passengerId;
    @NotBlank(message = "Passenger name cannot be blank")
    private String passengerName;
    @Valid
    private CoordinatesDTO location;
    private String message;
    private LocalDateTime timestamp;
    @NotBlank(message = "Vehicle type cannot be blank")
    private String vehicleType;
    @NotBlank(message = "Vehicle license plate cannot be blank")
    private String vehicleLicensePlate;
    @NotBlank(message = "Driver name cannot be blank")
    private String driverName;
}
