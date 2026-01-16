package com.backend.lavugio.repository.ride;

import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.user.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    // Find by driver
    List<Ride> findByDriver(Driver driver);
    List<Ride> findByDriverId(Long driverId);

    // Find by status
    List<Ride> findByRideStatus(RideStatus status);

    // Combined queries
    List<Ride> findByDriverIdAndRideStatus(Long driverId, RideStatus status);

    // Find by price
    List<Ride> findByPriceGreaterThan(float minPrice);
    List<Ride> findByPriceBetween(float minPrice, float maxPrice);

    // Find by passengers (ManyToMany relationship)

    @Query("SELECT r FROM Ride r JOIN r.passengers p WHERE p.id = :passengerId")
    List<Ride> findByPassengerId(@Param("passengerId") Long passengerId);

    // Count queries
    long countByRideStatus(RideStatus status);
    long countByDriverId(Long driverId);

    // Custom queries
    @Query("SELECT r FROM Ride r WHERE r.driver.id = :driverId " +
            "AND r.startDateTime >= :fromDate " +
            "AND r.rideStatus = 'SCHEDULED' " +
            "ORDER BY r.startDateTime ASC")
    List<Ride> findUpcomingRidesByDriver(@Param("driverId") Long driverId,
                                         @Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT r FROM Ride r WHERE r.rideStatus IN ('SCHEDULED', 'ACTIVE')")
    List<Ride> findAllActiveRides();

    @Query("""
        SELECT r
        FROM Ride r
        WHERE r.rideStatus = :status
          AND r.driver.id = :driverId
    """)
    List<Ride> findAllRidesForDriverByStatus(
            @Param("driverId") Long driverId,
            @Param("status") RideStatus status
    );

    @Query("SELECT r FROM Ride r JOIN r.passengers p " +
            "WHERE p.id = :passengerId " +
            "AND r.startDateTime BETWEEN :startDate AND :endDate " +
            "AND r.endDateTime BETWEEN :startDate AND :endDate " +
            "ORDER BY r.startDateTime DESC")
    List<Ride> findRidesForPassengerInDateRange(@Param("passengerId") Long passengerId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    // Aggregation queries
    @Query("SELECT SUM(r.price) FROM Ride r " +
            "WHERE r.driver.id = :driverId " +
            "AND r.rideStatus = 'FINISHED' ")
    Optional<Float> calculateTotalEarningsForDriver(@Param("driverId") Long driverId);

    @Query("SELECT SUM(r.distance) FROM Ride r " +
            "WHERE r.driver.id = :driverId " +
            "AND r.rideStatus = 'FINISHED'")
    Optional<Float> calculateTotalDistanceForDriver(@Param("driverId") Long driverId);

    @Query("SELECT AVG(r.price) FROM Ride r " +
            "WHERE r.driver.id = :driverId " +
            "AND r.rideStatus = 'FINISHED'")
    Optional<Float> calculateAverageFareForDriver(@Param("driverId") Long driverId);

    // Find rides with multiple passengers
    @Query("SELECT r FROM Ride r WHERE SIZE(r.passengers) > 1")
    List<Ride> findRidesWithMultiplePassengers();

    // Find available rides for a date
    @Query("SELECT r FROM Ride r " +
            "WHERE r.startDateTime = :date " +
            "AND r.rideStatus = 'SCHEDULED' ")
    List<Ride> findAvailableRidesForDate(@Param("date") LocalDateTime date);

    List<Ride> findByStartDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Ride> findByStartDateTime(LocalDateTime date);
}