package com.backend.lavugio.service.vehicle;

import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.model.vehicle.VehicleType;

import java.util.List;

public interface VehicleService {
    Vehicle createVehicle(Vehicle vehicle);
    Vehicle updateVehicle(Long id, Vehicle vehicle);
    void deleteVehicle(Long id);
    Vehicle getVehicleById(Long id);
    List<Vehicle> getAllVehicles();
    List<Vehicle> getVehiclesByMake(String make);
    List<Vehicle> getVehiclesByType(VehicleType type);
    List<Vehicle> getPetFriendlyVehicles();
    List<Vehicle> getBabyFriendlyVehicles();
    List<Vehicle> getVehiclesBySeats(int minSeats);
    List<Vehicle> getAvailableVehicles();
    Vehicle getVehicleByLicensePlate(String licensePlate);
    boolean isLicensePlateTaken(String licensePlate);
    List<String> getAllVehicleMakes();
    List<String> getModelsByMake(String make);
    List<Vehicle> searchVehicles(String make, String model, VehicleType type,
                                 Integer minSeats, Boolean petFriendly, Boolean babyFriendly);
    long countPetFriendlyVehicles();
    long countBabyFriendlyVehicles();
    long countAvailableVehicles();
}