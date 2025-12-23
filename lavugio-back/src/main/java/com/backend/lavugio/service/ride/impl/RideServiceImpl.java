package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.dto.ride.*;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.ride.RideStatus;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.vehicle.VehicleType;
import com.backend.lavugio.repository.ride.RideRepository;
import com.backend.lavugio.repository.user.RegularUserRepository;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.user.DriverService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RideServiceImpl implements RideService {

    @Autowired
    private final RideRepository rideRepository;
    @Autowired
    private final RegularUserRepository regularUserRepository;
    @Autowired
    private final DriverService driverService;

    @Override
    @Transactional
    public Ride createRide(Ride ride) {
        // DRIVER CAN BE NULL IF RIDE IS SCHEDULED
        //if (ride.getDriver() == null) {
        //    throw new IllegalArgumentException("Driver cannot be null");
        //}
        if (ride.getDate() == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        ride.setCancelled(false);
        if (ride.getRideStatus() == null) {
            ride.setRideStatus(RideStatus.SCHEDULED);
        }

        return rideRepository.save(ride);
    }

    @Override
    public Ride getRideById(Long id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + id));
    }

    @Override
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
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
    public List<Ride> getRidesByDate(LocalDate date) {
        return rideRepository.findByDate(date);
    }

    @Override
    public List<Ride> getRidesByStatus(RideStatus status) {
        return rideRepository.findByRideStatus(status);
    }

    @Override
    public List<Ride> getUpcomingRidesForDriver(Long driverId) {
        return rideRepository.findUpcomingRidesByDriver(driverId, LocalDate.now());
    }

    @Override
    public List<Ride> getRidesInDateRange(LocalDate startDate, LocalDate endDate) {
        return rideRepository.findByDateBetween(startDate, endDate);
    }

    @Override
    public List<Ride> getActiveRides() {
        return rideRepository.findAllActiveRides();
    }

    @Override
    @Transactional
    public Ride updateRide(Long id, Ride updatedRide) {
        Ride existingRide = getRideById(id);

        if (existingRide.getRideStatus() != RideStatus.SCHEDULED) {
            throw new IllegalStateException("Cannot update ride in status: " + existingRide.getRideStatus());
        }

        existingRide.setDate(updatedRide.getDate());
        existingRide.setTimeStart(updatedRide.getTimeStart());
        existingRide.setTimeEnd(updatedRide.getTimeEnd());
        existingRide.setPrice(updatedRide.getPrice());
        existingRide.setDistance(updatedRide.getDistance());

        return rideRepository.save(existingRide);
    }

    @Override
    @Transactional
    public Ride updateRideStatus(Long id, RideStatus newStatus) {
        Ride ride = getRideById(id);
        validateStatusTransition(ride.getRideStatus(), newStatus);

        ride.setRideStatus(newStatus);
        if (newStatus == RideStatus.CANCELLED) {
            ride.setCancelled(true);
        }

        return rideRepository.save(ride);
    }

    @Override
    @Transactional
    public Ride addPassengerToRide(Long rideId, Long passengerId) {
        Ride ride = getRideById(rideId);
        RegularUser passenger = regularUserRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + passengerId));

        if (ride.getRideStatus() != RideStatus.SCHEDULED) {
            throw new IllegalStateException("Cannot add passenger to ride in status: " + ride.getRideStatus());
        }

        if (ride.getPassangers().contains(passenger)) {
            throw new IllegalStateException("Passenger already in this ride");
        }

        ride.getPassangers().add(passenger);
        return rideRepository.save(ride);
    }

    @Override
    @Transactional
    public Ride addPassengerToRide(Ride ride, List<String> passengerEmails) {
        for (String passengerEmail : passengerEmails) {
            RegularUser passenger = regularUserRepository.findByEmail(passengerEmail)
                    .orElseThrow(() -> new RuntimeException("Passenger not found with email: " + passengerEmail));
            if (ride.getRideStatus() != RideStatus.SCHEDULED) {
                throw new IllegalStateException("Cannot add passenger to ride in status: " + ride.getRideStatus());
            }

            if (ride.getPassangers().contains(passenger)) {
                throw new IllegalStateException("Passenger already in this ride");
            }
            ride.getPassangers().add(passenger);
        }
        return rideRepository.save(ride);
    }

    @Override
    @Transactional
    public Ride removePassengerFromRide(Long rideId, Long passengerId) {
        Ride ride = getRideById(rideId);
        RegularUser passenger = regularUserRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + passengerId));

        if (ride.getRideStatus() == RideStatus.FINISHED) {
            throw new IllegalStateException("Cannot remove passenger from finished ride");
        }

        ride.getPassangers().remove(passenger);
        return rideRepository.save(ride);
    }

    @Override
    @Transactional
    public void cancelRide(Long id) {
        Ride ride = getRideById(id);

        if (ride.getRideStatus() == RideStatus.FINISHED) {
            throw new IllegalStateException("Cannot cancel finished ride");
        }

        ride.setCancelled(true);
        ride.setRideStatus(RideStatus.CANCELLED);
        rideRepository.save(ride);
    }

    @Override
    @Transactional
    public void deleteRide(Long id) {
        if (!rideRepository.existsById(id)) {
            throw new RuntimeException("Ride not found with id: " + id);
        }

        Ride ride = getRideById(id);
        if (ride.getRideStatus() != RideStatus.CANCELLED) {
            throw new IllegalStateException("Can only delete cancelled rides");
        }

        rideRepository.deleteById(id);
    }

    @Override
    public boolean isRideAvailable(Long rideId) {
        Ride ride = getRideById(rideId);
        return ride.getRideStatus() == RideStatus.SCHEDULED && !ride.isCancelled();
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

    private void validateStatusTransition(RideStatus currentStatus, RideStatus newStatus) {
        if (currentStatus == RideStatus.FINISHED) {
            throw new IllegalStateException("Cannot change status of finished ride");
        }
        if (currentStatus == RideStatus.CANCELLED && newStatus != RideStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of cancelled ride");
        }
    }
}