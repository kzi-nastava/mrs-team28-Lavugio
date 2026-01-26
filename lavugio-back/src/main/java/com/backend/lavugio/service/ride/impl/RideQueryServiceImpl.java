package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.repository.ride.RideRepository;
import com.backend.lavugio.service.ride.RideQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementation of RideQueryService for querying rides without modifying them.
 * This service exists to break circular dependency between RideService and DriverAvailabilityService.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RideQueryServiceImpl implements RideQueryService {

    private final RideRepository rideRepository;

    @Override
    public Ride getRideById(Long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Ride not found with id: " + id));
    }

    @Override
    public List<Ride> getRidesByDriverId(Long driverId) {
        return rideRepository.findByDriverId(driverId);
    }

    @Override
    public List<Ride> getRidesByPassengerId(Long passengerId) {
        return rideRepository.findByPassengerId(passengerId);
    }

    @Override
    public List<Ride> getRidesByStatus(RideStatus status) {
        return rideRepository.findByRideStatus(status);
    }

    @Override
    public List<Ride> getActiveRides() {
        return rideRepository.findAllActiveRides();
    }

    @Override
    public List<Ride> getScheduledRidesForDriver(Long driverId) {
        return rideRepository.findByDriverIdAndRideStatus(driverId, RideStatus.SCHEDULED);
    }

    @Override
    public List<Ride> getUpcomingRidesForDriver(Long driverId) {
        return rideRepository.findUpcomingRidesByDriver(driverId, LocalDateTime.now());
    }

    @Override
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    @Override
    public List<Ride> getRidesInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return rideRepository.findByStartDateTimeBetween(startDate, endDate);
    }

    @Override
    public Float calculateTotalEarningsForDriver(Long driverId) {
        return rideRepository.calculateTotalEarningsForDriver(driverId).orElse(0.0f);
    }

    @Override
    public Float calculateTotalDistanceForDriver(Long driverId) {
        return rideRepository.calculateTotalDistanceForDriver(driverId).orElse(0.0f);
    }

    @Override
    public Float calculateAverageFareForDriver(Long driverId) {
        return rideRepository.calculateAverageFareForDriver(driverId).orElse(0.0f);
    }
}
