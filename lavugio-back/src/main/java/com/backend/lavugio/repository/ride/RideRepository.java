package com.backend.lavugio.repository.ride;

import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.ride.RideStatus;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    // Find by driver
    List<Ride> findByDriver(Driver driver);
    List<Ride> findByDriverId(Long driverId);

    // Find by date
    List<Ride> findByDate(LocalDate date);
    List<Ride> findByDateBetween(LocalDate startDate, LocalDate endDate);

    // Find by status
    List<Ride> findByRideStatus(RideStatus status);

    // Find cancelled/active rides
    List<Ride> findByCancelledTrue();
    List<Ride> findByCancelledFalse();

    // Combined queries
    List<Ride> findByDriverIdAndRideStatus(Long driverId, RideStatus status);
    List<Ride> findByDriverIdAndDate(Long driverId, LocalDate date);
    List<Ride> findByDateAndTimeStartAfter(LocalDate date, LocalTime time);

    // Find by price
    List<Ride> findByPriceGreaterThan(float minPrice);
    List<Ride> findByPriceBetween(float minPrice, float maxPrice);

    // Find by passengers (ManyToMany relationship)
    List<Ride> findByPassangersContaining(RegularUser passenger);

    @Query("SELECT r FROM Ride r JOIN r.passengers p WHERE p.id = :passengerId")
    List<Ride> findByPassengerId(@Param("passengerId") Long passengerId);

    // Existence checks
    boolean existsByDriverIdAndDate(Long driverId, LocalDate date);

    // Count queries
    long countByRideStatus(RideStatus status);
    long countByDriverId(Long driverId);

    // Custom queries
    @Query("SELECT r FROM Ride r WHERE r.driver.id = :driverId " +
            "AND r.date >= :fromDate " +
            "AND r.cancelled = false " +
            "ORDER BY r.date ASC, r.timeStart ASC")
    List<Ride> findUpcomingRidesByDriver(@Param("driverId") Long driverId,
                                         @Param("fromDate") LocalDate fromDate);

    @Query("SELECT r FROM Ride r WHERE r.rideStatus IN ('SCHEDULED', 'ACTIVE') " +
            "AND r.cancelled = false")
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
            "AND r.date BETWEEN :startDate AND :endDate " +
            "ORDER BY r.date DESC")
    List<Ride> findRidesForPassengerInDateRange(@Param("passengerId") Long passengerId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    // Aggregation queries
    @Query("SELECT SUM(r.price) FROM Ride r " +
            "WHERE r.driver.id = :driverId " +
            "AND r.rideStatus = 'FINISHED' " +
            "AND r.cancelled = false")
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
            "WHERE r.date = :date " +
            "AND r.rideStatus = 'SCHEDULED' " +
            "AND r.cancelled = false")
    List<Ride> findAvailableRidesForDate(@Param("date") LocalDate date);
}