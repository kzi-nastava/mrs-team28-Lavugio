package com.backend.lavugio.repository.user;

import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByEmail(String email);
    List<Driver> findByIsDrivingTrue();
    List<Driver> findByIsDrivingFalse();
    List<Driver> findByBlockedFalseAndIsDrivingTrue();
    Optional<Driver> findByVehicle(Vehicle vehicle);
    List<Driver> findByVehicleIsNull();
    long countByIsDrivingTrue();
    boolean existsByVehicle(Vehicle vehicle);

    @Modifying
    @Query("UPDATE Driver d SET d.isActive = false")
    void setAllDriversInactive();
}