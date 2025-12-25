package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.model.user.DriverLocation;
import com.backend.lavugio.service.user.ActiveDriverLocationService;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ActiveDriverLocationServiceImpl implements ActiveDriverLocationService {

    private final HashMap<Long, DriverLocation> activeDriverLocations = new HashMap<>();

    @Override
    public HashMap<Long, DriverLocation> getAllActiveDriverLocations() {
        return activeDriverLocations;
    }

    @Override
    public DriverLocation getDriverLocation(Long driverId) {
        return activeDriverLocations.get(driverId);
    }

    @Override
    public DriverLocation addActiveDriverLocation(Long driverId, double longitude, double latitude) {
        DriverLocation driverLocation = new DriverLocation(driverId, longitude, latitude);
        activeDriverLocations.put(driverId, driverLocation);
        return driverLocation;
    }

    @Override
    public DriverLocation updateDriverLocation (Long driverId, double longitude, double latitude) throws RuntimeException {
        DriverLocation location = activeDriverLocations.get(driverId);
        if (location == null) {
            throw new RuntimeException("Driver location not found");
        }
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        return location;
    }

    @Override
    public void deleteDriverLocation(Long driverId) {
        activeDriverLocations.remove(driverId);
    }
}
