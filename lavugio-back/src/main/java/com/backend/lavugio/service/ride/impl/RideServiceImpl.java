package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.dto.ride.*;
import com.backend.lavugio.dto.user.DriverLocationDTO;
import com.backend.lavugio.model.enums.DriverHistorySortFieldEnum;
import com.backend.lavugio.model.enums.VehicleType;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.route.RideDestination;
import com.backend.lavugio.model.route.Address;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.DriverLocation;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.repository.ride.RideRepository;
import com.backend.lavugio.repository.user.RegularUserRepository;
import com.backend.lavugio.service.pricing.PricingService;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.ride.RideQueryService;
import com.backend.lavugio.service.route.RideDestinationService;
import com.backend.lavugio.service.user.DriverActivityService;
import com.backend.lavugio.service.user.DriverAvailabilityService;
import com.backend.lavugio.service.user.DriverService;
import com.backend.lavugio.service.utils.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final RegularUserRepository regularUserRepository;
    private final DriverService driverService;
    private final PricingService pricingService;
    private final RideDestinationService rideDestinationService;
    private final DriverAvailabilityService driverAvailabilityService;
    private final DriverActivityService driverActivityService;
    private final com.backend.lavugio.service.route.AddressService addressService;
    private final RideQueryService rideQueryService;

    @Autowired
    public RideServiceImpl(RideRepository rideRepository,
                           DriverService driverService,
                           PricingService pricingService,
                           RegularUserRepository regularUserRepository,
                           RideDestinationService rideDestinationService,
                           DriverAvailabilityService driverAvailabilityService,
                           DriverActivityService driverActivityService,
                           com.backend.lavugio.service.route.AddressService addressService,
                           RideQueryService rideQueryService) {
        this.rideRepository = rideRepository;
        this.driverService = driverService;
        this.pricingService = pricingService;
        this.regularUserRepository = regularUserRepository;
        this.rideDestinationService = rideDestinationService;
        this.driverAvailabilityService = driverAvailabilityService;
        this.driverActivityService = driverActivityService;
        this.addressService = addressService;
        this.rideQueryService = rideQueryService;
    }

    @Override
    @Transactional
    public Ride createRide(Ride ride) {
        if (ride.getDriver() == null) {
            throw new IllegalArgumentException("Driver cannot be null");
        }
        if (ride.getStartDateTime() == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        if (ride.getEndDateTime() == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }

        if (ride.getRideStatus() == null) {
            ride.setRideStatus(RideStatus.SCHEDULED);
        }

        return rideRepository.save(ride);
    }

    @Override
    public Ride getRideById(Long id) {
        return rideQueryService.getRideById(id);
    }

    @Override
    public List<Ride> getAllRides() {
        return rideQueryService.getAllRides();
    }

    @Override
    public List<Ride> getRidesByDriverId(Long driverId) {
        return rideQueryService.getRidesByDriverId(driverId);
    }

    @Override
    public List<Ride> getRidesByPassengerId(Long passengerId) {
        return rideQueryService.getRidesByPassengerId(passengerId);
    }

    @Override
    public List<Ride> getRidesByDate(LocalDateTime date) {
        return rideRepository.findByStartDateTime(date);
    }

    @Override
    public List<Ride> getRidesByStatus(RideStatus status) {
        return rideQueryService.getRidesByStatus(status);
    }

    @Override
    public List<Ride> getUpcomingRidesForDriver(Long driverId) {
        return rideQueryService.getUpcomingRidesForDriver(driverId);
    }

    @Override
    public List<Ride> getRidesInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return rideQueryService.getRidesInDateRange(startDate, endDate);
    }

    @Override
    public List<Ride> getActiveRides() {
        return rideQueryService.getActiveRides();
    }

    public List<Ride> getScheduledRidesForDriver(Long driverId){
        return rideQueryService.getScheduledRidesForDriver(driverId);
    }

    @Override
    public List<Ride> getFinishedRidesForDriver(Long driverId) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    @Transactional
    public Ride updateRide(Long id, Ride updatedRide) {
        Ride existingRide = getRideById(id);

        if (existingRide.getRideStatus() != RideStatus.SCHEDULED) {
            throw new IllegalStateException("Cannot update ride in status: " + existingRide.getRideStatus());
        }

        existingRide.setStartDateTime(updatedRide.getStartDateTime());
        existingRide.setEndDateTime(updatedRide.getEndDateTime());
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

        return rideRepository.save(ride);
    }

    @Override
    public double estimateRidePrice(RideEstimateRequestDTO request) {
        // TODO: Implement a more sophisticated pricing algorithm
        double priceForDistance = 200*(request.getDistanceMeters() / 1000);
        BigDecimal bd = BigDecimal.valueOf(priceForDistance).setScale(2, BigDecimal.ROUND_HALF_UP);
        priceForDistance = bd.doubleValue();
        if (request.getSelectedVehicleType() == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null");
        }
        switch (request.getSelectedVehicleType().toUpperCase()) {
            case "STANDARD":
                return priceForDistance;
            case "LUXURY":
                return priceForDistance * 1.5f;
            case "COMBI":
                return priceForDistance * 2.0f;
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + request.getSelectedVehicleType());
        }
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

        if (ride.getPassengers().contains(passenger)) {
            throw new IllegalStateException("Passenger already in this ride");
        }

        ride.getPassengers().add(passenger);
        return rideRepository.save(ride);
    }

    @Override
    @Transactional
    public Ride addPassengersToRide(Ride ride, List<String> passengerEmails) {
        Set<RegularUser> registeredPassengers = new HashSet<>();
        for (String passengerEmail : passengerEmails) {
            Optional<RegularUser> passenger = regularUserRepository.findByEmail(passengerEmail);
            if (passenger.isPresent()) {
                registeredPassengers.add(passenger.get());
            } else {
                // TODO: LOGIKA SLANJA MEJLOVA NEREGISTROVANIM PUTNICIMA
            }
        }
        ride.setPassengers(registeredPassengers);
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

        ride.getPassengers().remove(passenger);
        return rideRepository.save(ride);
    }

    @Override
    @Transactional
    public void cancelRide(Long id) {
        Ride ride = getRideById(id);

        if (ride.getRideStatus() == RideStatus.FINISHED) {
            throw new IllegalStateException("Cannot cancel finished ride");
        }

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
        return ride.getRideStatus() == RideStatus.SCHEDULED;
    }

    @Override
    public Float calculateTotalEarningsForDriver(Long driverId) {
        return rideQueryService.calculateTotalEarningsForDriver(driverId);
    }

    @Override
    public Float calculateTotalDistanceForDriver(Long driverId) {
        return rideQueryService.calculateTotalDistanceForDriver(driverId);
    }

    @Override
    public Float calculateAverageFareForDriver(Long driverId) {
        return rideQueryService.calculateAverageFareForDriver(driverId);
    }

    @Override
    public List<Ride> applyParametersToRides(List<Ride> rides, boolean ascending, DriverHistorySortFieldEnum sortBy, String dateRangeStart, String dateRangeEnd) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    @Transactional
    public RideResponseDTO createInstantRide(Long creatorID, RideRequestDTO request) {
        RegularUser creator = regularUserRepository.findById(creatorID)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + creatorID));

        // Find an available driver
        List<DriverLocationDTO> availableDrivers = this.driverAvailabilityService.getDriverLocationsDTO();
        if (availableDrivers.isEmpty()) {
            throw new RuntimeException("There are no available drivers at the moment");
        }

        // Sort the drivers by distance from first location
        List<DriverLocationDTO> sortedDrivers = availableDrivers.stream()
                .sorted(Comparator.comparingDouble(driver ->
                        GeoUtils.distanceKm(
                                request.getStartAddress().getLocation().getLatitude(),
                                request.getStartAddress().getLocation().getLongitude(),
                                driver.getLocation().getLatitude(),
                                driver.getLocation().getLongitude()
                        )
                ))
                .toList();

        for (DriverLocationDTO driverLocationDTO : sortedDrivers) {
            Driver driver = this.driverService.getDriverById(driverLocationDTO.getId());
            boolean isVehicleSuitable = this.isVehicleSuitable(driver.getVehicle(), request.isBabyFriendly(), request.isPetFriendly(), request.getPassengerEmails().size(), request.getVehicleType());
            boolean isDriverUnderDailyLimit = this.isDriverUnderDailyLimit(driver.getId(), request.getEstimatedDurationSeconds());
            boolean driverHasScheduledRideSoon = this.driverHasScheduledRideSoon(driver.getId(), request.getEstimatedDurationSeconds());
            if (isVehicleSuitable && isDriverUnderDailyLimit && !driverHasScheduledRideSoon) {
                Ride ride = createInstantRide(driver, creator, request);
                return mapToRideResponseDTO(ride);
            }
        }
        throw new RuntimeException("There are no available drivers at the moment");
    }

    public RideResponseDTO createScheduledRide(Long creatorID, RideRequestDTO request) {
        RegularUser creator = regularUserRepository.findById(creatorID)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + creatorID));

        List<Driver> allDrivers = this.driverService.getAllDrivers();
        if (allDrivers.isEmpty()) {
            throw new RuntimeException("There are no registered drivers at the moment");
        }

        for (Driver driver : allDrivers) {
            boolean isVehicleSuitable = this.isVehicleSuitable(driver.getVehicle(), request.isBabyFriendly(), request.isPetFriendly(), request.getPassengerEmails().size(), request.getVehicleType());
            boolean isDriverUnderDailyLimitScheduled = this.isDriverUnderDailyLimitScheduled(driver.getId(), request.getEstimatedDurationSeconds(), request.getScheduledTime());
            boolean driverHasScheduledRideSoonScheduled = this.driverHasScheduledRideSoonScheduled(driver.getId(), request.getEstimatedDurationSeconds(), request.getScheduledTime());
            if (isVehicleSuitable && isDriverUnderDailyLimitScheduled && !driverHasScheduledRideSoonScheduled) {
                Ride ride = createScheduledRide(driver, creator, request);
                return mapToRideResponseDTO(ride);
            }
        }
        throw new RuntimeException("There are no available drivers for the scheduled ride at the moment");
    }

    private boolean isVehicleSuitable(Vehicle vehicle, boolean requestBabyFriendly, boolean requestPetFriendly, int passangersNum, VehicleType vehicleType) {
        boolean petSuitable = !requestPetFriendly || vehicle.isPetFriendly();
        boolean babySuitable = !requestBabyFriendly || vehicle.isBabyFriendly();
        boolean passangersSuitable = vehicle.getSeatsNumber()-1 >=  passangersNum;
        boolean vehicleTypeSuitable = vehicleType == vehicle.getType();
        return petSuitable && babySuitable && passangersSuitable && vehicleTypeSuitable;
    }

    private boolean isDriverUnderDailyLimit(Long driverId, int estimatedDurationSeconds) {
        Duration currentActiveTime = this.driverActivityService.getActiveTimeIn24Hours(driverId);
        Duration estimatedRideDuration = Duration.ofSeconds(estimatedDurationSeconds);
        Duration totalActiveTimeAfterRide = currentActiveTime.plus(estimatedRideDuration);

        Duration maxAllowed = Duration.ofHours(8).plus(Duration.ofMinutes(15)); // Maximum daily limit with 15 minutes buffer

        return totalActiveTimeAfterRide.compareTo(maxAllowed) <= 0;
    }

    private boolean isDriverUnderDailyLimitScheduled(Long driverId, int estimatedDurationSeconds, LocalDateTime scheduledTime) {
        Duration currentActiveTime = this.driverActivityService.getActiveTimeIn24Hours(driverId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduledEnd = scheduledTime.plusSeconds(estimatedDurationSeconds);

        Duration additionalActiveIfAlwaysActive = Duration.between(now, scheduledEnd);

        Duration totalActiveTimeAfterRide = currentActiveTime.plus(additionalActiveIfAlwaysActive);

        Duration maxAllowed = Duration.ofHours(8).plus(Duration.ofMinutes(15)); // 8 hours + 15 minutes buffer

        return totalActiveTimeAfterRide.compareTo(maxAllowed) <= 0;
    }

    private boolean driverHasScheduledRideSoon(Long driverId, int estimatedRideDurationSeconds) {
        List<Ride> rides = this.getScheduledRidesForDriver(driverId);
        if (rides.isEmpty()) {
            return false;
        }
        LocalDateTime estimatedNewRideFinishTime = LocalDateTime.now().plusSeconds(estimatedRideDurationSeconds).plusMinutes(15);
        for (Ride ride : rides) {
            if (ride.getStartDateTime().isBefore(estimatedNewRideFinishTime))  {
                return true;
            }
        }
        return false;
    }

    private boolean driverHasScheduledRideSoonScheduled(Long driverId, int estimatedRideDurationSeconds, LocalDateTime scheduledTime) {
        List<Ride> scheduledRides = this.getScheduledRidesForDriver(driverId);

        if (scheduledRides.isEmpty()) {
            return false;
        }

        // Calculate the estimated end time of the new scheduled ride
        LocalDateTime scheduledRideEstimatedEnd = scheduledTime.plusSeconds(estimatedRideDurationSeconds).plusMinutes(15);

        // Check for overlaps with existing scheduled rides
        for (Ride existingRide : scheduledRides) {
            LocalDateTime existingRideStart = existingRide.getStartDateTime();
            LocalDateTime existingRideEnd = existingRideStart
                    .plusSeconds(existingRide.getEstimatedDurationSeconds())
                    .plusMinutes(15);

            // Check for overlap
            if (scheduledTime.isBefore(existingRideEnd) && existingRideStart.isBefore(scheduledRideEstimatedEnd)) {
                return true;
            }
        }

        return false;
    }

    private Ride createInstantRide(Driver driver, RegularUser creator, RideRequestDTO request) {
        Ride ride = new Ride();
        ride.setCreator(creator);
        ride.setDriver(driver);
        ride.setStartDateTime(LocalDateTime.now());
        ride.setEstimatedDurationSeconds(request.getEstimatedDurationSeconds());
        ride.setEndDateTime(null);
        ride.setPrice(request.getPrice());
        ride.setDistance(request.getDistance());
        ride.setRideStatus(RideStatus.SCHEDULED);
        ride.setHasPanic(false);

        // Save the ride
        rideRepository.save(ride);

        // Map and persist destinations from request to ride
        if (request.getDestinations() != null && !request.getDestinations().isEmpty()) {
            for (RideDestinationDTO destDTO : request.getDestinations()) {
                // Build Address entity from DTO
                Address address = new Address();
                address.setStreetName(destDTO.getStreetName());
                address.setCity(destDTO.getCity());
                address.setCountry(destDTO.getCountry());
                // Backend Address expects String streetNumber
                address.setStreetNumber(String.valueOf(destDTO.getStreetNumber()));
                address.setZipCode(destDTO.getZipCode());
                address.setLongitude(destDTO.getLocation().getLongitude());
                address.setLatitude(destDTO.getLocation().getLatitude());

                // Persist or reuse existing address
                address = addressService.createAddress(address);

                // Create ride destination linking ride and address
                RideDestination rideDestination = new RideDestination();
                rideDestination.setRide(ride);
                rideDestination.setAddress(address);
                Integer orderIndex = destDTO.getLocation().getOrderIndex();
                // Store as 1-based order
                rideDestination.setDestinationOrder(orderIndex != null ? orderIndex + 1 : null);

                // Persist destination
                rideDestinationService.addDestinationToRide(rideDestination);
            }
        }

        // Add passengers to ride
        this.addPassengersToRide(ride, request.getPassengerEmails());
        return ride;
    }

    private Ride createScheduledRide(Driver driver, RegularUser creator, RideRequestDTO request) {
        Ride ride = new Ride();
        ride.setCreator(creator);
        ride.setDriver(driver);
        ride.setStartDateTime(request.getScheduledTime());
        ride.setEndDateTime(null);
        ride.setEstimatedDurationSeconds(request.getEstimatedDurationSeconds());
        ride.setPrice(request.getPrice());
        ride.setDistance(request.getDistance());
        ride.setRideStatus(RideStatus.SCHEDULED);
        ride.setHasPanic(false);

        // Save the ride
        rideRepository.save(ride);

        // Map and persist destinations from request to ride
        if (request.getDestinations() != null && !request.getDestinations().isEmpty()) {
            for (RideDestinationDTO destDTO : request.getDestinations()) {
                // Build Address entity from DTO
                Address address = new Address();
                address.setStreetName(destDTO.getStreetName());
                address.setCity(destDTO.getCity());
                address.setCountry(destDTO.getCountry());
                // Backend Address expects String streetNumber
                address.setStreetNumber(String.valueOf(destDTO.getStreetNumber()));
                address.setZipCode(destDTO.getZipCode());
                address.setLongitude(destDTO.getLocation().getLongitude());
                address.setLatitude(destDTO.getLocation().getLatitude());

                // Persist or reuse existing address
                address = addressService.createAddress(address);

                // Create ride destination linking ride and address
                RideDestination rideDestination = new RideDestination();
                rideDestination.setRide(ride);
                rideDestination.setAddress(address);
                Integer orderIndex = destDTO.getLocation().getOrderIndex();
                // Store as 1-based order
                rideDestination.setDestinationOrder(orderIndex != null ? orderIndex + 1 : null);

                // Persist destination
                rideDestinationService.addDestinationToRide(rideDestination);
            }
        }

        // Add passengers to ride
        this.addPassengersToRide(ride, request.getPassengerEmails());
        return ride;
    }

    private RideResponseDTO mapToRideResponseDTO(Ride ride) {
        RideResponseDTO response = new RideResponseDTO();
        response.setId(ride.getId());
        response.setStatus(ride.getRideStatus());
        response.setPrice(ride.getPrice());
        response.setDistance(ride.getDistance());
        response.setEstimatedDuration(30); // Placeholder
        // TODO: Map driver, creator, passengers DTOs properly
        return response;
    }

    private void validateStatusTransition(RideStatus currentStatus, RideStatus newStatus) {
        if (currentStatus == RideStatus.FINISHED) {
            throw new IllegalStateException("Cannot change status of finished ride");
        }
        if (currentStatus == RideStatus.CANCELLED && newStatus != RideStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of cancelled ride");
        }
    }

    public Double calculatePrice(VehicleType vehicleType, Double distance){
        Double kilometerPrice = pricingService.getKilometerPricing();
        Double vehicleTypePrice = pricingService.getVehiclePricingByVehicleType(vehicleType);
        return vehicleTypePrice + kilometerPrice * distance;
    }

}