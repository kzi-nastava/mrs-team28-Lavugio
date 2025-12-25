package com.backend.lavugio.dto.vehicle;

import com.backend.lavugio.model.vehicle.VehicleType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO {
    private Long id;
    private String licensePlate;
    private String make;
    private String model;
    private String color;
    private VehicleType type;
    private boolean babyFriendly;
    private boolean petFriendly;
}
