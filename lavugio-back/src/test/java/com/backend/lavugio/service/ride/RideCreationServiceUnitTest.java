package com.backend.lavugio.service.ride;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.dto.ride.RideRequestDTO;
import com.backend.lavugio.dto.ride.RideDestinationDTO;
import com.backend.lavugio.dto.ride.RideResponseDTO;
import com.backend.lavugio.dto.ride.StopBaseDTO;
import com.backend.lavugio.dto.user.DriverLocationDTO;
import com.backend.lavugio.model.enums.DriverStatusEnum;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.enums.VehicleType;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.repository.ride.ReviewRepository;
import com.backend.lavugio.repository.ride.RideReportRepository;
import com.backend.lavugio.repository.ride.RideRepository;
import com.backend.lavugio.repository.user.RegularUserRepository;
import com.backend.lavugio.service.notification.NotificationService;
import com.backend.lavugio.service.pricing.PricingService;
import com.backend.lavugio.service.ride.impl.RideServiceImpl;
import com.backend.lavugio.service.route.AddressService;
import com.backend.lavugio.service.route.RideDestinationService;
import com.backend.lavugio.service.user.DriverActivityService;
import com.backend.lavugio.service.user.DriverAvailabilityService;
import com.backend.lavugio.service.user.DriverService;
import com.backend.lavugio.service.utils.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RideCreationServiceUnitTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private RegularUserRepository regularUserRepository;

    @Mock
    private DriverService driverService;

    @Mock
    private PricingService pricingService;

    @Mock
    private RideDestinationService rideDestinationService;

    @Mock
    private DriverAvailabilityService driverAvailabilityService;

    @Mock
    private DriverActivityService driverActivityService;

    @Mock
    private AddressService addressService;

    @Mock
    private RideQueryService rideQueryService;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RideReportRepository rideReportRepository;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private RideServiceImpl rideService;

    private RegularUser testCreator;
    private Driver testDriver;
    private Vehicle testVehicle;
    private RideRequestDTO testRequest;
    private DriverLocationDTO testDriverLocation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test creator
        testCreator = new RegularUser();
        testCreator.setId(1L);
        testCreator.setEmail("creator@test.com");
        testCreator.setCanOrder(true);

        // Setup test vehicle
        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setType(VehicleType.STANDARD);
        testVehicle.setPassengerSeats(4);
        testVehicle.setBabyFriendly(true);
        testVehicle.setPetFriendly(true);

        // Setup test driver
        testDriver = new Driver();
        testDriver.setId(1L);
        testDriver.setEmail("driver@test.com");
        testDriver.setVehicle(testVehicle);
        testDriver.setActive(true);
        testDriver.setDriving(false);

        // Setup test request
        testRequest = new RideRequestDTO();
        testRequest.setVehicleType(VehicleType.STANDARD);
        testRequest.setBabyFriendly(false);
        testRequest.setPetFriendly(false);
        testRequest.setEstimatedDurationSeconds(1800);
        testRequest.setPrice(500);
        testRequest.setDistance(10.0f);
        testRequest.setPassengerEmails(List.of("passenger@test.com"));

        // Setup destinations
        StopBaseDTO startLocation = new StopBaseDTO(0, 45.2671, 19.8335);
        StopBaseDTO endLocation = new StopBaseDTO(1, 45.2557, 19.8451);

        RideDestinationDTO startDest = new RideDestinationDTO();
        startDest.setLocation(startLocation);
        startDest.setStreetName("Bulevar oslobodjenja");
        startDest.setCity("Novi Sad");
        startDest.setCountry("Serbia");
        startDest.setStreetNumber(46);
        startDest.setZipCode(21000);

        RideDestinationDTO endDest = new RideDestinationDTO();
        endDest.setLocation(endLocation);
        endDest.setStreetName("Narodnog fronta");
        endDest.setCity("Novi Sad");
        endDest.setCountry("Serbia");
        endDest.setStreetNumber(23);
        endDest.setZipCode(21000);

        testRequest.setDestinations(List.of(startDest, endDest));

        // Setup driver location DTO
        CoordinatesDTO driverCoords = new CoordinatesDTO(45.2600, 19.8400);
        testDriverLocation = new DriverLocationDTO();
        testDriverLocation.setId(1L);
        testDriverLocation.setLocation(driverCoords);
        testDriverLocation.setStatus(DriverStatusEnum.AVAILABLE);
    }

    // CREATE INSTANT RIDE TESTS

    @Test
    void createInstantRide_Success() {
        Ride savedRide = new Ride();
        savedRide.setId(100L);
        savedRide.setRideStatus(RideStatus.SCHEDULED);
        savedRide.setPassengers(new HashSet<>());
        savedRide.setDriver(testDriver);

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverAvailabilityService.getDriverLocationsDTO()).thenReturn(List.of(testDriverLocation));
        when(driverService.getDriverById(1L)).thenReturn(testDriver);
        when(driverActivityService.getActiveTimeIn24Hours(1L)).thenReturn(Duration.ofHours(2));
        when(rideRepository.findByDriverIdAndRideStatus(1L, RideStatus.SCHEDULED)).thenReturn(Collections.emptyList());
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> {
            Ride ride = invocation.getArgument(0);
            ride.setId(100L);
            return ride;
        });
        when(rideQueryService.getRideById(100L)).thenReturn(savedRide);
        when(addressService.createAddress(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(regularUserRepository.findByEmail("passenger@test.com")).thenReturn(Optional.of(new RegularUser()));

        RideResponseDTO response = rideService.createInstantRide(1L, testRequest);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(RideStatus.SCHEDULED, response.getStatus());
        verify(rideRepository, atLeastOnce()).save(any(Ride.class));
    }

    @Test
    void createInstantRide_UserNotFound_ThrowsRuntimeException() {
        when(regularUserRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createInstantRide(999L, testRequest)
        );

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(driverAvailabilityService, never()).getDriverLocationsDTO();
    }

    // DRIVER AVAILABILITY TESTS

    @Test
    void createInstantRide_NoAvailableDrivers_ThrowsRuntimeException() {
        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverAvailabilityService.getDriverLocationsDTO()).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createInstantRide(1L, testRequest)
        );

        assertTrue(exception.getMessage().contains("No drivers are currently online."));
    }

    @Test
    void createInstantRide_AllDriversBusy_ThrowsRuntimeException() {
        testDriverLocation.setStatus(DriverStatusEnum.BUSY);

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverAvailabilityService.getDriverLocationsDTO()).thenReturn(List.of(testDriverLocation));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createInstantRide(1L, testRequest)
        );

        assertTrue(exception.getMessage().contains("All online drivers are currently busy with other rides."));
    }

    @Test
    void createInstantRide_DriverExceededDailyLimit_ThrowsRuntimeException() {
        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverAvailabilityService.getDriverLocationsDTO()).thenReturn(List.of(testDriverLocation));
        when(driverService.getDriverById(1L)).thenReturn(testDriver);
        when(driverActivityService.getActiveTimeIn24Hours(1L)).thenReturn(Duration.ofHours(8)); // Already at limit
        when(rideRepository.findByDriverIdAndRideStatus(1L, RideStatus.SCHEDULED)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createInstantRide(1L, testRequest)
        );

        assertTrue(exception.getMessage().contains("Available drivers have reached their daily driving limit."));
    }

    @Test
    void createInstantRide_DriverHasScheduledRideSoon_ThrowsRuntimeException() {
        Ride scheduledRide = new Ride();
        scheduledRide.setId(50L);
        scheduledRide.setStartDateTime(LocalDateTime.now().plusMinutes(10));
        scheduledRide.setEstimatedDurationSeconds(1800);

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverAvailabilityService.getDriverLocationsDTO()).thenReturn(List.of(testDriverLocation));
        when(driverService.getDriverById(1L)).thenReturn(testDriver);
        when(driverActivityService.getActiveTimeIn24Hours(1L)).thenReturn(Duration.ofHours(2));
        when(rideQueryService.getScheduledRidesForDriver(1L)).thenReturn(List.of(scheduledRide));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createInstantRide(1L, testRequest)
        );

        assertTrue(exception.getMessage().contains("scheduled"));
    }

    // VEHICLE SPECIFIC REQUIREMENTS TESTS

    @Test
    void createInstantRide_VehicleTypeNotSuitable_ThrowsRuntimeException() {
        testRequest.setVehicleType(VehicleType.LUXURY);

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverAvailabilityService.getDriverLocationsDTO()).thenReturn(List.of(testDriverLocation));
        when(driverService.getDriverById(1L)).thenReturn(testDriver);
        when(driverActivityService.getActiveTimeIn24Hours(1L)).thenReturn(Duration.ofHours(2));
        when(rideRepository.findByDriverIdAndRideStatus(1L, RideStatus.SCHEDULED)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createInstantRide(1L, testRequest)
        );

        assertTrue(exception.getMessage().contains("No available drivers with LUXURY vehicle type."));
    }

    @Test
    void createInstantRide_NotEnoughSeats_ThrowsRuntimeException() {
        testVehicle.setPassengerSeats(1);
        testRequest.setPassengerEmails(List.of("p1@test.com", "p2@test.com", "p3@test.com"));

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverAvailabilityService.getDriverLocationsDTO()).thenReturn(List.of(testDriverLocation));
        when(driverService.getDriverById(1L)).thenReturn(testDriver);
        when(driverActivityService.getActiveTimeIn24Hours(1L)).thenReturn(Duration.ofHours(2));
        when(rideRepository.findByDriverIdAndRideStatus(1L, RideStatus.SCHEDULED)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createInstantRide(1L, testRequest)
        );
        System.out.println(exception.getMessage());
        assertNotNull(exception.getMessage());
    }

    @Test
    void createInstantRide_BabyFriendlyRequired_VehicleNotBabyFriendly_ThrowsRuntimeException() {
        testRequest.setBabyFriendly(true);
        testVehicle.setBabyFriendly(false);

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverAvailabilityService.getDriverLocationsDTO()).thenReturn(List.of(testDriverLocation));
        when(driverService.getDriverById(1L)).thenReturn(testDriver);
        when(driverActivityService.getActiveTimeIn24Hours(1L)).thenReturn(Duration.ofHours(2));
        when(rideRepository.findByDriverIdAndRideStatus(1L, RideStatus.SCHEDULED)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createInstantRide(1L, testRequest)
        );

        assertNotNull(exception.getMessage());
    }

    @Test
    void createInstantRide_PetFriendlyRequired_VehicleNotPetFriendly_ThrowsRuntimeException() {
        testRequest.setPetFriendly(true);
        testVehicle.setPetFriendly(false);

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverAvailabilityService.getDriverLocationsDTO()).thenReturn(List.of(testDriverLocation));
        when(driverService.getDriverById(1L)).thenReturn(testDriver);
        when(driverActivityService.getActiveTimeIn24Hours(1L)).thenReturn(Duration.ofHours(2));
        when(rideRepository.findByDriverIdAndRideStatus(1L, RideStatus.SCHEDULED)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createInstantRide(1L, testRequest)
        );

        assertNotNull(exception.getMessage());
    }

    // MULTIPLE DRIVERS TEST

    @Test
    void createInstantRide_SelectsClosestDriver() {
        DriverLocationDTO farDriver = new DriverLocationDTO();
        farDriver.setId(2L);
        farDriver.setLocation(new CoordinatesDTO(46.0, 20.0));
        farDriver.setStatus(DriverStatusEnum.AVAILABLE);

        DriverLocationDTO closeDriver = new DriverLocationDTO();
        closeDriver.setId(1L);
        closeDriver.setLocation(new CoordinatesDTO(45.2671, 19.8335));
        closeDriver.setStatus(DriverStatusEnum.AVAILABLE);

        Driver farDriverEntity = new Driver();
        farDriverEntity.setId(2L);
        farDriverEntity.setVehicle(testVehicle);

        Ride savedRide = new Ride();
        savedRide.setId(100L);
        savedRide.setRideStatus(RideStatus.SCHEDULED);
        savedRide.setPassengers(new HashSet<>());
        savedRide.setDriver(testDriver);

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverAvailabilityService.getDriverLocationsDTO()).thenReturn(List.of(farDriver, closeDriver));
        when(driverService.getDriverById(1L)).thenReturn(testDriver);
        when(driverService.getDriverById(2L)).thenReturn(farDriverEntity);
        when(driverActivityService.getActiveTimeIn24Hours(anyLong())).thenReturn(Duration.ofHours(2));
        when(rideRepository.findByDriverIdAndRideStatus(anyLong(), eq(RideStatus.SCHEDULED))).thenReturn(Collections.emptyList());
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> {
            Ride ride = invocation.getArgument(0);
            ride.setId(100L);
            return ride;
        });
        when(rideQueryService.getRideById(100L)).thenReturn(savedRide);
        when(addressService.createAddress(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(regularUserRepository.findByEmail("passenger@test.com")).thenReturn(Optional.of(new RegularUser()));

        RideResponseDTO response = rideService.createInstantRide(1L, testRequest);

        assertNotNull(response);
        verify(driverService).getDriverById(1L);
    }

    // SCHEDULED RIDE TESTS

    @Test
    void createScheduledRide_Success() {
        testRequest.setScheduledTime(LocalDateTime.now().plusHours(2));
        testRequest.setScheduled(true);

        Ride savedRide = new Ride();
        savedRide.setId(100L);
        savedRide.setRideStatus(RideStatus.SCHEDULED);
        savedRide.setPassengers(new HashSet<>());
        savedRide.setDriver(testDriver);

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverService.getAllDrivers()).thenReturn(List.of(testDriver));
        when(driverActivityService.getActiveTimeIn24Hours(1L)).thenReturn(Duration.ofHours(2));
        when(rideRepository.findByDriverIdAndRideStatus(1L, RideStatus.SCHEDULED)).thenReturn(Collections.emptyList());
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> {
            Ride ride = invocation.getArgument(0);
            ride.setId(100L);
            return ride;
        });
        when(rideQueryService.getRideById(100L)).thenReturn(savedRide);
        when(addressService.createAddress(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(regularUserRepository.findByEmail("passenger@test.com")).thenReturn(Optional.of(new RegularUser()));

        RideResponseDTO response = rideService.createScheduledRide(1L, testRequest);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(RideStatus.SCHEDULED, response.getStatus());
        verify(rideRepository, atLeastOnce()).save(any(Ride.class));
    }

    @Test
    void createScheduledRide_UserNotFound_ThrowsRuntimeException() {
        testRequest.setScheduledTime(LocalDateTime.now().plusHours(2));

        when(regularUserRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createScheduledRide(999L, testRequest)
        );

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(driverService, never()).getAllDrivers();
    }

    // DRIVER AVAILABILITY TESTS

    @Test
    void createScheduledRide_NoRegisteredDrivers_ThrowsRuntimeException() {
        testRequest.setScheduledTime(LocalDateTime.now().plusHours(2));

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverService.getAllDrivers()).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createScheduledRide(1L, testRequest)
        );

        assertEquals("There are no registered drivers at the moment", exception.getMessage());
    }

    @Test
    void createScheduledRide_DriverExceededDailyLimitForScheduledTime_ThrowsRuntimeException() {
        testRequest.setScheduledTime(LocalDateTime.now().plusHours(2));

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverService.getAllDrivers()).thenReturn(List.of(testDriver));
        when(driverActivityService.getActiveTimeIn24Hours(1L)).thenReturn(Duration.ofHours(7)); // Close to limit
        testRequest.setEstimatedDurationSeconds(7200);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createScheduledRide(1L, testRequest)
        );

        assertEquals("There are no available drivers for the scheduled ride at the moment", exception.getMessage());
    }

    @Test
    void createScheduledRide_DriverHasConflictingScheduledRide_ThrowsRuntimeException() {
        LocalDateTime scheduledTime = LocalDateTime.now().plusHours(2);
        testRequest.setScheduledTime(scheduledTime);

        Ride conflictingRide = new Ride();
        conflictingRide.setId(50L);
        conflictingRide.setStartDateTime(scheduledTime.plusMinutes(15));
        conflictingRide.setEstimatedDurationSeconds(1800);
        conflictingRide.setRideStatus(RideStatus.SCHEDULED);

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverService.getAllDrivers()).thenReturn(List.of(testDriver));
        when(driverActivityService.getActiveTimeIn24Hours(1L)).thenReturn(Duration.ofHours(2));
        when(rideRepository.findByDriverIdAndRideStatus(1L, RideStatus.SCHEDULED)).thenReturn(List.of(conflictingRide));
        when(rideQueryService.getScheduledRidesForDriver(1L)).thenReturn(List.of(conflictingRide));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createScheduledRide(1L, testRequest)
        );

        assertEquals("There are no available drivers for the scheduled ride at the moment", exception.getMessage());
    }

    // VEHICLE SPECIFIC REQUIREMENTS TESTS

    @Test
    void createScheduledRide_VehicleTypeNotSuitable_ThrowsRuntimeException() {
        testRequest.setScheduledTime(LocalDateTime.now().plusHours(2));
        testRequest.setVehicleType(VehicleType.COMBI);

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverService.getAllDrivers()).thenReturn(List.of(testDriver));
        when(driverActivityService.getActiveTimeIn24Hours(1L)).thenReturn(Duration.ofHours(2));
        when(rideRepository.findByDriverIdAndRideStatus(1L, RideStatus.SCHEDULED)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createScheduledRide(1L, testRequest)
        );

        assertEquals("There are no available drivers for the scheduled ride at the moment", exception.getMessage());
    }


    @Test
    void createScheduledRide_BabyFriendlyRequired_VehicleNotBabyFriendly_ThrowsRuntimeException() {
        testRequest.setScheduledTime(LocalDateTime.now().plusHours(2));
        testRequest.setBabyFriendly(true);
        testVehicle.setBabyFriendly(false);

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverService.getAllDrivers()).thenReturn(List.of(testDriver));
        when(driverActivityService.getActiveTimeIn24Hours(1L)).thenReturn(Duration.ofHours(2));
        when(rideRepository.findByDriverIdAndRideStatus(1L, RideStatus.SCHEDULED)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createScheduledRide(1L, testRequest)
        );

        assertEquals("There are no available drivers for the scheduled ride at the moment", exception.getMessage());
    }

    @Test
    void createScheduledRide_PetFriendlyRequired_VehicleNotPetFriendly_ThrowsRuntimeException() {
        testRequest.setScheduledTime(LocalDateTime.now().plusHours(2));
        testRequest.setPetFriendly(true);
        testVehicle.setPetFriendly(false);

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverService.getAllDrivers()).thenReturn(List.of(testDriver));
        when(driverActivityService.getActiveTimeIn24Hours(1L)).thenReturn(Duration.ofHours(2));
        when(rideRepository.findByDriverIdAndRideStatus(1L, RideStatus.SCHEDULED)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createScheduledRide(1L, testRequest)
        );

        assertEquals("There are no available drivers for the scheduled ride at the moment", exception.getMessage());
    }

    @Test
    void createScheduledRide_NotEnoughSeats_ThrowsRuntimeException() {
        testRequest.setScheduledTime(LocalDateTime.now().plusHours(2));
        testVehicle.setPassengerSeats(1);
        testRequest.setPassengerEmails(List.of("p1@test.com", "p2@test.com", "p3@test.com"));

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverService.getAllDrivers()).thenReturn(List.of(testDriver));
        when(driverActivityService.getActiveTimeIn24Hours(1L)).thenReturn(Duration.ofHours(2));
        when(rideRepository.findByDriverIdAndRideStatus(1L, RideStatus.SCHEDULED)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rideService.createScheduledRide(1L, testRequest)
        );

        assertEquals("There are no available drivers for the scheduled ride at the moment", exception.getMessage());
    }

    @Test
    void createScheduledRide_MultipleDrivers_SelectsFirstSuitable() {
        testRequest.setScheduledTime(LocalDateTime.now().plusHours(2));

        Driver unsuitableDriver = new Driver();
        unsuitableDriver.setId(2L);
        Vehicle unsuitableVehicle = new Vehicle();
        unsuitableVehicle.setType(VehicleType.LUXURY);
        unsuitableVehicle.setPassengerSeats(4);
        unsuitableDriver.setVehicle(unsuitableVehicle);

        Ride savedRide = new Ride();
        savedRide.setId(100L);
        savedRide.setRideStatus(RideStatus.SCHEDULED);
        savedRide.setPassengers(new HashSet<>());
        savedRide.setDriver(testDriver);

        when(regularUserRepository.findById(1L)).thenReturn(Optional.of(testCreator));
        when(driverService.getAllDrivers()).thenReturn(List.of(unsuitableDriver, testDriver));
        when(driverActivityService.getActiveTimeIn24Hours(anyLong())).thenReturn(Duration.ofHours(2));
        when(rideRepository.findByDriverIdAndRideStatus(anyLong(), eq(RideStatus.SCHEDULED))).thenReturn(Collections.emptyList());
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> {
            Ride ride = invocation.getArgument(0);
            ride.setId(100L);
            return ride;
        });
        when(rideQueryService.getRideById(100L)).thenReturn(savedRide);
        when(addressService.createAddress(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(regularUserRepository.findByEmail("passenger@test.com")).thenReturn(Optional.of(new RegularUser()));

        RideResponseDTO response = rideService.createScheduledRide(1L, testRequest);

        assertNotNull(response);
        assertEquals(100L, response.getId());
    }

}
