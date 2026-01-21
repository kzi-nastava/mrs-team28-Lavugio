package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RideEstimateRequestDTO {
    private String selectedVehicleType;
    private float distanceMeters;
}
