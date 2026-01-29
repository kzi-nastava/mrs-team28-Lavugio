package com.backend.lavugio.dto.ride;

import com.backend.lavugio.dto.CoordinatesDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinishRideDTO {
    private Long rideId;
    @Valid
    private CoordinatesDTO finalDestination;
    @NotNull(message = "Finished early flag must not be null")
    private boolean finishedEarly;
    @Positive(message = "Distance must be positive")
    private Double distance;
}
