package com.backend.lavugio.service.vehicle.impl;

import com.backend.lavugio.dto.vehicle.VehicleDTO;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.model.enums.VehicleType;
import com.backend.lavugio.repository.user.DriverRepository;
import com.backend.lavugio.repository.vehicle.VehicleRepository;
import com.backend.lavugio.service.vehicle.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Override
    @Transactional
    public Vehicle createVehicle(Vehicle vehicle) {
        // Provera da li registracija već postoji
        if (vehicleRepository.existsByLicensePlate(vehicle.getLicensePlate())) {
            throw new RuntimeException("License plate already exists: " + vehicle.getLicensePlate());
        }

        // Validacija
        validateVehicle(vehicle);

        return vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public Vehicle updateVehicle(Long id, Vehicle vehicle) {
        Vehicle existing = getVehicleById(id);

        // Provera da li se registracija menja i da li nova već postoji
        if (!existing.getLicensePlate().equals(vehicle.getLicensePlate()) &&
                vehicleRepository.existsByLicensePlate(vehicle.getLicensePlate())) {
            throw new RuntimeException("License plate already exists: " + vehicle.getLicensePlate());
        }

        // Validacija
        validateVehicle(vehicle);

        existing.setMake(vehicle.getMake());
        existing.setModel(vehicle.getModel());
        existing.setLicensePlate(vehicle.getLicensePlate());
        existing.setSeatsNumber(vehicle.getSeatsNumber());
        existing.setPetFriendly(vehicle.isPetFriendly());
        existing.setBabyFriendly(vehicle.isBabyFriendly());
        existing.setColor(vehicle.getColor());
        existing.setType(vehicle.getType());

        return vehicleRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = getVehicleById(id);

        // TODO: Proveriti da li je vozilo dodeljeno vozaču pre brisanja
        // if (vehicle.getDriver() != null) {
        //     throw new RuntimeException("Cannot delete vehicle assigned to a driver");
        // }

        vehicleRepository.delete(vehicle);
    }

    @Override
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + id));
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public List<Vehicle> getVehiclesByMake(String make) {
        return vehicleRepository.findByMake(make);
    }

    @Override
    public List<Vehicle> getVehiclesByType(VehicleType type) {
        return vehicleRepository.findByType(type);
    }

    @Override
    public List<Vehicle> getPetFriendlyVehicles() {
        return vehicleRepository.findByPetFriendlyTrue();
    }

    @Override
    public List<Vehicle> getBabyFriendlyVehicles() {
        return vehicleRepository.findByBabyFriendlyTrue();
    }

    @Override
    public List<Vehicle> getVehiclesBySeats(int minSeats) {
        return vehicleRepository.findBySeatsNumberGreaterThanEqual(minSeats);
    }

    @Override
    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findAvailableVehicles();
    }

    @Override
    public Vehicle getVehicleByLicensePlate(String licensePlate) {
        return vehicleRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with license plate: " + licensePlate));
    }

    @Override
    public boolean isLicensePlateTaken(String licensePlate) {
        return vehicleRepository.existsByLicensePlate(licensePlate);
    }

    @Override
    public List<String> getAllVehicleMakes() {
        return vehicleRepository.findAllMakes();
    }

    @Override
    public List<String> getModelsByMake(String make) {
        return vehicleRepository.findModelsByMake(make);
    }

    @Override
    public List<Vehicle> searchVehicles(String make, String model, VehicleType type,
                                        Integer minSeats, Boolean petFriendly, Boolean babyFriendly) {
        return vehicleRepository.searchVehicles(make, model, type, minSeats, petFriendly, babyFriendly);
    }

    @Override
    public long countPetFriendlyVehicles() {
        return vehicleRepository.countPetFriendlyVehicles();
    }

    @Override
    public long countBabyFriendlyVehicles() {
        return vehicleRepository.countBabyFriendlyVehicles();
    }

    @Override
    public long countAvailableVehicles() {
        return vehicleRepository.countAvailableVehicles();
    }

    private void validateVehicle(Vehicle vehicle) {
        if (vehicle.getSeatsNumber() <= 0) {
            throw new RuntimeException("Seats number must be greater than 0");
        }

        if (vehicle.getLicensePlate() == null || vehicle.getLicensePlate().trim().isEmpty()) {
            throw new RuntimeException("License plate is required");
        }

        if (vehicle.getMake() == null || vehicle.getMake().trim().isEmpty()) {
            throw new RuntimeException("Make is required");
        }

        if (vehicle.getModel() == null || vehicle.getModel().trim().isEmpty()) {
            throw new RuntimeException("Model is required");
        }

        if (vehicle.getColor() == null || vehicle.getColor().trim().isEmpty()) {
            throw new RuntimeException("Color is required");
        }

        if (vehicle.getType() == null) {
            throw new RuntimeException("Vehicle type is required");
        }
    }

    @Override
    public VehicleDTO getVehicleByDriverIdDTO(Long driverId) {
        // Get driver first to ensure they exist
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        // Check if driver has a vehicle
        Vehicle vehicle = driver.getVehicle();
        if (vehicle == null) {
            throw new RuntimeException("Driver has no vehicle assigned");
        }

        return mapVehicleToDTO(vehicle);
    }

    @Override
    public VehicleDTO getVehicleByIdDTO(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));

        return mapVehicleToDTO(vehicle);
    }

    // Reuse the existing helper method
    private VehicleDTO mapVehicleToDTO(Vehicle vehicle) {
        VehicleDTO dto = new VehicleDTO();
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

}