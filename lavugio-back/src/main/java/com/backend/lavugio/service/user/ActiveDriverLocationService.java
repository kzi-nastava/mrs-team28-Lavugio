package com.backend.lavugio.service.user;

import com.backend.lavugio.model.user.DriverLocation;

import java.util.HashMap;

public interface ActiveDriverLocationService {
    public HashMap<Long, DriverLocation> getAllActiveDriverLocations();
    public DriverLocation getDriverLocation(Long driverId);
    public DriverLocation addActiveDriverLocation(Long driverId, double longitude, double latitude);
    public DriverLocation updateDriverLocation(Long driverId, double longitude, double latitude);
    public void deleteDriverLocation(Long driverId);
}
