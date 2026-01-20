package com.backend.lavugio.service.user;

import com.backend.lavugio.dto.user.DriverLocationDTO;
import com.backend.lavugio.model.enums.DriverStatusEnum;
import com.backend.lavugio.model.user.DriverLocation;

import java.util.List;
import java.util.Map;

public interface DriverAvailabilityService {
    DriverLocation updateDriverLocation(Long driverId, double longitude, double latitude);
    List<DriverLocationDTO> getDriverLocationsDTO();
    DriverLocationDTO getDriverLocationDTO(Long driverId);
    DriverLocation activateDriver(Long driverId, double longitude, double latitude);
    void deactivateDriver(Long driverId);
    DriverStatusEnum getDriverStatus(Long driverId);
}
