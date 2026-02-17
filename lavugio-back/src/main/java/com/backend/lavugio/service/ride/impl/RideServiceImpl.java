package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.dto.ride.*;
import com.backend.lavugio.dto.user.AdminHistoryDTO;
import com.backend.lavugio.dto.user.AdminHistoryDetailedDTO;
import com.backend.lavugio.dto.user.AdminHistoryPagingDTO;
import com.backend.lavugio.dto.user.DriverLocationDTO;
import com.backend.lavugio.dto.user.DriverHistoryDTO;
import com.backend.lavugio.dto.user.DriverHistoryDetailedDTO;
import com.backend.lavugio.dto.user.DriverHistoryPagingDTO;
import com.backend.lavugio.dto.user.PassengerTableRowDTO;
import com.backend.lavugio.dto.user.UserHistoryDTO;
import com.backend.lavugio.dto.user.UserHistoryDetailedDTO;
import com.backend.lavugio.dto.user.UserHistoryPagingDTO;
import com.backend.lavugio.model.enums.DriverHistorySortFieldEnum;
import com.backend.lavugio.model.enums.DriverStatusEnum;
import com.backend.lavugio.model.enums.VehicleType;
import com.backend.lavugio.model.notification.Notification;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.ride.Review;
import com.backend.lavugio.model.ride.RideReport;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.route.RideDestination;
import com.backend.lavugio.model.route.Address;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.repository.ride.RideRepository;
import com.backend.lavugio.repository.ride.ReviewRepository;
import com.backend.lavugio.repository.ride.RideReportRepository;
import com.backend.lavugio.repository.user.RegularUserRepository;
import com.backend.lavugio.service.pricing.PricingService;
import com.backend.lavugio.service.ride.RideOverviewService;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.ride.RideQueryService;
import com.backend.lavugio.service.route.RideDestinationService;
import com.backend.lavugio.service.user.DriverActivityService;
import com.backend.lavugio.service.user.DriverAvailabilityService;
import com.backend.lavugio.service.user.DriverService;
import com.backend.lavugio.service.utils.EmailService;
import com.backend.lavugio.service.utils.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

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
    private final EmailService emailService;
    private final com.backend.lavugio.service.notification.NotificationService notificationService;
    private final ReviewRepository reviewRepository;
    private final RideReportRepository rideReportRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public RideServiceImpl(RideRepository rideRepository,
                           DriverService driverService,
                           PricingService pricingService,
                           RegularUserRepository regularUserRepository,
                           RideDestinationService rideDestinationService,
                           DriverAvailabilityService driverAvailabilityService,
                           DriverActivityService driverActivityService,
                           com.backend.lavugio.service.route.AddressService addressService,
                           RideQueryService rideQueryService,
                           EmailService emailService,
                           com.backend.lavugio.service.notification.NotificationService notificationService,
                           ReviewRepository reviewRepository,
                           RideReportRepository rideReportRepository,
                           SimpMessagingTemplate simpMessagingTemplate) {
        this.rideRepository = rideRepository;
        this.driverService = driverService;
        this.pricingService = pricingService;
        this.regularUserRepository = regularUserRepository;
        this.rideDestinationService = rideDestinationService;
        this.driverAvailabilityService = driverAvailabilityService;
        this.driverActivityService = driverActivityService;
        this.addressService = addressService;
        this.rideQueryService = rideQueryService;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.reviewRepository = reviewRepository;
        this.rideReportRepository = rideReportRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
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
    public List<Ride> getRidesByCreatorAndStatus(Long creatorId, RideStatus status) {
        return rideRepository.findByCreatorIdAndStatus(creatorId, status);
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
    public List<Ride> getActiveOrScheduledRides() {
        return rideQueryService.getActiveOrScheduledRides();
    }

    @Override
    public List<RideMonitoringDTO> getActiveRides() {
        return this.rideRepository.findAllActiveRides().stream().map(RideMonitoringDTO::new).toList();
    }

    public List<Ride> getScheduledRidesForDriver(Long driverId){
        System.out.println("Pozvalo se");
        return rideQueryService.getScheduledRidesForDriver(driverId);
    }

    @Override
    public List<Ride> getFinishedRidesForDriver(Long driverId) {
        return rideRepository.findByDriverIdAndRideStatus(driverId, RideStatus.FINISHED);
    }

    @Override
    public List<Ride> getFinishedRidesInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return rideQueryService.getFinishedRidesInDateRange(startDate, endDate);
    }

    @Override
    public List<Ride> getFinishedRidesForDriverInDateRange(Long driverId, LocalDateTime startDate, LocalDateTime endDate) {
        return rideQueryService.getFinishedRidesForDriverInDateRange(driverId, startDate, endDate);
    }

    @Override
    public List<Ride> getFinishedRidesForCreatorInDateRange(Long creatorId, LocalDateTime startDate, LocalDateTime endDate) {
        return rideQueryService.getFinishedRidesForCreatorInDateRange(creatorId, startDate, endDate);
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
    @Transactional
    public void markRideWithPanic(Long rideId) {
        Ride ride = getRideById(rideId);
        ride.setHasPanic(true);
        rideRepository.save(ride);
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

        Notification notification = notificationService.createWebAddedToRideNotification(ride.getId(), passengerId);
        notificationService.sendNotificationToSocket(notification);
        ride.getPassengers().add(passenger);
        return rideRepository.save(ride);
    }

    @Override
    @Transactional
    public Ride addPassengersToRide(Ride ride, List<String> passengerEmails) {
        Set<RegularUser> registeredPassengers = new HashSet<>();
        for (String passengerEmail : passengerEmails) {
            Optional<RegularUser> passenger = regularUserRepository.findByEmail(passengerEmail);
            if (passenger.isEmpty()){
                continue;
            }
            passenger.ifPresent(registeredPassengers::add);
            Notification notification = notificationService.createWebAddedToRideNotification(ride.getId(), passenger.get().getId());
            notificationService.sendNotificationToSocket(notification);
        }
        emailService.sendFoundRideEmail(passengerEmails, ride);
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
    public void cancelRideByDriver(Long rideId, String reason) {
        Ride ride = getRideById(rideId);
        
        if (ride.getRideStatus() == RideStatus.FINISHED) {
            throw new IllegalStateException("Cannot cancel finished ride");
        }
        
        if (ride.getRideStatus() == RideStatus.ACTIVE) {
            throw new IllegalStateException("Cannot cancel active ride. Finish it early instead.");
        }
        
        // Set ride status to cancelled
        ride.setRideStatus(RideStatus.CANCELLED);
        
        // Mark driver as not driving
        if (ride.getDriver() != null) {
            ride.getDriver().setDriving(false);
        }
        
        // Reset creator's canOrder flag
        if (ride.getCreator() != null) {
            ride.getCreator().setCanOrder(true);
        }
        
        rideRepository.save(ride);
        
        // Load passengers eagerly before sending notifications
        Set<RegularUser> passengers = ride.getPassengers();
        System.out.println("Number of passengers on ride: " + passengers.size());
        
        // Send notifications to passengers with cancellation reason
        notificationService.notifyPassengersAboutCancellation(ride, reason, true);
    }

    @Override
    @Transactional
    public void cancelRideByPassenger(Long rideId) {
        Ride ride = getRideById(rideId);
        
        if (ride.getRideStatus() == RideStatus.FINISHED) {
            throw new IllegalStateException("Cannot cancel finished ride");
        }
        
        if (ride.getRideStatus() == RideStatus.ACTIVE) {
            throw new IllegalStateException("Cannot cancel active ride");
        }
        
        // Check if cancellation is within 10 minutes of start time
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = ride.getStartDateTime();
        long minutesUntilStart = java.time.Duration.between(now, startTime).toMinutes();
        
        if (minutesUntilStart < 10) {
            throw new IllegalStateException("Cannot cancel ride less than 10 minutes before start time");
        }
        
        // Set ride status to cancelled
        ride.setRideStatus(RideStatus.CANCELLED);
        
        // Mark driver as not driving
        if (ride.getDriver() != null) {
            ride.getDriver().setDriving(false);
        }
        
        // Reset creator's canOrder flag
        if (ride.getCreator() != null) {
            ride.getCreator().setCanOrder(true);
        }
        
        rideRepository.save(ride);
        
        // Load driver eagerly before sending notification
        if (ride.getDriver() != null) {
            System.out.println("Notifying driver: " + ride.getDriver().getId());
        }
        
        // Notify driver about cancellation
        notificationService.notifyDriverAboutPassengerCancellation(ride);
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
        System.out.println("Found creator: " + creator.getEmail());
        // Find an available driver
        List<DriverLocationDTO> availableDrivers = this.driverAvailabilityService.getDriverLocationsDTO();
        if (availableDrivers.isEmpty()) {
            System.out.println("No available drivers found");
            throw new RuntimeException("No drivers are currently online. Please try again later.");
        }
        System.out.println("Found available drivers: " + availableDrivers.size());
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

        System.out.println("Sorted drivers by distance");
        
        // Track rejection reasons for better error messages
        int vehicleTypeRejections = 0;
        int dailyLimitRejections = 0;
        int scheduledRideRejections = 0;
        int busyDrivers = 0;
        
        for (DriverLocationDTO driverLocationDTO : sortedDrivers) {
            // Skip drivers that are busy (on active ride)
            if (driverLocationDTO.getStatus() == DriverStatusEnum.BUSY) {
                busyDrivers++;
                System.out.println("Driver " + driverLocationDTO.getId() + " is busy with an active ride");
                continue;
            }
            
            Driver driver = this.driverService.getDriverById(driverLocationDTO.getId());
            System.out.println("Checking driver: " + driver.getId());
            boolean isVehicleSuitable = this.isVehicleSuitable(driver.getVehicle(), request.isBabyFriendly(), request.isPetFriendly(), request.getPassengerEmails().size(), request.getVehicleType());
            boolean isDriverUnderDailyLimit = this.isDriverUnderDailyLimit(driver.getId(), request.getEstimatedDurationSeconds());
            boolean driverHasScheduledRideSoon = this.driverHasScheduledRideSoon(driver.getId(), request.getEstimatedDurationSeconds());
            System.out.println("Vehicle suitable: " + isVehicleSuitable + ", Under daily limit: " + isDriverUnderDailyLimit + ", Has scheduled ride soon: " + driverHasScheduledRideSoon);
            
            if (!isVehicleSuitable) {
                vehicleTypeRejections++;
                System.out.println("Driver " + driver.getId() + " rejected: vehicle not suitable (requested: " + request.getVehicleType() + ", driver has: " + driver.getVehicle().getType() + ")");
            }
            if (!isDriverUnderDailyLimit) {
                dailyLimitRejections++;
                System.out.println("Driver " + driver.getId() + " rejected: exceeded daily limit");
            }
            if (driverHasScheduledRideSoon) {
                scheduledRideRejections++;
                System.out.println("Driver " + driver.getId() + " rejected: has scheduled ride soon");
            }
            
            if (isVehicleSuitable && isDriverUnderDailyLimit && !driverHasScheduledRideSoon) {
                System.out.println("Assigning driver: " + driver.getId());
                Ride ride = createInstantRide(driver, creator, request);
                return mapToRideResponseDTO(ride);
            }
        }
        
        // Provide specific error message based on rejection reasons
        String errorMessage = buildNoDriverErrorMessage(sortedDrivers.size(), busyDrivers, vehicleTypeRejections, 
                dailyLimitRejections, scheduledRideRejections, request.getVehicleType());
        throw new RuntimeException(errorMessage);
    }

    @Transactional
    public RideResponseDTO createScheduledRide(Long creatorID, RideRequestDTO request) {
        RegularUser creator = regularUserRepository.findById(creatorID)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + creatorID));

        System.out.println("Found creator: " + creator.getEmail());

        List<Driver> allDrivers = this.driverService.getAllDrivers();
        if (allDrivers.isEmpty()) {
            throw new RuntimeException("There are no registered drivers at the moment");
        }

        System.out.println("Got all drivers: " + allDrivers.size());

        for (Driver driver : allDrivers) {
            boolean isVehicleSuitable = this.isVehicleSuitable(driver.getVehicle(), request.isBabyFriendly(), request.isPetFriendly(), request.getPassengerEmails().size(), request.getVehicleType());
            boolean isDriverUnderDailyLimitScheduled = this.isDriverUnderDailyLimitScheduled(driver.getId(), request.getEstimatedDurationSeconds(), request.getScheduledTime());
            boolean driverHasScheduledRideSoonScheduled = this.driverHasScheduledRideSoonScheduled(driver.getId(), request.getEstimatedDurationSeconds(), request.getScheduledTime());
            System.out.println("Is vehicle suitable: " + isVehicleSuitable + ", Under daily limit for scheduled: " + isDriverUnderDailyLimitScheduled + ", Has scheduled ride soon for scheduled: " + driverHasScheduledRideSoonScheduled);
            if (isVehicleSuitable && isDriverUnderDailyLimitScheduled && !driverHasScheduledRideSoonScheduled) {
                System.out.println("Assigning driver for scheduled ride: " + driver.getId());
                Ride ride = createScheduledRide(driver, creator, request);
                return mapToRideResponseDTO(ride);
            }
        }
        throw new RuntimeException("There are no available drivers for the scheduled ride at the moment");
    }

    private boolean isVehicleSuitable(Vehicle vehicle, boolean requestBabyFriendly, boolean requestPetFriendly, int passangersNum, VehicleType vehicleType) {
        boolean petSuitable = !requestPetFriendly || vehicle.isPetFriendly();
        boolean babySuitable = !requestBabyFriendly || vehicle.isBabyFriendly();
        boolean passangersSuitable = vehicle.getPassengerSeats() >=  passangersNum;
        boolean vehicleTypeSuitable = vehicleType == vehicle.getType();
        System.out.println("Number of seats: " + vehicle.getPassengerSeats() + ", Passangers num: " + passangersNum);
        return petSuitable && babySuitable && passangersSuitable && vehicleTypeSuitable;
    }

    private String buildNoDriverErrorMessage(int totalDrivers, int busyDrivers, int vehicleTypeRejections, 
            int dailyLimitRejections, int scheduledRideRejections, VehicleType requestedType) {
        StringBuilder message = new StringBuilder();
        
        if (busyDrivers == totalDrivers) {
            return "All online drivers are currently busy with other rides. Please try again shortly.";
        }
        
        if (vehicleTypeRejections > 0 && vehicleTypeRejections + busyDrivers >= totalDrivers) {
            return "No available drivers with " + requestedType + " vehicle type. Please try a different vehicle type.";
        }
        
        if (dailyLimitRejections > 0 && dailyLimitRejections + busyDrivers >= totalDrivers) {
            return "Available drivers have reached their daily driving limit. Please try again later.";
        }
        
        if (scheduledRideRejections > 0 && scheduledRideRejections + busyDrivers >= totalDrivers) {
            return "Available drivers have upcoming scheduled rides. Please try again shortly.";
        }
        
        // Generic message with details
        message.append("No suitable driver found. ");
        if (busyDrivers > 0) message.append(busyDrivers).append(" driver(s) busy. ");
        if (vehicleTypeRejections > 0) message.append(vehicleTypeRejections).append(" driver(s) have incompatible vehicle. ");
        if (dailyLimitRejections > 0) message.append(dailyLimitRejections).append(" driver(s) at daily limit. ");
        if (scheduledRideRejections > 0) message.append(scheduledRideRejections).append(" driver(s) have scheduled rides. ");
        
        return message.toString().trim();
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

    @Transactional
    protected Ride createInstantRide(Driver driver, RegularUser creator, RideRequestDTO request) {
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

        this.addPassengersToRide(ride, request.getPassengerEmails());
        this.addPassengerToRide(ride.getId(), creator.getId());
        return ride;
    }

    @Transactional
    protected Ride createScheduledRide(Driver driver, RegularUser creator, RideRequestDTO request) {
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
        this.addPassengerToRide(ride.getId(), creator.getId());
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
        return (double) Math.round(vehicleTypePrice + kilometerPrice * distance);
    }

    @Override
    public DriverHistoryPagingDTO getDriverHistory(Long driverId, LocalDateTime startDate,
                                                   LocalDateTime endDate, String sortBy, String sorting, int pageSize, int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.unsorted());

        Page<Ride> rides = rideRepository.findRidesForDriver(driverId, startDate, endDate, sortBy, sorting, pageable);
        DriverHistoryPagingDTO dto = new DriverHistoryPagingDTO();
        dto.setReachedEnd(!rides.hasNext());
        dto.setTotalElements(rides.getTotalElements());

        List<DriverHistoryDTO> driverHistoryDTOs = rides.getContent().stream()
                .map(DriverHistoryDTO::new)
                .toList();

        dto.setDriverHistory(driverHistoryDTOs.toArray(new DriverHistoryDTO[0]));
        return dto;
    }

    @Override
    public DriverHistoryDetailedDTO getDriverHistoryDetailed(Long driverId, Long rideId) {
        Ride ride = this.getRideById(rideId);
        if (ride == null) {
            throw new NoSuchElementException(String.format("Cannot find ride with id %d", rideId));
        }
        if (!ride.getDriver().getId().equals(driverId)) {
            throw new IllegalStateException(String.format("Driver didn't drive this ride %d", ride.getDriver().getId()));
        }
        DriverHistoryDetailedDTO dto = new DriverHistoryDetailedDTO(ride);
        List<PassengerTableRowDTO> passengers = new ArrayList<>();
        for (RegularUser regularUser : ride.getPassengers()){
            passengers.add(new PassengerTableRowDTO(regularUser));
        }
        dto.setPassengers(passengers);
        return dto;
    }

    @Override
    @Transactional
    public void startRide(Long rideId) {
        Ride ride = getRideById(rideId);
        if (ride.getRideStatus() != RideStatus.SCHEDULED) {
            throw new IllegalStateException("Only scheduled rides can be started.");
        }
        ride.setRideStatus(RideStatus.ACTIVE);
        ride.setStartDateTime(LocalDateTime.now());
        rideRepository.save(ride);
        for (RegularUser passenger : ride.getPassengers()) {
            passenger.setCanOrder(false);
            regularUserRepository.save(passenger);
        }
        RegularUser rideCreator = ride.getCreator();
        rideCreator.setCanOrder(false);
        regularUserRepository.save(rideCreator);
        driverService.updateDriverDriving(ride.getDriver().getId(), true);
        notifySocket(ride);
        RideOverviewUpdateDTO rideOverviewUpdateDTO = new RideOverviewUpdateDTO(ride.getEndAddress(), new CoordinatesDTO(ride.getCheckpoints().getLast().getAddress()), ride);
        simpMessagingTemplate.convertAndSend("/socket-publisher/rides/" + rideId + "/update", rideOverviewUpdateDTO);
    }

    @Override
    public LatestRideDTO getLatestRide(Long userId){
        Ride ride = this.rideRepository.findFirstByPassengers_IdOrderByStartDateTimeDesc(userId);
        if (ride == null){
            throw new NoSuchElementException(String.format("Cannot find ride for user id %d", userId));
        }
        return new LatestRideDTO(ride.getId(), ride.getRideStatus());
    }

    private void notifySocket(Ride ride){
        this.simpMessagingTemplate.convertAndSend(
                "/socket-publisher/ride/start",
                new RideMonitoringDTO(ride)
        );
    }

    @Override
    public UserHistoryPagingDTO getUserHistory(Long userId, LocalDateTime startDate,
                                               LocalDateTime endDate, String sortBy, String sorting, int pageSize, int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.unsorted());

        Page<Ride> rides = rideRepository.findRidesForUser(userId, startDate, endDate, sortBy, sorting, pageable);
        UserHistoryPagingDTO dto = new UserHistoryPagingDTO();
        dto.setReachedEnd(!rides.hasNext());
        dto.setTotalElements(rides.getTotalElements());

        List<UserHistoryDTO> userHistoryDTOs = rides.getContent().stream()
                .map(UserHistoryDTO::new)
                .toList();

        dto.setUserHistory(userHistoryDTOs.toArray(new UserHistoryDTO[0]));
        return dto;
    }

    @Override
    public UserHistoryDetailedDTO getUserHistoryDetailed(Long userId, Long rideId) {
        Optional<Ride> rideOptional = rideRepository.findByIdAndPassengerId(rideId, userId);
        
        if (rideOptional.isEmpty()) {
            throw new NoSuchElementException(String.format("Cannot find ride with id %d for user %d", rideId, userId));
        }
        
        Ride ride = rideOptional.get();
        UserHistoryDetailedDTO dto = new UserHistoryDetailedDTO(ride);
        
        // Get reviews for this ride
        Review review = reviewRepository.findReviewByRideIdAndUserId(rideId, userId);
        if (review != null) {
            dto.setHasReview(true);
            dto.setDriverRating(review.getDriverRating());
            dto.setCarRating(review.getCarRating());
            dto.setReviewComment(review.getComment());
        } else {
            dto.setHasReview(false);
        }
        
        // Get reports for this ride
        List<RideReport> reports = rideReportRepository.findByRideId(rideId);
        List<UserHistoryDetailedDTO.ReportInfoDTO> reportInfoDTOs = new ArrayList<>();
        for (RideReport report : reports) {
            UserHistoryDetailedDTO.ReportInfoDTO reportInfo = new UserHistoryDetailedDTO.ReportInfoDTO();
            reportInfo.setReportId(report.getReportId());
            reportInfo.setReportMessage(report.getReportMessage());
            if (report.getReporter() != null) {
                reportInfo.setReporterName(report.getReporter().getName() + " " + report.getReporter().getLastName());
            }
            reportInfoDTOs.add(reportInfo);
        }
        dto.setReports(reportInfoDTOs);
        
        return dto;
    }

    @Override
    public AdminHistoryPagingDTO getAdminHistory(String email, LocalDateTime startDate,
                                                 LocalDateTime endDate, String sortBy, String sorting, int pageSize, int pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.unsorted());

        Page<Ride> rides = rideRepository.findRidesForUserByEmail(email, startDate, endDate, pageable);
        AdminHistoryPagingDTO dto = new AdminHistoryPagingDTO();
        dto.setReachedEnd(!rides.hasNext());
        dto.setTotalElements(rides.getTotalElements());

        List<AdminHistoryDTO> adminHistoryDTOs = rides.getContent().stream()
                .map(AdminHistoryDTO::new)
                .toList();

        dto.setAdminHistory(adminHistoryDTOs.toArray(new AdminHistoryDTO[0]));
        return dto;
    }

    @Override
    public AdminHistoryDetailedDTO getAdminHistoryDetailed(Long rideId) {
        Optional<Ride> rideOptional = rideRepository.findById(rideId);
        
        if (rideOptional.isEmpty()) {
            throw new NoSuchElementException(String.format("Cannot find ride with id %d", rideId));
        }
        
        Ride ride = rideOptional.get();
        AdminHistoryDetailedDTO dto = new AdminHistoryDetailedDTO(ride);
        
        // Get reviews for this ride
        List<Review> reviews = reviewRepository.findByReviewedRideId(rideId);
        if (!reviews.isEmpty()) {
            Review review = reviews.get(0);
            dto.setHasReview(true);
            dto.setDriverRating(review.getDriverRating());
            dto.setCarRating(review.getCarRating());
            dto.setReviewComment(review.getComment());
        } else {
            dto.setHasReview(false);
        }
        
        // Get reports for this ride
        List<RideReport> reports = rideReportRepository.findByRideId(rideId);
        List<AdminHistoryDetailedDTO.ReportInfoDTO> reportInfoDTOs = new ArrayList<>();
        for (RideReport report : reports) {
            AdminHistoryDetailedDTO.ReportInfoDTO reportInfo = new AdminHistoryDetailedDTO.ReportInfoDTO();
            reportInfo.setReportId(report.getReportId());
            reportInfo.setReportMessage(report.getReportMessage());
            if (report.getReporter() != null) {
                reportInfo.setReporterName(report.getReporter().getName() + " " + report.getReporter().getLastName());
            }
            reportInfoDTOs.add(reportInfo);
        }
        dto.setReports(reportInfoDTOs);
        
        return dto;
    }

}