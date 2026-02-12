package com.backend.lavugio.service;

import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.repository.ride.RideRepository;
import com.backend.lavugio.repository.ride.ReviewRepository;
import com.backend.lavugio.repository.ride.RideReportRepository;
import com.backend.lavugio.repository.user.RegularUserRepository;
import com.backend.lavugio.service.notification.NotificationService;
import com.backend.lavugio.service.pricing.PricingService;
import com.backend.lavugio.service.ride.RideQueryService;
import com.backend.lavugio.service.ride.impl.RideServiceImpl;
import com.backend.lavugio.service.route.AddressService;
import com.backend.lavugio.service.route.RideDestinationService;
import com.backend.lavugio.service.user.DriverActivityService;
import com.backend.lavugio.service.user.DriverAvailabilityService;
import com.backend.lavugio.service.user.DriverService;
import com.backend.lavugio.service.utils.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RideServiceImpl - Cancel Ride functionality (2.6.5)
 * Student 3 - Tests cancelRide, cancelRideByDriver, and cancelRideByPassenger methods
 */
@ExtendWith(MockitoExtension.class)
public class RideCancellationServiceTest {

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

    private Ride scheduledRide;
    private Ride activeRide;
    private Ride finishedRide;
    private Driver testDriver;
    private RegularUser testUser;

    @BeforeEach
    void setUp() {
        testDriver = new Driver();
        testDriver.setId(1L);
        testDriver.setEmail("driver@test.com");
        testDriver.setName("Test Driver");
        testDriver.setDriving(true);
        testDriver.setActive(true);

        testUser = new RegularUser();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
        testUser.setName("Test User");
        testUser.setCanOrder(false);

        Set<RegularUser> passengers = new HashSet<>();
        passengers.add(testUser);

        // Scheduled ride (can be cancelled)
        scheduledRide = new Ride();
        scheduledRide.setId(1L);
        scheduledRide.setDriver(testDriver);
        scheduledRide.setCreator(testUser);
        scheduledRide.setRideStatus(RideStatus.SCHEDULED);
        scheduledRide.setStartDateTime(LocalDateTime.now().plusHours(2));
        scheduledRide.setPrice(100.0f);
        scheduledRide.setPassengers(passengers);

        // Active ride
        activeRide = new Ride();
        activeRide.setId(2L);
        activeRide.setDriver(testDriver);
        activeRide.setCreator(testUser);
        activeRide.setRideStatus(RideStatus.ACTIVE);
        activeRide.setStartDateTime(LocalDateTime.now().minusMinutes(30));
        activeRide.setPrice(100.0f);
        activeRide.setPassengers(passengers);

        // Finished ride
        finishedRide = new Ride();
        finishedRide.setId(3L);
        finishedRide.setDriver(testDriver);
        finishedRide.setCreator(testUser);
        finishedRide.setRideStatus(RideStatus.FINISHED);
        finishedRide.setStartDateTime(LocalDateTime.now().minusHours(2));
        finishedRide.setEndDateTime(LocalDateTime.now().minusHours(1));
        finishedRide.setPrice(100.0f);
        finishedRide.setPassengers(passengers);
    }

    // ==================== Positive Tests ====================
    @Test
    @DisplayName("Should successfully cancel scheduled ride")
    void shouldSuccessfullyCancelScheduledRide() {
        when(rideQueryService.getRideById(1L)).thenReturn(scheduledRide);
        when(rideRepository.save(any(Ride.class))).thenReturn(scheduledRide);

        rideService.cancelRide(1L);

        ArgumentCaptor<Ride> rideCaptor = ArgumentCaptor.forClass(Ride.class);
        verify(rideRepository).save(rideCaptor.capture());
        assertThat(rideCaptor.getValue().getRideStatus()).isEqualTo(RideStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should update driver status when cancelling ride")
    void shouldUpdateDriverStatusWhenCancellingRide() {
        when(rideQueryService.getRideById(1L)).thenReturn(scheduledRide);
        when(rideRepository.save(any(Ride.class))).thenReturn(scheduledRide);

        rideService.cancelRide(1L);

        verify(rideRepository).save(any(Ride.class));
    }

    // ==================== Negative Tests ====================
    @Test
    @DisplayName("Should throw exception when cancelling finished ride")
    void shouldThrowExceptionWhenCancellingFinishedRide() {
        when(rideQueryService.getRideById(3L)).thenReturn(finishedRide);

        assertThatThrownBy(() -> rideService.cancelRide(3L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cannot cancel finished ride");

        verify(rideRepository, never()).save(any(Ride.class));
    }

    @Test
    @DisplayName("Should throw exception when ride not found")
    void shouldThrowExceptionWhenRideNotFound() {
        when(rideQueryService.getRideById(999L))
            .thenThrow(new RuntimeException("Ride not found with id: 999"));

        assertThatThrownBy(() -> rideService.cancelRide(999L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Ride not found");
    }

    @Test
    @DisplayName("Should throw exception when driver cancels active ride")
    void shouldThrowExceptionWhenDriverCancelsActiveRide() {
        when(rideQueryService.getRideById(2L)).thenReturn(activeRide);

        assertThatThrownBy(() -> rideService.cancelRideByDriver(2L, "reason"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cannot cancel active ride. Finish it early instead.");
    }

    // ==================== Boundary Tests ====================
    @Test
    @DisplayName("Should allow passenger to cancel ride more than 10 minutes before start (boundary)")
    void shouldAllowPassengerToCancelRideMoreThan10MinBeforeStart() {
        Ride futureRide = new Ride();
        futureRide.setId(10L);
        futureRide.setDriver(testDriver);
        futureRide.setCreator(testUser);
        futureRide.setRideStatus(RideStatus.SCHEDULED);
        futureRide.setStartDateTime(LocalDateTime.now().plusMinutes(15)); // 15 min in future - can cancel
        Set<RegularUser> passengers = new HashSet<>();
        passengers.add(testUser);
        futureRide.setPassengers(passengers);

        when(rideQueryService.getRideById(10L)).thenReturn(futureRide);
        when(rideRepository.save(any(Ride.class))).thenReturn(futureRide);

        rideService.cancelRideByPassenger(10L);

        verify(rideRepository).save(any(Ride.class));
    }

    @Test
    @DisplayName("Should not allow passenger to cancel ride less than 10 minutes before start (boundary)")
    void shouldNotAllowPassengerToCancelRideLessThan10MinBeforeStart() {
        Ride soonRide = new Ride();
        soonRide.setId(11L);
        soonRide.setDriver(testDriver);
        soonRide.setCreator(testUser);
        soonRide.setRideStatus(RideStatus.SCHEDULED);
        soonRide.setStartDateTime(LocalDateTime.now().plusMinutes(5)); // 5 min in future - too late to cancel
        Set<RegularUser> passengers = new HashSet<>();
        passengers.add(testUser);
        soonRide.setPassengers(passengers);

        when(rideQueryService.getRideById(11L)).thenReturn(soonRide);

        assertThatThrownBy(() -> rideService.cancelRideByPassenger(11L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cannot cancel ride less than 10 minutes before start time");
    }
}
