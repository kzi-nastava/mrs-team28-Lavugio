package com.backend.lavugio.repository.vehicle;

import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.model.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);

    boolean existsByLicensePlate(String licensePlate);

    List<Vehicle> findByMake(String make);

    List<Vehicle> findByMakeAndModel(String make, String model);

    List<Vehicle> findByType(VehicleType type);

    List<Vehicle> findBySeatsNumberGreaterThanEqual(int minSeats);

    List<Vehicle> findBySeatsNumber(int seatsNumber);

    List<Vehicle> findByPetFriendlyTrue();

    List<Vehicle> findByBabyFriendlyTrue();

    List<Vehicle> findByPetFriendlyTrueAndBabyFriendlyTrue();

    List<Vehicle> findByColor(String color);

    @Query("SELECT DISTINCT v.make FROM Vehicle v ORDER BY v.make")
    List<String> findAllMakes();

    @Query("SELECT DISTINCT v.model FROM Vehicle v WHERE v.make = :make ORDER BY v.model")
    List<String> findModelsByMake(String make);

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.petFriendly = true")
    long countPetFriendlyVehicles();

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.babyFriendly = true")
    long countBabyFriendlyVehicles();

    @Query("SELECT v FROM Vehicle v WHERE " +
            "(:make IS NULL OR v.make = :make) AND " +
            "(:model IS NULL OR v.model = :model) AND " +
            "(:type IS NULL OR v.type = :type) AND " +
            "(:minSeats IS NULL OR v.seatsNumber >= :minSeats) AND " +
            "(:petFriendly IS NULL OR v.petFriendly = :petFriendly) AND " +
            "(:babyFriendly IS NULL OR v.babyFriendly = :babyFriendly)")
    List<Vehicle> searchVehicles(String make, String model, VehicleType type,
                                 Integer minSeats, Boolean petFriendly, Boolean babyFriendly);

    @Query("SELECT v FROM Vehicle v WHERE v.id NOT IN " +
            "(SELECT d.vehicle.id FROM Driver d WHERE d.vehicle IS NOT NULL)")
    List<Vehicle> findAvailableVehicles();

    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.id NOT IN " +
            "(SELECT d.vehicle.id FROM Driver d WHERE d.vehicle IS NOT NULL)")
    long countAvailableVehicles();
}