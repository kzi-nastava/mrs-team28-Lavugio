package com.backend.lavugio.service.user;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.dto.user.DriverLocationDTO;
import com.backend.lavugio.model.enums.DriverStatusEnum;
import com.backend.lavugio.model.user.DriverLocation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface DriverAvailabilityService {
    DriverLocation updateDriverLocation(Long id, CoordinatesDTO driverCoords);
    List<DriverLocationDTO> getDriverLocationsDTO();
    DriverLocationDTO getDriverLocationDTO(Long driverId);
    DriverLocation activateDriver(Long driverId, double longitude, double latitude);
    void deactivateDriver(Long driverId);
    DriverStatusEnum getDriverStatus(Long driverId);
    CompletableFuture<List<DriverLocationDTO>> getDriverLocationsDTOAsync();
}
