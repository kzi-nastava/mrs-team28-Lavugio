package com.backend.lavugio.repository.ride;

import com.backend.lavugio.model.ride.RideReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideReportRepository extends JpaRepository<RideReport, Long> {

    // Find reports by ride
    List<RideReport> findByRide(com.backend.lavugio.model.ride.Ride ride);
    List<RideReport> findByRideId(Long rideId);

    // Count reports for a ride
    long countByRideId(Long rideId);

    // Check if ride has been reported
    boolean existsByRideId(Long rideId);

    // Find rides with multiple reports (might indicate serious issues)
    @Query("SELECT rr.ride.id, COUNT(rr) as report_count " +
            "FROM RideReport rr " +
            "GROUP BY rr.ride.id " +
            "HAVING COUNT(rr) > :threshold")
    List<Object[]> findRidesWithMultipleReports(@Param("threshold") long threshold);

    // Find all reports for a driver's rides
    @Query("SELECT rr FROM RideReport rr WHERE rr.ride.driver.id = :driverId")
    List<RideReport> findAllReportsForDriver(@Param("driverId") Long driverId);

    // Count reports for a driver
    @Query("SELECT COUNT(rr) FROM RideReport rr WHERE rr.ride.driver.id = :driverId")
    long countReportsForDriver(@Param("driverId") Long driverId);
}