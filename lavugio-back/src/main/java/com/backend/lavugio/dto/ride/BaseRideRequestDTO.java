package com.backend.lavugio.dto.ride;

import com.backend.lavugio.model.vehicle.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseRideRequestDTO {
    @NotNull
    List<RideRequstDestinationDTO> stops;
    private VehicleType vehicleType;
}
