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
    public RideResponseDTO bookRide(String userEmail, RideRequestDTO request) throws Exception {
        validateScheduledTime(request.getScheduledTime());

        // 4. Find available driver if ride is not scheduled
        Driver driver = null;
        if (!request.isScheduled()) {
            driver = findAvailableDriver(
                    request.getStartAddress(),
                    request.getVehicleType(),
                    request.isBabyFriendly(),
                    request.isPetFriendly(),
                    request.getScheduledTime()
            );
            if (driver == null) {
                throw new Exception("No available drivers");
            }
        }
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setDate(LocalDate.now());
        if (request.isScheduled()) {
            ride.setTimeStart(request.getScheduledTime().toLocalTime());
        } else {
            ride.setTimeStart(LocalTime.now());
        }
        ride.setPrice(request.getPrice());
        ride.setDistance(request.getDistance());
        ride.setTimeEnd(ride.getTimeStart() + request.getEstimatedTime());
        if (request.isScheduled()) {
            ride.setRideStatus(RideStatus.SCHEDULED);
        } else {
            ride.setRideStatus(RideStatus.ACTIVE);
        }
        ride.setCancelled(false);
        // 5. Create ride
        //Ride ride = createRide(userEmail, request, route, price, driver);

        // 6. Send notifications
        // sendBookingNotifications(ride);

        // 7. Schedule reminder notifications (for future rides)
        /*if (ride.getScheduledTime() != null) {
            scheduleReminders(ride);
        }*/

        return new RideResponseDTO(ride.getDriver().getName(), ride.getDriver().getLastName());
    }

    @PostMapping("/calculate-ride-info")
    public RideCalculatedInfoDTO calculateRideInfo(BaseRideRequestDTO request) {
        // IMPLEMENTIRAJ LOGIKU ZA DOBIJANJE DUŽINE I OČEKIVANOG TRAJANJA
        // RUTE IZ EXTERNAL API-A
        float distance = 11.3f; // PRIMER VREDNOSTI
        int estimatedTimeMinutes = 25; // PRIMER VREDNOSTI

        VehicleType vehicleType = request.getVehicleType();
        float price;
        if (vehicleType == VehicleType.STANDARD) {
            price = 500f;
        } else if (vehicleType == VehicleType.LUXURY) {
            price = 1000f;
        } else {
            price = 750f;
        }

        price += 120 * distance;

        return new RideCalculatedInfoDTO(price, distance, estimatedTimeMinutes);
    }

    private Ride createRide(String userEmail, RideRequestDTO request,
                            RouteDetails route, float price, Driver driver) {

        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setScheduledTime(request.getScheduledTime());
        ride.setStatus(RideStatus.WAITING);
        ride.setPrice(price);
        ride.setDistance(route.getTotalDistance());
        ride.setEstimatedDuration(route.getEstimatedDuration());
        ride.setBabyFriendly(request.isBabyFriendly());
        ride.setPetFriendly(request.isPetFriendly());
        ride.setVehicleType(request.getPreferredVehicleType());

        // Add stops
        List<Stop> stops = request.getStops().stream()
                .map(stopDTO -> createStop(stopDTO, ride))
                .collect(Collectors.toList());
        ride.setStops(stops);

        // Link passengers
        List<User> passengers = linkPassengers(request.getPassengerEmails(), ride);
        ride.setPassengers(passengers);

        return rideRepository.save(ride);
    }

    private void validateScheduledTime(LocalDateTime scheduledTime) throws Exception {
        if (scheduledTime != null) {
            LocalDateTime maxFuture = LocalDateTime.now().plusHours(5);
            if (scheduledTime.isAfter(maxFuture)) {
                throw new Exception("Ride can be scheduled max 5 hours in advance");
            }
        }
    }

    private Driver findAvailableDriver(RideRequstDestinationDTO startAddress,
                                       VehicleType vehicleType, boolean babyFriendly,
                                       boolean petFriendly, LocalDateTime scheduledTime) {

        List<Driver> availableDrivers = driverService.getAvailableDrivers();

        if (availableDrivers.isEmpty()) {
            return null;
        }

        List<Driver> filteredDrivers = availableDrivers.stream()
                .filter(driver ->
                        (vehicleType == null || driver.getVehicle().getType() == vehicleType) &&
                                (!babyFriendly || driver.getVehicle().isBabyFriendly()) &&
                                (!petFriendly || driver.getVehicle().isPetFriendly())
                                //!driverService.hasExceededWorkHours(driver.getId()) // Check 8-hour limit
                )
                .toList();

        if (filteredDrivers.isEmpty()) {
            return null;
        }

        return filteredDrivers.getFirst();

        // ADD LOGIC WHEN CONNECTED WITH API
        // Sort by proximity and availability
        /*return filteredDrivers.stream()
                .min(Comparator.comparing(driver -> {
                    double proximity = calculateProximity(driver.getLastKnownLocation(), startAddress);
                    LocalDateTime nextAvailable = driverService.getNextAvailableTime(driver.getId());

                    // If scheduled ride, prioritize drivers available at that time
                    if (scheduledTime != null) {
                        return nextAvailable.isBefore(scheduledTime) ? proximity : Double.MAX_VALUE;
                    }

                    return proximity;
                }))
                .orElse(null);*/
    }

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