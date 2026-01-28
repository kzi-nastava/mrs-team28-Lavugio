package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.enums.RideStatus;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
    @Future
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    @Positive(message = "Price must be positive")
    private float price;
    @Positive(message = "Distance must be positive")
    private float distance;
    @NotBlank(message = "Has panic flag cannot be blank")
    private boolean hasPanic;
    @NotBlank(message = "Start location cannot be blank")
    private String startLocation;
    @NotBlank(message = "End location cannot be blank")
    private String endLocation;
}
