package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.DriverLocation;
import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.repository.user.DriverRepository;
import com.backend.lavugio.service.user.ActiveDriverLocationService;
import com.backend.lavugio.service.user.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private ActiveDriverLocationService activeDriverLocationService;

    @Override
    @Transactional
    public Driver createDriver(Driver driver) {
        // Provera da li email već postoji
        if (driverRepository.findByEmail(driver.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + driver.getEmail());
        }

        // Postavi početne vrednosti
        driver.setBlocked(false);
        driver.setBlockReason(null);
        driver.setDriving(false);

        return driverRepository.save(driver);
    }

    @Override
    @Transactional
    public Driver updateDriver(Long id, Driver driver) {
        Driver existing = getDriverById(id);

        // Provera da li se email menja
        if (!existing.getEmail().equals(driver.getEmail()) &&
                driverRepository.findByEmail(driver.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + driver.getEmail());
        }

        existing.setName(driver.getName());
        existing.setLastName(driver.getLastName());
        existing.setEmail(driver.getEmail());
        existing.setPassword(driver.getPassword());
        existing.setProfilePhotoPath(driver.getProfilePhotoPath());

        return driverRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteDriver(Long id) {
        Driver driver = getDriverById(id);
        driverRepository.delete(driver);
    }

    @Override
    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
    }

    @Override
    public Driver getDriverByEmail(String email) {
        return driverRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found with email: " + email));
    }

    @Override
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Override
    public DriverLocation activateDriver(Long driverId, double longitude, double latitude) throws RuntimeException{
        if (getDriverById(driverId) == null) {
            throw new RuntimeException("Driver not found with id: " + driverId);
        };
        return activeDriverLocationService.addActiveDriverLocation(driverId, longitude, latitude);
    }

    @Override
    public DriverLocation updateDriverLocation(Long driverId, double longitude, double latitude) throws RuntimeException{
        return activeDriverLocationService.updateDriverLocation(driverId, longitude, latitude);
    }

    @Override
    public void updateDriverDriving(Long driverId, boolean isDriving) throws RuntimeException{
        Driver driver = this.getDriverById(driverId);
        driver.setDriving(isDriving);
        driverRepository.save(driver);
    }

    @Override
    public void deactivateDriver(Long driverId) {
        activeDriverLocationService.deleteDriverLocation(driverId);
    }

    @Override
    @Transactional
    public Driver blockDriver(Long driverId, String reason) {
        Driver driver = getDriverById(driverId);
        driver.setBlocked(true);
        driver.setBlockReason(reason);
        driver.setDriving(false);
        return driverRepository.save(driver);
    }

    @Override
    @Transactional
    public Driver unblockDriver(Long driverId) {
        Driver driver = getDriverById(driverId);
        driver.setBlocked(false);
        driver.setBlockReason(null);
        return driverRepository.save(driver);
    }

    @Override
    public List<Driver> getActiveDrivers() {
        return driverRepository.findByActiveTrue();
    }

    @Override
    public List<Driver> getAvailableDrivers() {
        return driverRepository.findByBlockedFalseAndActiveTrue();
    }

    @Override
    @Transactional
    public Driver assignVehicle(Long driverId, Vehicle vehicle) {
        Driver driver = getDriverById(driverId);

        // Provera da li vozilo već dodeljeno drugom vozaču
        if (driverRepository.existsByVehicle(vehicle)) {
            throw new RuntimeException("Vehicle is already assigned to another driver");
        }

        driver.setVehicle(vehicle);
        return driverRepository.save(driver);
    }

    @Override
    @Transactional
    public Driver removeVehicle(Long driverId) {
        Driver driver = getDriverById(driverId);
        driver.setVehicle(null);
        return driverRepository.save(driver);
    }

    @Override
    public boolean isDriverAvailable(Long driverId) {
        Driver driver = getDriverById(driverId);
        return !driver.isDriving() && !driver.isBlocked();
    }

    @Override
    public long countActiveDrivers() {
        return driverRepository.countByActiveTrue();
    }

    @Override
    public List<Driver> getDriversWithoutVehicle() {
        return driverRepository.findByVehicleIsNull();
    }

    @Override
    public Map<Long, DriverLocation> getAllActiveDriverStatuses() {
        return activeDriverLocationService.getAllActiveDriverLocations();
    }

    @Override
    public DriverLocation getDriverStatus(Long driverId) {
        return activeDriverLocationService.getDriverLocation(driverId);
    }
}