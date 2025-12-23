package com.backend.lavugio.dto.user;

import com.backend.lavugio.model.vehicle.Vehicle;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DriverDTO {
    private UserDTO user;

    private Vehicle vehicle;
}
