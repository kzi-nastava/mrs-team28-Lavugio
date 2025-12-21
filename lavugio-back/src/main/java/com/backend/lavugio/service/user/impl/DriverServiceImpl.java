package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.DriverStatus;
import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.repository.user.DriverRepository;
import com.backend.lavugio.service.user.ActiveDriverStatusService;
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
    private ActiveDriverStatusService activeDriverStatusService;

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
        driver.setActive(true); // Novi vozač je aktiviran po defaultu

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
    public DriverStatus activateDriver(Long driverId, double longitude, double latitude) throws RuntimeException{
        if (getDriverById(driverId) == null) {
            throw new RuntimeException("Driver not found with id: " + driverId);
        };
        return activeDriverStatusService.addActiveDriverStatus(driverId, longitude, latitude);
    }

    @Override
    public DriverStatus updateDriverLocation(Long driverId, double longitude, double latitude) throws RuntimeException{
        return activeDriverStatusService.updateDriverLocation(driverId, longitude, latitude);
    }

    @Override
    public DriverStatus updateDriverAvailability(Long driverId, boolean isAvailable) throws RuntimeException{
        return activeDriverStatusService.updateDriverAvailability(driverId, isAvailable);
    }

    @Override
    public void deactivateDriver(Long driverId) {
        activeDriverStatusService.deleteDriverStatus(driverId);
    }

    @Override
    @Transactional
    public Driver blockDriver(Long driverId, String reason) {
        Driver driver = getDriverById(driverId);
        driver.setBlocked(true);
        driver.setBlockReason(reason);
        driver.setActive(false); // Blokiran vozač ne može biti aktivan
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
        return driver.isActive() && !driver.isBlocked();
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
    public Map<Long, DriverStatus> getAllActiveDriverStatuses() {
        return activeDriverStatusService.getAllActiveDriverStatuses();
    }

    @Override
    public DriverStatus getDriverStatus(Long driverId) {
        return activeDriverStatusService.getDriverStatus(driverId);
    }
}