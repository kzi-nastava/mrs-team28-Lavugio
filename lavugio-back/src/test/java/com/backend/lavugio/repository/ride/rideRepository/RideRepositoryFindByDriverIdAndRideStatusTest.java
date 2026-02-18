package com.backend.lavugio.repository.ride.rideRepository;

import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.repository.ride.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RideRepositoryFindByDriverIdAndRideStatusTest {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Driver testDriver;

    @BeforeEach
    void setUp() {
        testDriver = new Driver();
        testDriver.setName("Test Driver");
        testDriver.setLastName("Smith");
        testDriver.setEmail("driver@test.com");
        testDriver.setPassword("password123");
        testDriver.setActive(true);
        testDriver.setDriving(false);
        testDriver.setBlocked(false);
        testDriver = testEntityManager.persistAndFlush(testDriver);
    }

    @Test
    @DisplayName("Test findByDriverIdAndRideStatus - returns scheduled rides for driver")
    void testFindByDriverIdAndRideStatus_ReturnsScheduledRides() {
        Ride scheduledRide1 = createRide(testDriver, RideStatus.SCHEDULED, LocalDateTime.now().plusHours(1));
        Ride scheduledRide2 = createRide(testDriver, RideStatus.SCHEDULED, LocalDateTime.now().plusHours(2));
        Ride activeRide = createRide(testDriver, RideStatus.ACTIVE, LocalDateTime.now());
        Ride finishedRide = createRide(testDriver, RideStatus.FINISHED, LocalDateTime.now().minusHours(1));

        testEntityManager.persistAndFlush(scheduledRide1);
        testEntityManager.persistAndFlush(scheduledRide2);
        testEntityManager.persistAndFlush(activeRide);
        testEntityManager.persistAndFlush(finishedRide);
        testEntityManager.clear();

        List<Ride> result = rideRepository.findByDriverIdAndRideStatus(testDriver.getId(), RideStatus.SCHEDULED);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(ride -> ride.getRideStatus() == RideStatus.SCHEDULED);
        assertThat(result).allMatch(ride -> ride.getDriver().getId().equals(testDriver.getId()));
    }

    @Test
    @DisplayName("Test findByDriverIdAndRideStatus - returns empty list when no rides match")
    void testFindByDriverIdAndRideStatus_ReturnsEmptyWhenNoMatch() {
        Ride activeRide = createRide(testDriver, RideStatus.ACTIVE, LocalDateTime.now());
        testEntityManager.persistAndFlush(activeRide);
        testEntityManager.clear();

        List<Ride> result = rideRepository.findByDriverIdAndRideStatus(testDriver.getId(), RideStatus.SCHEDULED);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Test findByDriverIdAndRideStatus - returns empty list for non-existent driver")
    void testFindByDriverIdAndRideStatus_ReturnsEmptyForNonExistentDriver() {
        Ride scheduledRide = createRide(testDriver, RideStatus.SCHEDULED, LocalDateTime.now().plusHours(1));
        testEntityManager.persistAndFlush(scheduledRide);
        testEntityManager.clear();

        List<Ride> result = rideRepository.findByDriverIdAndRideStatus(999L, RideStatus.SCHEDULED);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Test findByDriverIdAndRideStatus - filters by driver correctly")
    void testFindByDriverIdAndRideStatus_FiltersByDriver() {
        Driver anotherDriver = new Driver();
        anotherDriver.setName("Another Driver");
        anotherDriver.setLastName("Jones");
        anotherDriver.setEmail("another.driver@test.com");
        anotherDriver.setPassword("password123");
        anotherDriver.setActive(true);
        anotherDriver.setDriving(false);
        anotherDriver.setBlocked(false);
        anotherDriver = testEntityManager.persistAndFlush(anotherDriver);

        Ride ride1 = createRide(testDriver, RideStatus.SCHEDULED, LocalDateTime.now().plusHours(1));
        Ride ride2 = createRide(anotherDriver, RideStatus.SCHEDULED, LocalDateTime.now().plusHours(2));

        testEntityManager.persistAndFlush(ride1);
        testEntityManager.persistAndFlush(ride2);
        testEntityManager.clear();

        List<Ride> resultForTestDriver = rideRepository.findByDriverIdAndRideStatus(testDriver.getId(), RideStatus.SCHEDULED);
        List<Ride> resultForAnotherDriver = rideRepository.findByDriverIdAndRideStatus(anotherDriver.getId(), RideStatus.SCHEDULED);

        assertThat(resultForTestDriver).hasSize(1);
        assertThat(resultForTestDriver.getFirst().getDriver().getId()).isEqualTo(testDriver.getId());

        assertThat(resultForAnotherDriver).hasSize(1);
        assertThat(resultForAnotherDriver.getFirst().getDriver().getId()).isEqualTo(anotherDriver.getId());
    }

    @Test
    @DisplayName("Test findByDriverIdAndRideStatus - returns active rides")
    void testFindByDriverIdAndRideStatus_ReturnsActiveRides() {
        Ride activeRide = createRide(testDriver, RideStatus.ACTIVE, LocalDateTime.now());
        Ride scheduledRide = createRide(testDriver, RideStatus.SCHEDULED, LocalDateTime.now().plusHours(1));

        testEntityManager.persistAndFlush(activeRide);
        testEntityManager.persistAndFlush(scheduledRide);
        testEntityManager.clear();

        List<Ride> result = rideRepository.findByDriverIdAndRideStatus(testDriver.getId(), RideStatus.ACTIVE);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getRideStatus()).isEqualTo(RideStatus.ACTIVE);
    }

    @Test
    @DisplayName("Test findByDriverIdAndRideStatus - returns finished rides")
    void testFindByDriverIdAndRideStatus_ReturnsFinishedRides() {
        Ride finishedRide1 = createRide(testDriver, RideStatus.FINISHED, LocalDateTime.now().minusHours(2));
        Ride finishedRide2 = createRide(testDriver, RideStatus.FINISHED, LocalDateTime.now().minusHours(1));
        Ride activeRide = createRide(testDriver, RideStatus.ACTIVE, LocalDateTime.now());

        testEntityManager.persistAndFlush(finishedRide1);
        testEntityManager.persistAndFlush(finishedRide2);
        testEntityManager.persistAndFlush(activeRide);
        testEntityManager.clear();

        List<Ride> result = rideRepository.findByDriverIdAndRideStatus(testDriver.getId(), RideStatus.FINISHED);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(ride -> ride.getRideStatus() == RideStatus.FINISHED);
    }

    private Ride createRide(Driver driver, RideStatus status, LocalDateTime startDateTime) {
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setRideStatus(status);
        ride.setStartDateTime(startDateTime);
        ride.setEstimatedDurationSeconds(1800);
        ride.setPrice(15.0f);
        ride.setDistance(10.0f);
        ride.setHasPanic(false);
        return ride;
    }
}
