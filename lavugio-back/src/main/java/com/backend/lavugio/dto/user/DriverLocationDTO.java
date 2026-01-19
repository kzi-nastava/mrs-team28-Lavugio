package com.backend.lavugio.dto.user;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.model.enums.DriverStatusEnum;
import com.backend.lavugio.model.user.DriverLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocationDTO {
    private Long id;
    private CoordinatesDTO location;
    private DriverStatusEnum status;

    public DriverLocationDTO(DriverLocation location, DriverStatusEnum status) {
        this.id = location.getDriverId();
        this.location = new CoordinatesDTO(location.getLatitude(), location.getLongitude());
        this.status = status;
    }
}
