package com.backend.lavugio.repository.ride.rideDestinationRepository;

import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.route.Address;
import com.backend.lavugio.model.route.RideDestination;
import com.backend.lavugio.repository.route.RideDestinationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RideDestinationRepositoryTest {

    @Autowired
    private RideDestinationRepository rideDestinationRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("Test findByRideIdOrderByDestinationOrder - returns destinations in correct order")
    void testFindByRideIdOrderByDestinationOrder_ReturnsOrderedDestinations() {

        Ride ride = new Ride();
        ride.setRideStatus(RideStatus.SCHEDULED);
        ride.setStartDateTime(LocalDateTime.now().plusDays(1));
        ride.setEstimatedDurationSeconds(3600);
        ride.setPrice(20.0f);
        ride.setDistance(15.0f);
        ride.setHasPanic(false);
        
        Ride savedRide = testEntityManager.persistAndFlush(ride);

        Address address1 = new Address();
        address1.setStreetName("Bulevar oslobodjenja");
        address1.setStreetNumber("46");
        address1.setCity("Novi Sad");
        address1.setCountry("Serbia");
        address1.setZipCode(21000);
        address1.setLatitude(45.2671);
        address1.setLongitude(19.8335);
        Address savedAddress1 = testEntityManager.persistAndFlush(address1);

        Address address2 = new Address();
        address2.setStreetName("Narodnog fronta");
        address2.setStreetNumber("23");
        address2.setCity("Novi Sad");
        address2.setCountry("Serbia");
        address2.setZipCode(21000);
        address2.setLatitude(45.2557);
        address2.setLongitude(19.8451);
        Address savedAddress2 = testEntityManager.persistAndFlush(address2);

        Address address3 = new Address();
        address3.setStreetName("Maksima Gorkog");
        address3.setStreetNumber("7");
        address3.setCity("Novi Sad");
        address3.setCountry("Serbia");
        address3.setZipCode(21000);
        address3.setLatitude(45.2500);
        address3.setLongitude(19.8400);
        Address savedAddress3 = testEntityManager.persistAndFlush(address3);

        RideDestination destination2 = new RideDestination();
        destination2.setRide(savedRide);
        destination2.setAddress(savedAddress2);
        destination2.setDestinationOrder(1);
        testEntityManager.persistAndFlush(destination2);

        RideDestination destination3 = new RideDestination();
        destination3.setRide(savedRide);
        destination3.setAddress(savedAddress3);
        destination3.setDestinationOrder(2);
        testEntityManager.persistAndFlush(destination3);

        RideDestination destination1 = new RideDestination();
        destination1.setRide(savedRide);
        destination1.setAddress(savedAddress1);
        destination1.setDestinationOrder(0);
        testEntityManager.persistAndFlush(destination1);

        testEntityManager.clear();

        List<RideDestination> result = rideDestinationRepository
                .findByRideIdOrderByDestinationOrder(savedRide.getId());

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);

        assertThat(result.get(0).getDestinationOrder()).isEqualTo(0);
        assertThat(result.get(1).getDestinationOrder()).isEqualTo(1);
        assertThat(result.get(2).getDestinationOrder()).isEqualTo(2);

        assertThat(result.get(0).getAddress().getStreetName()).isEqualTo("Bulevar oslobodjenja");
        assertThat(result.get(1).getAddress().getStreetName()).isEqualTo("Narodnog fronta");
        assertThat(result.get(2).getAddress().getStreetName()).isEqualTo("Maksima Gorkog");

        assertThat(result)
                .extracting(rd -> rd.getRide().getId())
                .containsOnly(savedRide.getId());
    }

    @Test
    @DisplayName("Test findByRideIdOrderByDestinationOrder - empty list for non-existent ride")
    void testFindByRideIdOrderByDestinationOrder_ReturnsEmptyListForNonExistentRide() {
        List<RideDestination> result = rideDestinationRepository
                .findByRideIdOrderByDestinationOrder(999L);

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Test findByRideIdOrderByDestinationOrder - single destination")
    void testFindByRideIdOrderByDestinationOrder_ReturnsSingleDestination() {
        Ride ride = new Ride();
        ride.setRideStatus(RideStatus.SCHEDULED);
        ride.setStartDateTime(LocalDateTime.now().plusDays(1));
        ride.setEstimatedDurationSeconds(1800);
        ride.setPrice(10.0f);
        ride.setDistance(5.0f);
        ride.setHasPanic(false);
        Ride savedRide = testEntityManager.persistAndFlush(ride);

        Address address = new Address();
        address.setStreetName("Zmaj Jovina");
        address.setStreetNumber("1");
        address.setCity("Novi Sad");
        address.setCountry("Serbia");
        address.setZipCode(21000);
        address.setLatitude(45.2550);
        address.setLongitude(19.8420);
        Address savedAddress = testEntityManager.persistAndFlush(address);

        RideDestination destination = new RideDestination();
        destination.setRide(savedRide);
        destination.setAddress(savedAddress);
        destination.setDestinationOrder(0);
        testEntityManager.persistAndFlush(destination);

        testEntityManager.clear();

        List<RideDestination> result = rideDestinationRepository
                .findByRideIdOrderByDestinationOrder(savedRide.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDestinationOrder()).isEqualTo(0);
        assertThat(result.getFirst().getAddress().getStreetName()).isEqualTo("Zmaj Jovina");
    }

    @Test
    @DisplayName("Test findByRideIdOrderByDestinationOrder - multiple rides")
    void testFindByRideIdOrderByDestinationOrder_FiltersByRideId() {

        Ride ride1 = new Ride();
        ride1.setRideStatus(RideStatus.SCHEDULED);
        ride1.setStartDateTime(LocalDateTime.now().plusDays(1));
        ride1.setEstimatedDurationSeconds(3600);
        ride1.setPrice(20.0f);
        ride1.setDistance(15.0f);
        ride1.setHasPanic(false);
        Ride savedRide1 = testEntityManager.persistAndFlush(ride1);

        Address address1 = new Address();
        address1.setStreetName("Street 1");
        address1.setStreetNumber("1");
        address1.setCity("City 1");
        address1.setCountry("Country");
        address1.setZipCode(11000);
        address1.setLatitude(45.0);
        address1.setLongitude(19.0);
        Address savedAddress1 = testEntityManager.persistAndFlush(address1);

        RideDestination destination1 = new RideDestination();
        destination1.setRide(savedRide1);
        destination1.setAddress(savedAddress1);
        destination1.setDestinationOrder(0);
        testEntityManager.persistAndFlush(destination1);

        Ride ride2 = new Ride();
        ride2.setRideStatus(RideStatus.SCHEDULED);
        ride2.setStartDateTime(LocalDateTime.now().plusDays(2));
        ride2.setEstimatedDurationSeconds(1800);
        ride2.setPrice(10.0f);
        ride2.setDistance(5.0f);
        ride2.setHasPanic(false);
        Ride savedRide2 = testEntityManager.persistAndFlush(ride2);

        Address address2 = new Address();
        address2.setStreetName("Street 2");
        address2.setStreetNumber("2");
        address2.setCity("City 2");
        address2.setCountry("Country");
        address2.setZipCode(21000);
        address2.setLatitude(46.0);
        address2.setLongitude(20.0);
        Address savedAddress2 = testEntityManager.persistAndFlush(address2);

        RideDestination destination2 = new RideDestination();
        destination2.setRide(savedRide2);
        destination2.setAddress(savedAddress2);
        destination2.setDestinationOrder(0);
        testEntityManager.persistAndFlush(destination2);

        testEntityManager.clear();

        List<RideDestination> result = rideDestinationRepository
                .findByRideIdOrderByDestinationOrder(savedRide1.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getRide().getId()).isEqualTo(savedRide1.getId());
        assertThat(result.getFirst().getAddress().getStreetName()).isEqualTo("Street 1");
    }
}