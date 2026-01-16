package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.enums.VehicleType;

import java.util.List;

public class RideEstimateRequestDTO {
    private List<StopBaseDTO> stops;
    private VehicleType vehicleType;
}
