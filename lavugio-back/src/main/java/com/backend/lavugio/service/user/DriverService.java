package com.backend.lavugio.service.user;

import com.backend.lavugio.dto.user.*;
import com.backend.lavugio.model.enums.DriverStatusEnum;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.DriverLocation;
import com.backend.lavugio.model.user.DriverUpdateRequest;
import com.backend.lavugio.model.vehicle.Vehicle;

import java.util.List;

public interface DriverService {
    // Registration
    DriverDTO register(DriverRegistrationDTO request);
    Driver createDriver(Driver driver);

    // CRUD Operations
    Driver updateDriver(Long id, Driver driver);
    void deleteDriver(Long id);
    Driver getDriverById(Long id);
    Driver getDriverByEmail(String email);
    List<Driver> getAllDrivers();

    // CRUD Operations DTO
    DriverDTO getDriverDTOById(Long id);
    DriverDTO getDriverDTOByEmail(String email);
    List<DriverDTO> getAllDriversDTO();
    List<DriverDTO> getAvailableDriversDTO();
    DriverDTO updateDriverDTO(Long id, UserProfileDTO request);

    void createDriverEditRequest(DriverUpdateRequestDTO request, Long driverId);
    List<DriverUpdateRequestDiffDTO> getAllPendingDriverEditRequests();
    // Profile Management DTO
    DriverDTO getDriverProfile(String email);
    DriverDTO getDriverProfileById(Long id);

    // Activation
    DriverStatusDTO activateDriverDTO(Long id);
    DriverStatusDTO deactivateDriverDTO(Long id);
    DriverStatusDTO getDriverStatusDTO(Long id);
    DriverStatusDTO getDriverActivityDTO(Long id);


    Driver activateDriver(Long driverId);
    void updateDriverDriving(Long driverId, boolean isDriving);

    Driver blockDriver(Long driverId, String reason);
    Driver unblockDriver(Long driverId);
    List<Driver> getActiveDrivers();
    List<Driver> getAvailableDrivers();
    Driver assignVehicle(Long driverId, Vehicle vehicle);
    Driver removeVehicle(Long driverId);
    boolean isDriverAvailable(Long driverId);
    long countActiveDrivers();
    List<Driver> getDriversWithoutVehicle();
}