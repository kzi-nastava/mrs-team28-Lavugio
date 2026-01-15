package com.backend.lavugio.dto.vehicle;

import com.backend.lavugio.model.enums.VehicleType;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVehicleDTO {
    private String licensePlate;
    private String make;
    private String model;
    private String color;
    private VehicleType type;
    private Boolean babyFriendly;
    private Boolean petFriendly;
}
