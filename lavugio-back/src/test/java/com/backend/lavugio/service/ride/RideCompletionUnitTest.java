package com.backend.lavugio.service.ride;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.dto.ride.FinishRideDTO;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.notification.Notification;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.route.Address;
import com.backend.lavugio.model.route.RideDestination;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.model.enums.VehicleType;
import com.backend.lavugio.repository.user.DriverRepository;
import com.backend.lavugio.service.notification.NotificationService;
import com.backend.lavugio.service.ride.impl.RideCompletionServiceImpl;
import com.backend.lavugio.service.route.RideDestinationService;
import com.backend.lavugio.service.utils.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RideCompletionServiceImplTest {

    @Mock
    private RideDestinationService rideDestinationService;

    @Mock
    private RideOverviewService rideOverviewService;

    @Mock
    private RideService rideService;

    @Mock
    private EmailService emailService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @InjectMocks
    private RideCompletionServiceImpl rideCompletionService;

    private Ride testRide;
    private Driver testDriver;
    private RegularUser testCreator;
    private List<RideDestination> testRoute;
    private FinishRideDTO finishRideDTO;

    @BeforeEach
    void setUp() {
        VehicleType testVehicleType = VehicleType.STANDARD;

        Vehicle testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setType(testVehicleType);

        testDriver = new Driver();
        testDriver.setId(1L);
        testDriver.setEmail("driver@test.com");
        testDriver.setDriving(true);
        testDriver.setActive(true);
        testDriver.setVehicle(testVehicle);

        testCreator = new RegularUser();
        testCreator.setId(10L);
        testCreator.setEmail("creator@test.com");
        testCreator.setCanOrder(false);

        RegularUser testPassenger1 = new RegularUser();
        testPassenger1.setId(2L);
        testPassenger1.setEmail("passenger1@test.com");

        RegularUser testPassenger2 = new RegularUser();
        testPassenger2.setId(3L);
        testPassenger2.setEmail("passenger2@test.com");

        testRide = new Ride();
        testRide.setId(100L);
        testRide.setDriver(testDriver);
        testRide.setCreator(testCreator);
        testRide.setPassengers(Set.of(testPassenger1, testPassenger2));
        testRide.setRideStatus(RideStatus.ACTIVE);
        testRide.setPrice(1000.0f);
        testRide.setDistance(10.0f);

        Address address1 = new Address();
        address1.setId(1L);
        address1.setLatitude(45.2671);
        address1.setLongitude(19.8335);
        address1.setStreetName("Bulevar oslobodjenja");
        address1.setStreetNumber("46");

        Address address2 = new Address();
        address2.setId(2L);
        address2.setLatitude(45.2557);
        address2.setLongitude(19.8451);
        address2.setStreetName("Narodnog fronta");
        address2.setStreetNumber("23");

        RideDestination destination1 = new RideDestination();
        destination1.setId(1L);
        destination1.setAddress(address1);
        destination1.setDestinationOrder(0);

        RideDestination destination2 = new RideDestination();
        destination2.setId(2L);
        destination2.setAddress(address2);
        destination2.setDestinationOrder(1);

        testRoute = Arrays.asList(destination1, destination2);

        finishRideDTO = new FinishRideDTO();
        finishRideDTO.setRideId(100L);
        finishRideDTO.setFinishedEarly(false);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void finishRide_NormalCompletion_Success() {
        when(rideDestinationService.getOrderedDestinationsByRideId(100L)).thenReturn(testRoute);
        when(rideService.getRideById(100L)).thenReturn(testRide);
        when(notificationService.createWebRideFinishedNotification(anyLong(), anyLong()))
                .thenReturn(new Notification());

        rideCompletionService.finishRide(1L, finishRideDTO);

        assertEquals(RideStatus.FINISHED, testRide.getRideStatus());
        assertNotNull(testRide.getEndDateTime());
        assertFalse(testDriver.isDriving());
        assertTrue(testCreator.isCanOrder());

        verify(driverRepository, times(1)).save(testDriver);
        verify(emailService, times(2)).sendEmail(anyString(), anyString(), anyString());
        verify(notificationService, times(2)).createWebRideFinishedNotification(eq(100L), anyLong());
        verify(notificationService, times(2)).sendNotificationToSocket(any(Notification.class));
        verify(simpMessagingTemplate, times(1)).convertAndSend(
                eq("/socket-publisher/ride/finish"),
                eq(100L)
        );
        verify(rideOverviewService, times(1)).sendRideOverviewUpdateDTO(
                eq(100L),
                anyString(),
                any(CoordinatesDTO.class)
        );
    }

    @Test
    void finishRide_EmptyRoute_ThrowsNoSuchElementException() {
        when(rideDestinationService.getOrderedDestinationsByRideId(100L)).thenReturn(Collections.emptyList());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> rideCompletionService.finishRide(1L, finishRideDTO)
        );

        assertEquals("Cannot find route for ride 100", exception.getMessage());
        verify(rideService, never()).getRideById(anyLong());
    }

    @Test
    void finishRide_RideNotFound_ThrowsNoSuchElementException() {
        when(rideDestinationService.getOrderedDestinationsByRideId(100L)).thenReturn(testRoute);
        when(rideService.getRideById(100L)).thenReturn(null);

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> rideCompletionService.finishRide(1L, finishRideDTO)
        );

        assertEquals("Cannot find ride for ride 100", exception.getMessage());
    }

    @Test
    void finishRide_WrongDriver_ThrowsIllegalStateException() {
        Long wrongDriverId = 999L;
        when(rideDestinationService.getOrderedDestinationsByRideId(100L)).thenReturn(testRoute);
        when(rideService.getRideById(100L)).thenReturn(testRide);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> rideCompletionService.finishRide(wrongDriverId, finishRideDTO)
        );

        assertEquals("Driver isn't driving this ride", exception.getMessage());
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void finishRide_WithPendingStatusChange_AppliesStatusChange() {
        testDriver.setPendingStatusChange(false);

        when(rideDestinationService.getOrderedDestinationsByRideId(100L)).thenReturn(testRoute);
        when(rideService.getRideById(100L)).thenReturn(testRide);
        when(notificationService.createWebRideFinishedNotification(anyLong(), anyLong()))
                .thenReturn(new Notification());

        rideCompletionService.finishRide(1L, finishRideDTO);

        assertFalse(testDriver.isActive());
        assertNull(testDriver.getPendingStatusChange());
    }

    @Test
    void finishRide_WithoutPendingStatusChange_KeepsOriginalStatus() {
        testDriver.setPendingStatusChange(null);
        testDriver.setActive(true);

        when(rideDestinationService.getOrderedDestinationsByRideId(100L)).thenReturn(testRoute);
        when(rideService.getRideById(100L)).thenReturn(testRide);
        when(notificationService.createWebRideFinishedNotification(anyLong(), anyLong()))
                .thenReturn(new Notification());

        rideCompletionService.finishRide(1L, finishRideDTO);

        assertTrue(testDriver.isActive());
        assertNull(testDriver.getPendingStatusChange());
    }

    @Test
    void finishRide_WithoutCreator_Success() {
        testRide.setCreator(null);

        when(rideDestinationService.getOrderedDestinationsByRideId(100L)).thenReturn(testRoute);
        when(rideService.getRideById(100L)).thenReturn(testRide);
        when(notificationService.createWebRideFinishedNotification(anyLong(), anyLong()))
                .thenReturn(new Notification());

        assertDoesNotThrow(() -> rideCompletionService.finishRide(1L, finishRideDTO));
        assertEquals(RideStatus.FINISHED, testRide.getRideStatus());
    }

    @Test
    void finishRide_EmailsSentToAllPassengers() {
        when(rideDestinationService.getOrderedDestinationsByRideId(100L)).thenReturn(testRoute);
        when(rideService.getRideById(100L)).thenReturn(testRide);
        when(notificationService.createWebRideFinishedNotification(anyLong(), anyLong()))
                .thenReturn(new Notification());

        rideCompletionService.finishRide(1L, finishRideDTO);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> bodyCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService, times(2)).sendEmail(
                emailCaptor.capture(),
                subjectCaptor.capture(),
                bodyCaptor.capture()
        );

        List<String> capturedEmails = emailCaptor.getAllValues();
        assertTrue(capturedEmails.contains("passenger1@test.com"));
        assertTrue(capturedEmails.contains("passenger2@test.com"));

        assertEquals("Your ride has been finished", subjectCaptor.getAllValues().getFirst());
        assertTrue(bodyCaptor.getAllValues().getFirst().contains("100"));
    }

    @Test
    void finishRide_NotificationsSentToAllPassengers() {
        Notification notification1 = new Notification();
        Notification notification2 = new Notification();

        when(rideDestinationService.getOrderedDestinationsByRideId(100L)).thenReturn(testRoute);
        when(rideService.getRideById(100L)).thenReturn(testRide);
        when(notificationService.createWebRideFinishedNotification(100L, 2L))
                .thenReturn(notification1);
        when(notificationService.createWebRideFinishedNotification(100L, 3L))
                .thenReturn(notification2);

        rideCompletionService.finishRide(1L, finishRideDTO);

        verify(notificationService, times(1)).createWebRideFinishedNotification(100L, 2L);
        verify(notificationService, times(1)).createWebRideFinishedNotification(100L, 3L);
        verify(notificationService, times(1)).sendNotificationToSocket(notification1);
        verify(notificationService, times(1)).sendNotificationToSocket(notification2);
    }

    @Test
    void finishRide_VerifyRideOverviewUpdate_NormalFinish() {
        when(rideDestinationService.getOrderedDestinationsByRideId(100L)).thenReturn(testRoute);
        when(rideService.getRideById(100L)).thenReturn(testRide);
        when(notificationService.createWebRideFinishedNotification(anyLong(), anyLong()))
                .thenReturn(new Notification());

        rideCompletionService.finishRide(1L, finishRideDTO);

        ArgumentCaptor<Long> rideIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> addressCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CoordinatesDTO> coordsCaptor = ArgumentCaptor.forClass(CoordinatesDTO.class);

        verify(rideOverviewService, times(1)).sendRideOverviewUpdateDTO(
                rideIdCaptor.capture(),
                addressCaptor.capture(),
                coordsCaptor.capture()
        );

        assertEquals(100L, rideIdCaptor.getValue());
        assertEquals(45.2557, coordsCaptor.getValue().getLatitude());
        assertEquals(19.8451, coordsCaptor.getValue().getLongitude());
    }

    @Test
    void finishRide_WebSocketNotificationSent() {
        when(rideDestinationService.getOrderedDestinationsByRideId(100L)).thenReturn(testRoute);
        when(rideService.getRideById(100L)).thenReturn(testRide);
        when(notificationService.createWebRideFinishedNotification(anyLong(), anyLong()))
                .thenReturn(new Notification());

        rideCompletionService.finishRide(1L, finishRideDTO);

        verify(simpMessagingTemplate, times(1)).convertAndSend(
                "/socket-publisher/ride/finish",
                100L
        );
    }

    @Test
    void finishRide_EmptyPassengersList_NoEmailsOrNotificationsSent() {
        testRide.setPassengers(Collections.emptySet());

        when(rideDestinationService.getOrderedDestinationsByRideId(100L)).thenReturn(testRoute);
        when(rideService.getRideById(100L)).thenReturn(testRide);

        rideCompletionService.finishRide(1L, finishRideDTO);

        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
        verify(notificationService, never()).createWebRideFinishedNotification(anyLong(), anyLong());
        verify(notificationService, never()).sendNotificationToSocket(any(Notification.class));
    }

    @Test
    void finishRide_DriverStatusSetToDriving_False() {
        testDriver.setDriving(true);

        when(rideDestinationService.getOrderedDestinationsByRideId(100L)).thenReturn(testRoute);
        when(rideService.getRideById(100L)).thenReturn(testRide);
        when(notificationService.createWebRideFinishedNotification(anyLong(), anyLong()))
                .thenReturn(new Notification());

        rideCompletionService.finishRide(1L, finishRideDTO);

        assertFalse(testDriver.isDriving());
        verify(driverRepository, times(1)).save(testDriver);
    }
}