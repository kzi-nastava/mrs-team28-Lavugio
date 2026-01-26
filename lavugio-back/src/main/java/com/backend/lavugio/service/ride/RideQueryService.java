package com.backend.lavugio.service.ride;

import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.enums.RideStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for querying rides without modifying them.
 * This service exists to break circular dependency between RideService and DriverAvailabilityService.
 */
public interface RideQueryService {

    /**
     * Get a ride by its ID
     */
    Ride getRideById(Long id);

    /**
     * Get all rides for a specific driver
     */
    List<Ride> getRidesByDriverId(Long driverId);

    /**
     * Get all rides for a specific passenger
     */
    List<Ride> getRidesByPassengerId(Long passengerId);

    /**
     * Get all rides with a specific status
     */
    List<Ride> getRidesByStatus(RideStatus status);

    /**
     * Get all active rides
     */
    List<Ride> getActiveRides();

    /**
     * Get scheduled rides for a specific driver
     */
    List<Ride> getScheduledRidesForDriver(Long driverId);

    /**
     * Get upcoming rides for a specific driver
     */
    List<Ride> getUpcomingRidesForDriver(Long driverId);

    /**
     * Get all rides
     */
    List<Ride> getAllRides();

    /**
     * Get rides in a date range
     */
    List<Ride> getRidesInDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Calculate total earnings for a driver
     */
    Float calculateTotalEarningsForDriver(Long driverId);

    /**
     * Calculate total distance for a driver
     */
    Float calculateTotalDistanceForDriver(Long driverId);

    /**
     * Calculate average fare for a driver
     */
    Float calculateAverageFareForDriver(Long driverId);
}
