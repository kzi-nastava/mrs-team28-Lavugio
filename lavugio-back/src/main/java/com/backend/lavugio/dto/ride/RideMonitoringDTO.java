package com.backend.lavugio.dto.ride;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.ride.Ride;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RideMonitoringDTO {
    @NotNull
    private Long rideId;

    @NotNull
    private Long driverId;

    @NotBlank
    private String driverName;

    @Past
    private LocalDateTime startTime;

    @NotBlank
    private String startAddress;
    
    @NotBlank
    private String endAddress;

    @NotEmpty
    private CoordinatesDTO[] checkpoints;

    public RideMonitoringDTO(Ride ride) {
        this.rideId = ride.getId();
        this.driverId = ride.getDriver().getId();
        this.driverName = ride.getDriver().getName() + " " + ride.getDriver().getLastName();
        this.startTime = ride.getStartDateTime();
        this.startAddress = ride.getStartAddress();
        this.endAddress = ride.getEndAddress();
        List<CoordinatesDTO> checkpoints = ride.getCheckpoints().stream().map(dest -> new CoordinatesDTO(dest.getAddress().getLatitude(), dest.getAddress().getLongitude())).toList();
        this.checkpoints = checkpoints.toArray(new CoordinatesDTO[0]);
    }
}
