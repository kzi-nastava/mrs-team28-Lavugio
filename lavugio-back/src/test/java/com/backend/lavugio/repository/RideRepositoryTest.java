package com.backend.lavugio.repository;

import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.repository.ride.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RideRepository - testing custom query methods
 * related to ride cancellation functionality (2.6.5)
 * Student 3 - Tests only custom methods, not inherited JpaRepository methods.
 */
@ExtendWith(MockitoExtension.class)
public class RideRepositoryTest {

    @Mock
    private RideRepository rideRepository;

    private Driver testDriver;
    private RegularUser testUser;
    private Ride activeRide;
    private Ride scheduledRide;
    private Ride cancelledRide;

    @BeforeEach
    void setUp() {
        // Create test driver
        testDriver = new Driver();
        testDriver.setId(1L);
        testDriver.setEmail("driver@test.com");
        testDriver.setName("Test");
        testDriver.setLastName("Driver");

        // Create test user
        testUser = new RegularUser();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
        testUser.setName("Test");
        testUser.setLastName("User");

        // Create test rides
        activeRide = createTestRide(1L, RideStatus.ACTIVE);
        scheduledRide = createTestRide(2L, RideStatus.SCHEDULED);
        cancelledRide = createTestRide(3L, RideStatus.CANCELLED);
    }

    private Ride createTestRide(Long id, RideStatus status) {
        Ride ride = new Ride();
        ride.setId(id);
        ride.setDriver(testDriver);
        ride.setCreator(testUser);
        ride.setRideStatus(status);
        ride.setStartDateTime(LocalDateTime.now().plusHours(1));
        ride.setPrice(100.0f);
        return ride;
    }

    // ==================== Positive Tests ====================
    @Test
    @DisplayName("findByRideStatus should return rides with matching status")
    void findByRideStatusShouldReturnMatchingRides() {
        when(rideRepository.findByRideStatus(RideStatus.ACTIVE))
            .thenReturn(Arrays.asList(activeRide));

        List<Ride> result = rideRepository.findByRideStatus(RideStatus.ACTIVE);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRideStatus()).isEqualTo(RideStatus.ACTIVE);
        verify(rideRepository).findByRideStatus(RideStatus.ACTIVE);
    }

    @Test
    @DisplayName("findByDriverIdAndRideStatus should return rides for specific driver and status")
    void findByDriverIdAndRideStatusShouldReturnCorrectRides() {
        when(rideRepository.findByDriverIdAndRideStatus(1L, RideStatus.SCHEDULED))
            .thenReturn(Arrays.asList(scheduledRide));

        List<Ride> result = rideRepository.findByDriverIdAndRideStatus(1L, RideStatus.SCHEDULED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDriver().getId()).isEqualTo(1L);
        verify(rideRepository).findByDriverIdAndRideStatus(1L, RideStatus.SCHEDULED);
    }

    @Test
    @DisplayName("findAllActiveOrScheduledRides should return active and scheduled rides")
    void findAllActiveOrScheduledRidesShouldWork() {
        when(rideRepository.findAllActiveOrScheduledRides())
            .thenReturn(Arrays.asList(activeRide, scheduledRide));

        List<Ride> result = rideRepository.findAllActiveOrScheduledRides();

        assertThat(result).hasSize(2);
        verify(rideRepository).findAllActiveOrScheduledRides();
    }

    // ==================== Negative Tests ====================
    @Test
    @DisplayName("findByRideStatus should return empty list when no rides with status")
    void findByRideStatusShouldReturnEmptyListWhenNoMatches() {
        when(rideRepository.findByRideStatus(RideStatus.STOPPED))
            .thenReturn(Collections.emptyList());

        List<Ride> result = rideRepository.findByRideStatus(RideStatus.STOPPED);

        assertThat(result).isEmpty();
        verify(rideRepository).findByRideStatus(RideStatus.STOPPED);
    }

    @Test
    @DisplayName("findByDriverIdAndRideStatus should return empty for non-existent driver")
    void findByDriverIdAndRideStatusShouldReturnEmptyForNonExistentDriver() {
        when(rideRepository.findByDriverIdAndRideStatus(99999L, RideStatus.ACTIVE))
            .thenReturn(Collections.emptyList());

        List<Ride> result = rideRepository.findByDriverIdAndRideStatus(99999L, RideStatus.ACTIVE);

        assertThat(result).isEmpty();
        verify(rideRepository).findByDriverIdAndRideStatus(99999L, RideStatus.ACTIVE);
    }

    // ==================== Boundary Tests ====================
    @Test
    @DisplayName("findByRideStatus should handle null status")
    void findByRideStatusShouldHandleNullStatus() {
        when(rideRepository.findByRideStatus(null))
            .thenReturn(Collections.emptyList());

        List<Ride> result = rideRepository.findByRideStatus(null);

        assertThat(result).isEmpty();
        verify(rideRepository).findByRideStatus(null);
    }

    @Test
    @DisplayName("countByRideStatus should return correct count")
    void countByRideStatusShouldReturnCorrectCount() {
        when(rideRepository.countByRideStatus(RideStatus.CANCELLED))
            .thenReturn(5L);

        long count = rideRepository.countByRideStatus(RideStatus.CANCELLED);

        assertThat(count).isEqualTo(5L);
        verify(rideRepository).countByRideStatus(RideStatus.CANCELLED);
    }

    @Test
    @DisplayName("findByDriverId should return all rides for driver")
    void findByDriverIdShouldReturnAllDriverRides() {
        when(rideRepository.findByDriverId(1L))
            .thenReturn(Arrays.asList(activeRide, scheduledRide, cancelledRide));

        List<Ride> result = rideRepository.findByDriverId(1L);

        assertThat(result).hasSize(3);
        verify(rideRepository).findByDriverId(1L);
    }

    @Test
    @DisplayName("findByDriverId should return empty list for driver with no rides")
    void findByDriverIdShouldReturnEmptyForDriverWithNoRides() {
        when(rideRepository.findByDriverId(999L))
            .thenReturn(Collections.emptyList());

        List<Ride> result = rideRepository.findByDriverId(999L);

        assertThat(result).isEmpty();
        verify(rideRepository).findByDriverId(999L);
    }
}
