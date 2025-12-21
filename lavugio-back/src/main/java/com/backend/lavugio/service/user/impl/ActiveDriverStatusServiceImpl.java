package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.model.route.Address;
import com.backend.lavugio.model.user.DriverStatus;
import com.backend.lavugio.service.user.ActiveDriverStatusService;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ActiveDriverStatusServiceImpl implements ActiveDriverStatusService {

    private HashMap<Long,DriverStatus> activeDriverStatuses;

    @Override
    public HashMap<Long, DriverStatus> getAllActiveDriverStatuses() {
        return activeDriverStatuses;
    }

    @Override
    public DriverStatus getDriverStatus(Long driverId) {
        return activeDriverStatuses.get(driverId);
    }

    @Override
    public DriverStatus addActiveDriverStatus(Long driverId, double longitude, double latitude) {
        DriverStatus driverStatus = new DriverStatus(driverId, longitude, latitude, true);
        activeDriverStatuses.put(driverId,driverStatus);
        return driverStatus;
    }

    @Override
    public DriverStatus updateDriverLocation (Long driverId, double longitude, double latitude) throws RuntimeException {
        DriverStatus status = activeDriverStatuses.get(driverId);
        if (status == null) {
            throw new RuntimeException("Driver status not found");
        }
        status.setLongitude(longitude);
        status.setLatitude(latitude);
        return status;
    }

    @Override
    public DriverStatus updateDriverAvailability(Long driverId, boolean isAvailable) throws RuntimeException {
        DriverStatus status = activeDriverStatuses.get(driverId);
        if (status == null) {
            throw new RuntimeException("Driver status not found");
        }
        status.setAvailable(isAvailable);
        return status;
    }

    @Override
    public void deleteDriverStatus(Long driverId) {
        activeDriverStatuses.remove(driverId);
    }
}
