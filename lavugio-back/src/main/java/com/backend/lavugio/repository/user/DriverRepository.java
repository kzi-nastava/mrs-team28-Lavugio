package com.backend.lavugio.repository.user;

import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByEmail(String email);
    List<Driver> findByActiveTrue();
    List<Driver> findByActiveFalse();
    List<Driver> findByBlockedFalseAndActiveTrue();
    Optional<Driver> findByVehicle(Vehicle vehicle);
    List<Driver> findByVehicleIsNull();
    long countByActiveTrue();
    boolean existsByVehicle(Vehicle vehicle);
}