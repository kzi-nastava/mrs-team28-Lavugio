package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.dto.user.DriverLocationDTO;
import com.backend.lavugio.model.enums.DriverStatusEnum;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.user.DriverLocation;
import com.backend.lavugio.service.ride.RideQueryService;
import com.backend.lavugio.service.user.DriverAvailabilityService;
import com.backend.lavugio.service.user.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class DriverAvailabilityServiceImpl implements DriverAvailabilityService {

    private final HashMap<Long, DriverLocation> activeDriverLocations = new HashMap<>();

    private final RideQueryService rideQueryService;
    private final DriverService driverService;

    @Autowired
    public DriverAvailabilityServiceImpl(RideQueryService rideQueryService, DriverService driverService) {
        this.rideQueryService = rideQueryService;
        this.driverService = driverService;
    }

    @Override
    public DriverLocation updateDriverLocation(Long driverId, double longitude, double latitude) {
        DriverLocation location = activeDriverLocations.get(driverId);
        if (location == null) {
            throw new NoSuchElementException("Cannot update location: Driver with id " + driverId + " is not active.");
        }
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        return location;
    }

    @Override
    public ArrayList<DriverLocationDTO> getDriverLocationsDTO() {
        Map<Long, DriverLocation> locations = getAllActiveDriverLocations();

        ArrayList<DriverLocationDTO> locationDTOs = new ArrayList<>();
        for (DriverLocation location : locations.values()) {
            System.out.println("Processing location for driver ID: " + location.getDriverId());
            DriverStatusEnum status = getDriverStatus(location.getDriverId());
            locationDTOs.add(new DriverLocationDTO(location, status));
        }
        return locationDTOs;
    }

    @Override
    public DriverLocationDTO getDriverLocationDTO(Long driverId) {
        DriverLocation location = getDriverLocation(driverId);
        if (location == null) {
            throw new NoSuchElementException("Driver with id " + driverId + " is not active.");
        }

        DriverStatusEnum status = getDriverStatus(driverId);
        return new DriverLocationDTO(location, status);
    }

    @Override
    public DriverStatusEnum getDriverStatus(Long driverId) {
        List<Ride> driverRides = rideQueryService.getRidesByDriverId(driverId);
        if (driverRides == null) {
            throw new NoSuchElementException("No rides found for driver with id " + driverId);
        }

        for (Ride ride : driverRides) {
            switch (ride.getRideStatus()) {
                case ACTIVE:
                    return DriverStatusEnum.BUSY;
                case SCHEDULED:
                    return DriverStatusEnum.RESERVED;
                default:
                    break;
            }
        }
        return DriverStatusEnum.AVAILABLE;
    }

    @Override
    public DriverLocation activateDriver(Long driverId, double longitude, double latitude) {
        if (driverService.getDriverById(driverId) == null) {
            throw new NoSuchElementException("Cannot activate driver: Driver not found with id " + driverId);
        }

        if (activeDriverLocations.containsKey(driverId)) {
            throw new IllegalStateException("Driver with id " + driverId + " is already active.");
        }

        return addActiveDriverLocation(driverId, longitude, latitude);
    }

    @Override
    public void deactivateDriver(Long driverId) {
        if (!activeDriverLocations.containsKey(driverId)) {
            throw new NoSuchElementException("Cannot deactivate: Driver with id " + driverId + " is not active.");
        }
        deleteDriverLocation(driverId);
    }

    private HashMap<Long, DriverLocation> getAllActiveDriverLocations() {
        return activeDriverLocations;
    }

    private DriverLocation getDriverLocation(Long driverId) {
        return activeDriverLocations.get(driverId);
    }

    private DriverLocation addActiveDriverLocation(Long driverId, double longitude, double latitude) {
        DriverLocation driverLocation = new DriverLocation(driverId, longitude, latitude);
        activeDriverLocations.put(driverId, driverLocation);
        return driverLocation;
    }

    private void deleteDriverLocation(Long driverId) {
        activeDriverLocations.remove(driverId);
    }

}
