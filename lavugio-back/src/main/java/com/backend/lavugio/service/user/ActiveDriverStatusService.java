package com.backend.lavugio.service.user;

import com.backend.lavugio.model.route.Address;
import com.backend.lavugio.model.user.DriverStatus;

import java.util.HashMap;

public interface ActiveDriverStatusService {
    public HashMap<Long, DriverStatus> getAllActiveDriverStatuses();
    public DriverStatus getDriverStatus(Long driverId);
    public DriverStatus addActiveDriverStatus(Long driverId, double longitude, double latitude);
    public DriverStatus updateDriverLocation(Long driverId, double longitude, double latitude);
    public DriverStatus updateDriverAvailability(Long driverId, boolean isAvailable);
    public void deleteDriverStatus(Long driverId);
}
