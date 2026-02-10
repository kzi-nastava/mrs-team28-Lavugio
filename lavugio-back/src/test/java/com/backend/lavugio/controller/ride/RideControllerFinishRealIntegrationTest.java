package com.backend.lavugio.controller.ride;

import com.backend.lavugio.dto.ride.FinishRideDTO;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.route.Address;
import com.backend.lavugio.model.route.RideDestination;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.model.enums.VehicleType;
import com.backend.lavugio.repository.ride.RideRepository;
import com.backend.lavugio.repository.route.AddressRepository;
import com.backend.lavugio.repository.route.RideDestinationRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class RideControllerFinishIntegrationTest {

    @Autowired
    private AddressRepository addressRepository;

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
    private RideDestinationRepository rideDestinationRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private Driver testDriver;
    private Ride testRide;
    private String driverToken;

    @BeforeEach
    void setUp() {
        rideDestinationRepository.deleteAll();
        rideRepository.deleteAll();
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
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        vehicleRepository.flush();

        testDriver = new Driver();
        testDriver.setName("Test Driver");
        testDriver.setEmail("driver@test.com");
        testDriver.setPassword("password123");
        testDriver.setPhoneNumber("+381641234567");
        testDriver.setActive(true);
        testDriver.setDriving(true);
        testDriver.setVehicle(savedVehicle);
        testDriver.setEmailVerified(true);
        testDriver = driverRepository.save(testDriver);
        driverRepository.flush();

        driverToken = jwtUtil.generateToken(testDriver.getEmail(), testDriver.getId(), "DRIVER");

        RegularUser creator = new RegularUser();
        creator.setName("Test User");
        creator.setEmail("user@test.com");
        creator.setPassword("password123");
        creator.setPhoneNumber("+381649876543");
        creator.setCanOrder(false);
        creator = regularUserRepository.save(creator);
        regularUserRepository.flush();

        testRide = new Ride();
        testRide.setDriver(testDriver);
        testRide.setCreator(creator);
        testRide.setRideStatus(RideStatus.ACTIVE);
        testRide.setStartDateTime(LocalDateTime.now());
        testRide.setEstimatedDurationSeconds(3600);
        testRide.setPrice(1000.0f);
        testRide.setDistance(10.0f);
        testRide.setHasPanic(false);
        testRide = rideRepository.save(testRide);
        rideRepository.flush();

        Address address1 = new Address();
        address1.setStreetName("Bulevar oslobodjenja");
        address1.setStreetNumber("46");
        address1.setCity("Novi Sad");
        address1.setCountry("Serbia");
        address1.setZipCode(21000);
        address1.setLatitude(45.2671);
        address1.setLongitude(19.8335);
        addressRepository.save(address1);

        Address address2 = new Address();
        address2.setStreetName("Narodnog fronta");
        address2.setStreetNumber("23");
        address2.setCity("Novi Sad");
        address2.setCountry("Serbia");
        address2.setZipCode(21000);
        address2.setLatitude(45.2557);
        address2.setLongitude(19.8451);
        addressRepository.save(address2);
        addressRepository.flush();

        RideDestination dest1 = new RideDestination();
        dest1.setRide(testRide);
        dest1.setAddress(address1);
        dest1.setDestinationOrder(0);
        rideDestinationRepository.save(dest1);

        RideDestination dest2 = new RideDestination();
        dest2.setRide(testRide);
        dest2.setAddress(address2);
        dest2.setDestinationOrder(1);
        rideDestinationRepository.save(dest2);
        rideDestinationRepository.flush();

    }

    @Test
    @DisplayName("Should finish ride successfully when making PUT request to /api/rides/finish")
    void shouldFinishRideSuccessfully() {
        FinishRideDTO finishRideDTO = new FinishRideDTO();
        finishRideDTO.setRideId(testRide.getId());
        finishRideDTO.setFinishedEarly(false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(driverToken);

        HttpEntity<FinishRideDTO> requestEntity = new HttpEntity<>(finishRideDTO, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/rides/finish",
                HttpMethod.PUT,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<Ride> updatedRideOpt = rideRepository.findById(testRide.getId());
        assertThat(updatedRideOpt).isPresent();
        Ride updatedRide = updatedRideOpt.get();
        assertThat(updatedRide.getRideStatus()).isEqualTo(RideStatus.FINISHED);
        assertThat(updatedRide.getEndDateTime()).isNotNull();

        Optional<Driver> updatedDriverOpt = driverRepository.findById(testDriver.getId());
        assertThat(updatedDriverOpt).isPresent();
        assertThat(updatedDriverOpt.get().isDriving()).isFalse();

        Optional<RegularUser> updatedCreatorOpt = regularUserRepository.findById(testRide.getCreator().getId());
        assertThat(updatedCreatorOpt).isPresent();
        assertThat(updatedCreatorOpt.get().isCanOrder()).isTrue();
    }

    @Test
    @DisplayName("Should return 404 when ride does not exist")
    void shouldReturn404WhenRideNotFound() {
        FinishRideDTO finishRideDTO = new FinishRideDTO();
        finishRideDTO.setRideId(9999L);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(driverToken);

        HttpEntity<FinishRideDTO> requestEntity = new HttpEntity<>(finishRideDTO, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/rides/finish",
                HttpMethod.PUT,
                requestEntity,
                Map.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertThat(response.getBody()).containsKey("error");
    }

    @Test
    @DisplayName("Should return 401 when no authentication provided")
    void shouldReturn401WhenNoAuthentication() {
        FinishRideDTO finishRideDTO = new FinishRideDTO();
        finishRideDTO.setRideId(testRide.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<FinishRideDTO> requestEntity = new HttpEntity<>(finishRideDTO, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/rides/finish",
                HttpMethod.PUT,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        Optional<Ride> unchangedRide = rideRepository.findById(testRide.getId());
        assertThat(unchangedRide).isPresent();
        assertThat(unchangedRide.get().getRideStatus()).isEqualTo(RideStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should return 403 when user has wrong role")
    void shouldReturn403WhenWrongRole() {
        String userToken = jwtUtil.generateToken("user@test.com", 999L, "REGULAR_USER");

        FinishRideDTO finishRideDTO = new FinishRideDTO();
        finishRideDTO.setRideId(testRide.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken);

        HttpEntity<FinishRideDTO> requestEntity = new HttpEntity<>(finishRideDTO, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/rides/finish",
                HttpMethod.PUT,
                requestEntity,
                Void.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());

        Optional<Ride> unchangedRide = rideRepository.findById(testRide.getId());
        assertThat(unchangedRide).isPresent();
        assertThat(unchangedRide.get().getRideStatus()).isEqualTo(RideStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should return 403 when driver tries to finish another driver's ride")
    void shouldReturn403WhenWrongDriver() {
        Driver otherDriver = new Driver();
        otherDriver.setName("Other Driver");
        otherDriver.setEmail("other@test.com");
        otherDriver.setPassword("password123");
        otherDriver.setPhoneNumber("+381649999999");
        otherDriver.setActive(true);
        otherDriver.setDriving(false);
        otherDriver.setEmailVerified(true);
        otherDriver = driverRepository.save(otherDriver);

        String otherDriverToken = jwtUtil.generateToken(otherDriver.getEmail(), otherDriver.getId(), "DRIVER");

        FinishRideDTO finishRideDTO = new FinishRideDTO();
        finishRideDTO.setRideId(testRide.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(otherDriverToken);

        HttpEntity<FinishRideDTO> requestEntity = new HttpEntity<>(finishRideDTO, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/rides/finish",
                HttpMethod.PUT,
                requestEntity,
                Map.class
        );

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertThat(response.getBody()).containsKey("error");
        assertThat(response.getBody().get("error").toString())
                .contains("Driver isn't driving this ride");
    }
}
