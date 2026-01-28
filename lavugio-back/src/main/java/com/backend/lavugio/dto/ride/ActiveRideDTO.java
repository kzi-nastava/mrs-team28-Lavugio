package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.enums.RideStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActiveRideDTO {
    private Long id;
    private RideStatus rideStatus;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private float price;
    private float distance;
    private boolean hasPanic;
    private String startLocation;
    private String endLocation;
}
