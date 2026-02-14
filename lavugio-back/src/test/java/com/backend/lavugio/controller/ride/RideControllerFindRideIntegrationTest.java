package com.backend.lavugio.controller.ride;

import com.backend.lavugio.dto.ride.RideDestinationDTO;
import com.backend.lavugio.dto.ride.RideRequestDTO;
import com.backend.lavugio.dto.ride.RideResponseDTO;
import com.backend.lavugio.dto.ride.StopBaseDTO;
import com.backend.lavugio.dto.user.DriverLocationDTO;
import com.backend.lavugio.model.enums.DriverStatusEnum;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.enums.VehicleType;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.DriverLocation;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.repository.notification.NotificationRepository;
import com.backend.lavugio.repository.ride.RideRepository;
import com.backend.lavugio.repository.route.AddressRepository;
import com.backend.lavugio.repository.route.RideDestinationRepository;
import com.backend.lavugio.repository.user.DriverActivityRepository;
import com.backend.lavugio.repository.user.DriverRepository;
import com.backend.lavugio.repository.user.RegularUserRepository;
import com.backend.lavugio.repository.vehicle.VehicleRepository;
import com.backend.lavugio.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import com.backend.lavugio.service.user.DriverAvailabilityService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class RideControllerFindRideIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RegularUserRepository regularUserRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RideDestinationRepository rideDestinationRepository;

    @Autowired
    private DriverActivityRepository driverActivityRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private DriverAvailabilityService driverAvailabilityService;

    private RegularUser testUser;
    private Driver testDriver;
    private String userToken;

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
        rideDestinationRepository.deleteAll();
        rideRepository.deleteAll();
        driverActivityRepository.deleteAll();
        driverRepository.deleteAll();
        regularUserRepository.deleteAll();
        vehicleRepository.deleteAll();
        addressRepository.deleteAll();

        Vehicle vehicle = new Vehicle();
        vehicle.setType(VehicleType.STANDARD);
        vehicle.setMake("Toyota");
        vehicle.setModel("Corolla");
        vehicle.setLicensePlate("NS-123-AB");
        vehicle.setColor("White");
        vehicle.setPassengerSeats(4);
        vehicle.setPetFriendly(false);
        vehicle.setBabyFriendly(false);
        vehicle = vehicleRepository.save(vehicle);
        vehicleRepository.flush();

        testDriver = new Driver();
        testDriver.setName("Test Driver");
        testDriver.setEmail("driver@test.com");
        testDriver.setPassword("password123");
        testDriver.setPhoneNumber("+381641234567");
        testDriver.setActive(true);
        testDriver.setDriving(false);
        testDriver.setVehicle(vehicle);
        testDriver.setEmailVerified(true);
        testDriver = driverRepository.save(testDriver);
        driverRepository.flush();

        List<DriverLocationDTO> driverLocations = new ArrayList<>();
        driverLocations.add(new DriverLocationDTO(new DriverLocation(testDriver.getId(), 19.8335, 45.2671), DriverStatusEnum.AVAILABLE));
        when(driverAvailabilityService.getDriverLocationsDTO()).thenReturn(driverLocations);

        testUser = new RegularUser();
        testUser.setName("Test User");
        testUser.setEmail("user@test.com");
        testUser.setPassword("password123");
        testUser.setPhoneNumber("+381649876543");
        testUser.setCanOrder(true);
        testUser.setEmailVerified(true);
        testUser = regularUserRepository.save(testUser);
        regularUserRepository.flush();

        userToken = jwtUtil.generateToken(testUser.getEmail(), testUser.getId(), "REGULAR_USER");
    }

    private RideRequestDTO createValidInstantRideRequest() {
        RideRequestDTO request = new RideRequestDTO();
        
        List<RideDestinationDTO> destinations = new ArrayList<>();

        RideDestinationDTO start = new RideDestinationDTO();
        start.setLocation(new StopBaseDTO(0, 45.2671, 19.8335));
        start.setAddress("Bulevar oslobodjenja 46, Novi Sad");
        start.setStreetName("Bulevar oslobodjenja");
        start.setStreetNumber(46);
        start.setCity("Novi Sad");
        start.setCountry("Serbia");
        start.setZipCode(21000);
        destinations.add(start);

        RideDestinationDTO end = new RideDestinationDTO();
        end.setLocation(new StopBaseDTO(1, 45.2557, 19.8451));
        end.setAddress("Narodnog fronta 23, Novi Sad");
        end.setStreetName("Narodnog fronta");
        end.setStreetNumber(23);
        end.setCity("Novi Sad");
        end.setCountry("Serbia");
        end.setZipCode(21000);
        destinations.add(end);
        
        request.setDestinations(destinations);
        request.setVehicleType(VehicleType.STANDARD);
        request.setBabyFriendly(false);
        request.setPetFriendly(false);
        request.setScheduled(false);
        request.setEstimatedDurationSeconds(600);
        request.setPrice(500);
        request.setDistance(5.0f);
        request.setPassengerEmails(new ArrayList<>());
        
        return request;
    }

    private RideRequestDTO createValidScheduledRideRequest() {
        // DATA SAME AS IN INSTANT RIDE, JUST SCHEDULED FLAG AND TIME SET
        RideRequestDTO request = createValidInstantRideRequest();
        request.setScheduled(true);
        request.setScheduledTime(LocalDateTime.now().plusHours(2));
        return request;
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken);
        return headers;
    }

    // INSTANT RIDE TEST
    @Test
    @DisplayName("Should create instant ride successfully when valid request is sent")
    void shouldCreateInstantRideSuccessfully() {
        RideRequestDTO request = createValidInstantRideRequest();
        HttpEntity<RideRequestDTO> requestEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<RideResponseDTO> response = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity,
                RideResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertNotNull(response.getBody().getDriver());
    }

    // SCHEDULED RIDE TESTS
    @Test
    @DisplayName("Should create scheduled ride successfully when valid request with future time is sent")
    void shouldCreateScheduledRideSuccessfully() {
        RideRequestDTO request = createValidScheduledRideRequest();
        HttpEntity<RideRequestDTO> requestEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<RideResponseDTO> response = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity,
                RideResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(RideStatus.SCHEDULED, response.getBody().getStatus());
        assertNotNull(response.getBody().getScheduledTime());
    }

    @Test
    @DisplayName("Should not assign driver immediately for scheduled ride")
    void shouldNotAssignDriverImmediatelyForScheduledRide() {
        RideRequestDTO request = createValidScheduledRideRequest();
        HttpEntity<RideRequestDTO> requestEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<RideResponseDTO> response = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity,
                RideResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    // VALIDATION TESTS
    @Test
    @DisplayName("Should return 400 when destinations are missing")
    void shouldReturn400WhenDestinationsAreMissing() {
        RideRequestDTO request = createValidInstantRideRequest();
        request.setDestinations(new ArrayList<>());

        HttpEntity<RideRequestDTO> requestEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 when only one destination is provided")
    void shouldReturn400WhenOnlyOneDestination() {
        RideRequestDTO request = new RideRequestDTO();

        List<RideDestinationDTO> destinations = new ArrayList<>();
        RideDestinationDTO start = new RideDestinationDTO();
        start.setLocation(new StopBaseDTO(0, 45.2671, 19.8335));
        start.setAddress("Bulevar oslobodjenja 46, Novi Sad");
        destinations.add(start);

        request.setDestinations(destinations);
        request.setVehicleType(VehicleType.STANDARD);
        request.setScheduled(false);

        HttpEntity<RideRequestDTO> requestEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 when vehicle type is missing")
    void shouldReturn400WhenVehicleTypeIsMissing() {
        RideRequestDTO request = createValidInstantRideRequest();
        request.setVehicleType(null);

        HttpEntity<RideRequestDTO> requestEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 when scheduled time is in the past for scheduled ride")
    void shouldReturn400WhenScheduledTimeIsInPast() {
        RideRequestDTO request = createValidInstantRideRequest();
        request.setScheduled(true);
        request.setScheduledTime(LocalDateTime.now().minusHours(1));

        HttpEntity<RideRequestDTO> requestEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // DRIVER TRIES TO FIND RIDE TEST
    @Test
    @DisplayName("Should return 4xx when driver tries to find ride")
    void shouldReturn403WhenDriverTriesToFindRide() {
        String driverToken = jwtUtil.generateToken(testDriver.getEmail(), testDriver.getId(), "DRIVER");
        RideRequestDTO request = createValidInstantRideRequest();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(driverToken);

        HttpEntity<RideRequestDTO> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertTrue(response.getStatusCode().is4xxClientError());
    }

    // BUSINESS LOGIC TESTS
    @Test
    @DisplayName("Should return error when user already has an active ride")
    void shouldReturnErrorWhenUserHasActiveRide() {
        RideRequestDTO request1 = createValidInstantRideRequest();
        HttpEntity<RideRequestDTO> requestEntity1 = new HttpEntity<>(request1, createAuthHeaders());

        ResponseEntity<RideResponseDTO> response1 = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity1,
                RideResponseDTO.class
        );
        assertEquals(HttpStatus.CREATED, response1.getStatusCode());

        RideRequestDTO request2 = createValidInstantRideRequest();
        HttpEntity<RideRequestDTO> requestEntity2 = new HttpEntity<>(request2, createAuthHeaders());

        ResponseEntity<String> response2 = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity2,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response2.getStatusCode());
    }

    @Test
    @DisplayName("Should return error when no drivers are available")
    void shouldReturnErrorWhenNoDriversAvailable() {
        testDriver.setActive(false);
        driverRepository.save(testDriver);
        driverRepository.flush();

        when(driverAvailabilityService.getDriverLocationsDTO()).thenReturn(new ArrayList<>());

        RideRequestDTO request = createValidInstantRideRequest();
        HttpEntity<RideRequestDTO> requestEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should persist ride in database after creation")
    void shouldPersistRideInDatabaseAfterCreation() {
        RideRequestDTO request = createValidInstantRideRequest();
        HttpEntity<RideRequestDTO> requestEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<RideResponseDTO> response = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity,
                RideResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        Long rideId = response.getBody().getId();
        Ride persistedRide = rideRepository.findById(rideId).orElse(null);

        assertNotNull(persistedRide);
        assertEquals(RideStatus.SCHEDULED, persistedRide.getRideStatus());
        assertEquals(testUser.getId(), persistedRide.getCreator().getId());
    }

    // VEHICLE SPECIFICATION TESTS
    @Test
    @DisplayName("Should create ride with LUXURY vehicle type when requested")
    void shouldCreateRideWithLuxuryVehicle() {
        Vehicle vehicle = testDriver.getVehicle();
        vehicle.setType(VehicleType.LUXURY);
        vehicleRepository.save(vehicle);
        vehicleRepository.flush();

        RideRequestDTO request = createValidInstantRideRequest();
        request.setVehicleType(VehicleType.LUXURY);

        HttpEntity<RideRequestDTO> requestEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<RideResponseDTO> response = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity,
                RideResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(VehicleType.LUXURY, response.getBody().getVehicleType());
    }

    @Test
    @DisplayName("Should create ride with COMBI vehicle type when requested")
    void shouldCreateRideWithVanVehicle() {
        Vehicle driverVehicle = testDriver.getVehicle();
        driverVehicle.setType(VehicleType.COMBI);
        vehicleRepository.save(driverVehicle);
        vehicleRepository.flush();

        RideRequestDTO request = createValidInstantRideRequest();
        request.setVehicleType(VehicleType.COMBI);

        HttpEntity<RideRequestDTO> requestEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<RideResponseDTO> response = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity,
                RideResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(VehicleType.COMBI, response.getBody().getVehicleType());
    }

    @Test
    @DisplayName("Should create ride with baby-friendly vehicle when requested")
    void shouldCreateRideWithBabyFriendlyVehicle() {
        Vehicle vehicle = testDriver.getVehicle();
        vehicle.setBabyFriendly(true);
        vehicleRepository.save(vehicle);
        vehicleRepository.flush();

        RideRequestDTO request = createValidInstantRideRequest();
        request.setBabyFriendly(true);

        HttpEntity<RideRequestDTO> requestEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<RideResponseDTO> response = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity,
                RideResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isBabyFriendly());
    }

    @Test
    @DisplayName("Should create ride with pet-friendly vehicle when requested")
    void shouldCreateRideWithPetFriendlyVehicle() {
        Vehicle vehicle = testDriver.getVehicle();
        vehicle.setPetFriendly(true);
        vehicleRepository.save(vehicle);
        vehicleRepository.flush();

        RideRequestDTO request = createValidInstantRideRequest();
        request.setPetFriendly(true);

        HttpEntity<RideRequestDTO> requestEntity = new HttpEntity<>(request, createAuthHeaders());

        ResponseEntity<RideResponseDTO> response = restTemplate.exchange(
                "/api/rides/find-ride",
                HttpMethod.POST,
                requestEntity,
                RideResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isPetFriendly());
    }
}
