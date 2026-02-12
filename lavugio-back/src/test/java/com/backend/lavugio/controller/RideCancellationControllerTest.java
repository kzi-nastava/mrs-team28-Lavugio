package com.backend.lavugio.controller;

import com.backend.lavugio.controller.ride.RideController;
import com.backend.lavugio.exception.GlobalExceptionHandler;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.service.notification.NotificationService;
import com.backend.lavugio.service.notification.PanicNotificationWebSocketService;
import com.backend.lavugio.service.pricing.PricingService;
import com.backend.lavugio.service.ride.ReviewService;
import com.backend.lavugio.service.ride.RideCompletionService;
import com.backend.lavugio.service.ride.RideOverviewService;
import com.backend.lavugio.service.ride.RideReportService;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.user.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for RideController - Cancel Ride endpoints (Functionality 2.6.5)
 * Student 3 - Tests the REST layer with mocked service layer
 */
@ExtendWith(MockitoExtension.class)
public class RideCancellationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RideService rideService;

    @Mock
    private DriverService driverService;

    @Mock
    private RideReportService rideReportService;

    @Mock
    private ReviewService reviewService;

    @Mock
    private RideCompletionService rideCompletionService;

    @Mock
    private RideOverviewService rideOverviewService;

    @Mock
    private PanicNotificationWebSocketService panicWebSocketService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PricingService pricingService;

    private RideController rideController;

    private Ride scheduledRide;
    private Driver testDriver;
    private RegularUser testUser;

    @BeforeEach
    void setUp() {
        rideController = new RideController(
            rideService,
            driverService,
            rideReportService,
            reviewService,
            rideCompletionService,
            rideOverviewService,
            panicWebSocketService,
            notificationService,
            pricingService
        );

        mockMvc = MockMvcBuilders.standaloneSetup(rideController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        testDriver = new Driver();
        testDriver.setId(1L);
        testDriver.setEmail("driver@test.com");
        testDriver.setName("Test Driver");

        testUser = new RegularUser();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
        testUser.setName("Test User");

        scheduledRide = new Ride();
        scheduledRide.setId(1L);
        scheduledRide.setDriver(testDriver);
        scheduledRide.setCreator(testUser);
        scheduledRide.setRideStatus(RideStatus.SCHEDULED);
        scheduledRide.setStartDateTime(LocalDateTime.now().plusHours(2));
        scheduledRide.setPassengers(new HashSet<>());
    }

    // ==================== Positive Tests ====================
    @Test
    @DisplayName("Should return 200 OK when ride is successfully cancelled")
    void shouldReturn200WhenRideSuccessfullyCancelled() throws Exception {
        doNothing().when(rideService).cancelRide(1L);

        mockMvc.perform(post("/api/rides/1/cancel")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("Ride cancelled successfully"));

        verify(rideService).cancelRide(1L);
    }

    // ==================== Negative Tests ====================
    @Test
    @DisplayName("Should return 400 Bad Request when ride cannot be cancelled")
    void shouldReturn400WhenRideCannotBeCancelled() throws Exception {
        doThrow(new IllegalStateException("Cannot cancel finished ride"))
            .when(rideService).cancelRide(1L);

        mockMvc.perform(post("/api/rides/1/cancel")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when ride not found")
    void shouldReturn400WhenRideNotFound() throws Exception {
        doThrow(new RuntimeException("Ride not found with id: 999"))
            .when(rideService).cancelRide(999L);

        mockMvc.perform(post("/api/rides/999/cancel")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should call service method with correct ride ID")
    void shouldCallServiceWithCorrectRideId() throws Exception {
        doNothing().when(rideService).cancelRide(anyLong());

        mockMvc.perform(post("/api/rides/123/cancel")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(rideService).cancelRide(123L);
    }

    // ==================== Boundary/Exception Tests ====================
    @Test
    @DisplayName("Should handle negative ride ID")
    void shouldHandleNegativeRideId() throws Exception {
        doThrow(new RuntimeException("Ride not found with id: -1"))
            .when(rideService).cancelRide(-1L);

        mockMvc.perform(post("/api/rides/-1/cancel")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle zero ride ID as edge case")
    void shouldHandleZeroRideId() throws Exception {
        doThrow(new RuntimeException("Ride not found with id: 0"))
            .when(rideService).cancelRide(0L);

        mockMvc.perform(post("/api/rides/0/cancel")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle very large ride ID")
    void shouldHandleVeryLargeRideId() throws Exception {
        doThrow(new RuntimeException("Ride not found"))
            .when(rideService).cancelRide(Long.MAX_VALUE);

        mockMvc.perform(post("/api/rides/" + Long.MAX_VALUE + "/cancel")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }
}
