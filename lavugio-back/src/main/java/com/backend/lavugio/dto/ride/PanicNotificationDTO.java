package com.backend.lavugio.dto.ride;

import com.backend.lavugio.dto.CoordinatesDTO;
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
    private String passengerName;
    private CoordinatesDTO location;
    private String message;
    private LocalDateTime timestamp;
    private String vehicleType;
    private String vehicleLicensePlate;
    private String driverName;
}
