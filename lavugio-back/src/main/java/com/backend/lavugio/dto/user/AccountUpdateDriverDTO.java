package com.backend.lavugio.dto.user;

import com.backend.lavugio.model.enums.VehicleType;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountUpdateDriverDTO extends AccountUpdateDTO {
    private String licenseNumber;
    private String licensePlate;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleColor;
    private VehicleType vehicleType;
    private Boolean babyFriendly;
    private Boolean petFriendly;
}
