package com.backend.lavugio.repository.ride.rideRepository;

import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.repository.ride.RideRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RideRepositoryFindByIdTest {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("Test findById returns correct Ride")
    void testFindById() {

        Driver driver = new Driver();
        driver.setName("Test Driver");
        driver.setEmail("driver@test.com");
        driver.setActive(true);
        driver.setDriving(false);


        Driver savedDriver = testEntityManager.persistAndFlush(driver);

        Ride ride = new Ride();
        ride.setDriver(savedDriver);
        ride.setRideStatus(RideStatus.SCHEDULED);
        ride.setStartDateTime(LocalDateTime.now().plusDays(1));
        ride.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(1));
        ride.setEstimatedDurationSeconds(3600);
        ride.setPrice(15.0f);
        ride.setDistance(10.0f);
        ride.setHasPanic(false);

        Ride savedRide = testEntityManager.persistAndFlush(ride);

        testEntityManager.clear();

        Optional<Ride> foundRideOpt = rideRepository.findById(savedRide.getId());

        assertThat(foundRideOpt).isPresent();

        Ride foundRide = foundRideOpt.get();
        assertThat(foundRide.getId()).isEqualTo(savedRide.getId());
        assertThat(foundRide.getRideStatus()).isEqualTo(RideStatus.SCHEDULED);
        assertThat(foundRide.getPrice()).isEqualTo(15.0f);
        assertThat(foundRide.getDistance()).isEqualTo(10.0f);
        assertThat(foundRide.getDriver()).isNotNull();
        assertThat(foundRide.getDriver().getName()).isEqualTo("Test Driver");

    }
}