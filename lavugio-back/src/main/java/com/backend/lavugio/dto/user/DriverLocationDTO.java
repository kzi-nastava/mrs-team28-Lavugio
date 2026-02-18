package com.backend.lavugio.dto.user;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.model.enums.DriverStatusEnum;
import com.backend.lavugio.model.user.DriverLocation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverLocationDTO {
    @NotNull
    private Long id;

    @Valid
    @NotNull
    private CoordinatesDTO location;

    @NotNull
    private DriverStatusEnum status;

    public DriverLocationDTO(DriverLocation location, DriverStatusEnum status) {
        this.id = location.getDriverId();
        this.location = new CoordinatesDTO(location.getLatitude(), location.getLongitude());
        this.status = status;
    }
}
