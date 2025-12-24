package com.backend.lavugio.service.user;

import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.DriverLocation;
import com.backend.lavugio.model.vehicle.Vehicle;

import java.util.List;
import java.util.Map;

public interface DriverService {
    Driver createDriver(Driver driver);
    Driver updateDriver(Long id, Driver driver);
    void deleteDriver(Long id);
    Driver getDriverById(Long id);
    Driver getDriverByEmail(String email);
    List<Driver> getAllDrivers();
    DriverLocation activateDriver(Long driverId, double longitude, double latitude);
    DriverLocation updateDriverLocation(Long driverId, double longitude, double latitude);
    void updateDriverDriving(Long driverId, boolean isDriving);
    void deactivateDriver(Long driverId);
    Driver blockDriver(Long driverId, String reason);
    Driver unblockDriver(Long driverId);
    List<Driver> getActiveDrivers();
    List<Driver> getAvailableDrivers();
    Driver assignVehicle(Long driverId, Vehicle vehicle);
    Driver removeVehicle(Long driverId);
    boolean isDriverAvailable(Long driverId);
    long countActiveDrivers();
    List<Driver> getDriversWithoutVehicle();

    Map<Long, DriverLocation> getAllActiveDriverStatuses();

    DriverLocation getDriverStatus(Long driverId);
}