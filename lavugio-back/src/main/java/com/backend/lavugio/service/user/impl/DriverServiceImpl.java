package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.dto.user.*;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.DriverUpdateRequest;
import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.model.enums.VehicleType;
import com.backend.lavugio.repository.user.DriverRepository;

import com.backend.lavugio.repository.user.DriverUpdateRequestRepository;
import com.backend.lavugio.repository.vehicle.VehicleRepository;

import com.backend.lavugio.service.user.DriverActivityService;
import com.backend.lavugio.service.user.DriverAvailabilityService;
import com.backend.lavugio.service.user.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DriverUpdateRequestRepository driverUpdateRequestRepository;

    @Autowired
    private DriverActivityService driverActivityService;

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
    public Driver activateDriver(Long driverId) {
        Driver driver = getDriverById(driverId);
        
        if (driver.isActive()) {
             throw new RuntimeException("Driver is already active");
        }
        
        if (driver.isBlocked()) {
            throw new RuntimeException("Driver is blocked. Cannot activate.");
        }
        
        driver.setActive(true);

        driverActivityService.startActivity(driverId);

        return driverRepository.save(driver);
    }

    @Override
    public Driver deactivateDriver(Long driverId) {
        Driver driver = getDriverById(driverId);

        if (!driver.isActive()) {
            throw new RuntimeException("Driver is already inactive");
        }

        driver.setActive(false);

        driverActivityService.endActivity(driverId);

        return driverRepository.save(driver);
    }



    @Override
    public void updateDriverDriving(Long driverId, boolean isDriving) throws RuntimeException{
        Driver driver = this.getDriverById(driverId);
        driver.setDriving(isDriving);
        driverRepository.save(driver);
    }
    
    @Override
    @Transactional
    public void setDriverStatus(Long driverId, Boolean active) {
        Driver driver = getDriverById(driverId);
        
        if (driver.isBlocked()) {
            throw new RuntimeException("Cannot change status of a blocked driver");
        }
        
        // If driver is currently driving and wants to go inactive, save as pending
        if (driver.isDriving() && !active) {
            driver.setPendingStatusChange(false); // false = wants to be inactive
            driverRepository.save(driver);
            // Don't throw exception, return normally - controller will handle the response
            return;
        }
        
        // If driver wants to go active or is not driving, apply immediately
        driver.setActive(active);
        driver.setPendingStatusChange(null); // Clear any pending change
        driverRepository.save(driver);
    }
    
    @Override
    public boolean hasActiveRide(Long driverId) {
        Driver driver = getDriverById(driverId);
        return driver.isDriving();
    }
    
    @Override
    public Boolean getPendingStatusChange(Long driverId) {
        Driver driver = getDriverById(driverId);
        return driver.getPendingStatusChange();
    }
    
    @Override
    public boolean canDriverLogout(Long driverId) {
        Driver driver = getDriverById(driverId);
        // Driver can logout only if they are not currently driving
        return !driver.isDriving();
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
        return driverRepository.findByIsDrivingTrue();
    }

    @Override
    public List<Driver> getAvailableDrivers() {
        return driverRepository.findByBlockedFalseAndIsDrivingTrue();
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
        return driverRepository.countByIsDrivingTrue();
    }

    @Override
    public List<Driver> getDriversWithoutVehicle() {
        return driverRepository.findByVehicleIsNull();
    }

    @Override
    public DriverDTO register(DriverRegistrationDTO request) {
        // Check if email already exists
        if (driverRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Check if license plate exists
        if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new RuntimeException("License plate already registered: " + request.getLicensePlate());
        }

        // Create Vehicle
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setMake(request.getVehicleMake());
        vehicle.setModel(request.getVehicleModel());
        vehicle.setColor(request.getVehicleColor());
        vehicle.setPassengerSeats(request.getPassangerSeats());
        try {
            vehicle.setType(VehicleType.valueOf(request.getVehicleType().name()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid vehicle type: " + request.getVehicleType());
        }

        vehicle.setBabyFriendly(request.isBabyFriendly());
        vehicle.setPetFriendly(request.isPetFriendly());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        // Create Driver
        Driver driver = new Driver();
        driver.setEmail(request.getEmail());
        driver.setPassword(UUID.randomUUID().toString());
        driver.setName(request.getName());
        driver.setLastName(request.getLastName());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setAddress(request.getAddress());
        driver.setProfilePhotoPath(request.getProfilePhotoPath());
        driver.setBlocked(false);
        driver.setBlockReason(null);
        driver.setActive(false);
        driver.setVehicle(savedVehicle);

        Driver savedDriver = driverRepository.save(driver);
        return mapToDTO(savedDriver);
    }

    @Override
    public void createDriverEditRequest(DriverUpdateRequestDTO request, Long driverId) {
        DriverUpdateRequest editRequest = new DriverUpdateRequest();
        editRequest.setDriverId(driverId);
        editRequest.setName(request.getProfile().getName());
        editRequest.setLastName(request.getProfile().getSurname());
        editRequest.setPhoneNumber(request.getProfile().getPhoneNumber());
        editRequest.setAddress(request.getProfile().getAddress());
        editRequest.setMake(request.getVehicleMake());
        editRequest.setModel(request.getVehicleModel());
        editRequest.setLicensePlate(request.getVehicleLicensePlate());
        editRequest.setSeatsNumber(request.getVehicleSeats());
        editRequest.setPetFriendly(request.isVehiclePetFriendly());
        editRequest.setBabyFriendly(request.isVehicleBabyFriendly());
        editRequest.setColor(request.getVehicleColor());
        editRequest.setType(request.getVehicleType());
        editRequest.setValidated(false);
        driverUpdateRequestRepository.save(editRequest);
    }

    @Override
    public List<DriverUpdateRequestDiffDTO> getAllPendingDriverEditRequests() {
        List<DriverUpdateRequest> requests = driverUpdateRequestRepository.findByValidatedFalse();
        List<DriverUpdateRequestDiffDTO> requestsDTO = new ArrayList<>();
        for (DriverUpdateRequest request : requests) {
            Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + request.getDriverId()));
            
                DriverUpdateRequestDiffDTO requestDTO = new DriverUpdateRequestDiffDTO();
            
            requestDTO.setRequestId(request.getId());

            DriverUpdateRequestDTO oldData = new DriverUpdateRequestDTO();
            AccountUpdateDTO oldAccount = new AccountUpdateDTO();
            oldAccount.setName(driver.getName());
            oldAccount.setSurname(driver.getLastName());
            oldAccount.setPhoneNumber(driver.getPhoneNumber());
            oldAccount.setAddress(driver.getAddress());
            oldData.setProfile(oldAccount);
            oldData.setVehicleModel(driver.getVehicle().getModel());
            oldData.setVehicleMake(driver.getVehicle().getMake());
            oldData.setVehicleLicensePlate(driver.getVehicle().getLicensePlate());
            oldData.setVehicleColor(driver.getVehicle().getColor());
            oldData.setVehicleType(driver.getVehicle().getType());
            oldData.setVehicleBabyFriendly(driver.getVehicle().isBabyFriendly());
            oldData.setVehiclePetFriendly(driver.getVehicle().isPetFriendly());
            requestDTO.setOldData(oldData);

            DriverUpdateRequestDTO newData = new DriverUpdateRequestDTO();
            AccountUpdateDTO newAccount = new AccountUpdateDTO();
            newAccount.setName(request.getName());
            newAccount.setSurname(request.getLastName());
            newAccount.setPhoneNumber(request.getPhoneNumber());
            newAccount.setAddress(request.getAddress());
            newData.setProfile(newAccount);
            newData.setVehicleModel(request.getModel());
            newData.setVehicleMake(request.getMake());
            newData.setVehicleLicensePlate(request.getLicensePlate());
            newData.setVehicleColor(request.getColor());
            newData.setVehicleType(request.getType());
            newData.setVehicleBabyFriendly(request.isBabyFriendly());
            newData.setVehiclePetFriendly(request.isPetFriendly());
            requestDTO.setNewData(newData);

            requestDTO.setEmail(driver.getEmail());

            requestsDTO.add(requestDTO);
        }
        return requestsDTO;
    }

    @Override
    public void approveEditRequest(Long requestId) {
        DriverUpdateRequest request = driverUpdateRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Edit request not found with id: " + requestId));
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + request.getDriverId()));
        Vehicle vehicle = driver.getVehicle();
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setColor(request.getColor());
        try {
            vehicle.setType(request.getType());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid vehicle type: " + request.getType());
        }
        vehicle.setBabyFriendly(request.isBabyFriendly());
        vehicle.setPetFriendly(request.isPetFriendly());
        vehicleRepository.save(vehicle);

        driver.setName(request.getName());
        driver.setLastName(request.getLastName());
        driver.setPhoneNumber(request.getPhoneNumber());
        driver.setAddress(request.getAddress());
        driverRepository.save(driver);
        
        request.setValidated(true);
        driverUpdateRequestRepository.save(request);
    }

    @Override
    public void rejectEditRequest(Long requestId) {
        DriverUpdateRequest request = driverUpdateRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Edit request not found with id: " + requestId));
        request.setValidated(true);
        driverUpdateRequestRepository.save(request);
    }

    @Override
    public DriverDTO getDriverDTOById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        return mapToDTO(driver);
    }

    @Override
    public DriverDTO getDriverDTOByEmail(String email) {
        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found with email: " + email));
        return mapToDTO(driver);
    }

    @Override
    public List<DriverDTO> getAllDriversDTO() {
        List<Driver> drivers = driverRepository.findAll();
        return drivers.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DriverDTO> getAvailableDriversDTO() {
        List<Driver> drivers = driverRepository.findByBlockedFalseAndIsDrivingTrue();
        return drivers.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DriverDTO updateDriverDTO(Long id, UserProfileDTO request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        // Verify current user has permission (simple check - email match)
        /*if (!driver.getEmail().equals(currentEmail)) {
            throw new RuntimeException("Unauthorized to update this driver");
        }*/

        if (request.getName() != null) {
            driver.setName(request.getName());
        }
        if (request.getSurname() != null) {
            driver.setLastName(request.getSurname());
        }
        if (request.getPhoneNumber() != null) {
            driver.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getProfilePhotoPath() != null) {
            driver.setProfilePhotoPath(request.getProfilePhotoPath());
        }

        // Update vehicle if provided
        if (driver.getVehicle() != null) {
            Vehicle vehicle = driver.getVehicle();

            if (request.getVehicleLicensePlate() != null) {
                // Check if new license plate is unique
                if (!vehicle.getLicensePlate().equals(request.getVehicleLicensePlate()) &&
                        vehicleRepository.existsByLicensePlate(request.getVehicleLicensePlate())) {
                    throw new RuntimeException("License plate already exists: " + request.getVehicleLicensePlate());
                }
                vehicle.setLicensePlate(request.getVehicleLicensePlate());
            }

            if (request.getVehicleMake() != null) {
                vehicle.setMake(request.getVehicleMake());
            }
            if (request.getVehicleModel() != null) {
                vehicle.setModel(request.getVehicleModel());
            }
            if (request.getVehicleColor() != null) {
                vehicle.setColor(request.getVehicleColor());
            }
            if (request.getVehicleType() != null) {
                try {
                    vehicle.setType(VehicleType.valueOf(request.getVehicleType()));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid vehicle type: " + request.getVehicleType());
                }
            }
            if (request.getVehicleBabyFriendly() != null) {
                vehicle.setBabyFriendly(request.getVehicleBabyFriendly());
            }
            if (request.getVehiclePetFriendly() != null) {
                vehicle.setPetFriendly(request.getVehiclePetFriendly());
            }

            vehicleRepository.save(vehicle);
        }

        Driver updatedDriver = driverRepository.save(driver);
        return mapToDTO(updatedDriver);
    }

    @Override
    public DriverDTO getDriverProfile(String email) {
        Driver driver = driverRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found with email: " + email));
        return mapToProfileDTO(driver);
    }

    @Override
    public DriverDTO getDriverProfileById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
        return mapToProfileDTO(driver);
    }

    @Override
    public DriverStatusDTO activateDriverDTO(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        // if (driver.isActive()) {
        //     throw new RuntimeException("Driver is already active");
        // }

        if (driver.isBlocked()) {
            throw new RuntimeException("Driver is blocked. Cannot activate.");
        }

        // TODO: Check 8-hour work limit
        // driver.setActive(true);
        Driver savedDriver = driverRepository.save(driver);
        return createDriverStatusDTO(savedDriver);
    }

    @Override
    public DriverStatusDTO deactivateDriverDTO(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        // Check if already inactive
        // if (!driver.isActive()) {
        //     throw new RuntimeException("Driver is already inactive");
        // }

        // driver.setActive(false);
        Driver savedDriver = driverRepository.save(driver);

        return createDriverStatusDTO(savedDriver);
    }

    @Override
    public DriverStatusDTO getDriverStatusDTO(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        return createDriverStatusDTO(driver);
    }

    @Override
    public DriverStatusDTO getDriverActivityDTO(Long id) {
        // For now, return same as status
        return getDriverStatusDTO(id);
    }

    // ========== HELPER METHODS ==========

    private DriverDTO mapToDTO(Driver driver) {
        DriverDTO dto = new DriverDTO();
        dto.setId(driver.getId());
        dto.setName(driver.getName());
        dto.setLastName(driver.getLastName());
        dto.setEmail(driver.getEmail());
        dto.setPhoneNumber(driver.getPhoneNumber());
        dto.setProfilePhotoPath(driver.getProfilePhotoPath());
        dto.setBlocked(driver.isBlocked());
        dto.setBlockReason(driver.getBlockReason());
        dto.setActive(driver.isActive());

        // Map vehicle if exists
        if (driver.getVehicle() != null) {
            dto.setVehicleMake(driver.getVehicle().getMake());
            dto.setVehicleModel(driver.getVehicle().getModel());
            dto.setVehicleType(driver.getVehicle().getType());
            dto.setVehicleLicensePlate(driver.getVehicle().getLicensePlate());
            dto.setVehicleColor(driver.getVehicle().getColor());
            dto.setVehicleBabyFriendly(driver.getVehicle().isBabyFriendly());
            dto.setVehiclePetFriendly(driver.getVehicle().isPetFriendly());
        }

        return dto;
    }

    private DriverDTO mapToProfileDTO(Driver driver) {
        DriverDTO profile = new DriverDTO();
        profile.setId(driver.getId());
        profile.setName(driver.getName());
        profile.setLastName(driver.getLastName());
        profile.setEmail(driver.getEmail());
        profile.setPhoneNumber(driver.getPhoneNumber());
        profile.setProfilePhotoPath(driver.getProfilePhotoPath());
        profile.setBlocked(driver.isBlocked());
        profile.setBlockReason(driver.getBlockReason());
        // profile.setActive(driver.isActive());

        if (driver.getVehicle() != null) {
            profile.setVehicleLicensePlate(driver.getVehicle().getLicensePlate());
            profile.setVehicleMake(driver.getVehicle().getMake());
            profile.setVehicleModel(driver.getVehicle().getModel());
            profile.setVehicleColor(driver.getVehicle().getColor());
            profile.setVehicleType(driver.getVehicle().getType());
            profile.setVehicleBabyFriendly(driver.getVehicle().isBabyFriendly());
            profile.setVehiclePetFriendly(driver.getVehicle().isPetFriendly());
        }

        // TODO: Calculate actual values
        /*profile.setActiveMinutesLast24h(0L);
        profile.setCanActivate(!driver.isActive()); // Simple logic
        profile.setLastActiveChange(java.time.LocalDateTime.now()); // TODO: Track actual*/

        return profile;
    }

    private com.backend.lavugio.dto.vehicle.VehicleDTO mapVehicleToDTO(Vehicle vehicle) {
        com.backend.lavugio.dto.vehicle.VehicleDTO dto = new com.backend.lavugio.dto.vehicle.VehicleDTO();
        dto.setId(vehicle.getId());
        dto.setLicensePlate(vehicle.getLicensePlate());
        dto.setMake(vehicle.getMake());
        dto.setModel(vehicle.getModel());
        dto.setColor(vehicle.getColor());
        dto.setType(vehicle.getType());
        dto.setBabyFriendly(vehicle.isBabyFriendly());
        dto.setPetFriendly(vehicle.isPetFriendly());
        return dto;
    }

    private DriverStatusDTO createDriverStatusDTO(Driver driver) {
        DriverStatusDTO status = new DriverStatusDTO();
        status.setDriverId(driver.getId());
        // status.setActive(driver.isActive());
        status.setLastStatusChange(java.time.LocalDateTime.now()); // TODO: Track actual

        // TODO: Calculate from activity sessions
        status.setActiveMinutesLast24h(0L);

        // Calculate remaining minutes (8 hours = 480 minutes)
        long remaining = 480L - status.getActiveMinutesLast24h();
        status.setRemainingMinutesToday(Math.max(0, remaining));

        // Can activate if not active and hasn't exceeded 8 hours
        // status.setCanActivate(!driver.isActive() && status.getRemainingMinutesToday() > 0);

        return status;
    }


}