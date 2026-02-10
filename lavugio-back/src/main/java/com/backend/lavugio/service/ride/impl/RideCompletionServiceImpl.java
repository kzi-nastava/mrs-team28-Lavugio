package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.dto.ride.FinishRideDTO;
import com.backend.lavugio.model.enums.NotificationType;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.notification.Notification;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.route.Address;
import com.backend.lavugio.model.route.RideDestination;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.repository.user.DriverRepository;
import com.backend.lavugio.service.notification.NotificationService;
import com.backend.lavugio.service.ride.RideCompletionService;
import com.backend.lavugio.service.ride.RideOverviewService;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.route.RideDestinationService;
import com.backend.lavugio.service.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RideCompletionServiceImpl implements RideCompletionService {

    private final RideDestinationService rideDestinationService;
    private final RideOverviewService rideOverviewService;
    private final RideService rideService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final DriverRepository driverRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public RideCompletionServiceImpl(NotificationService notificationService,
                                     RideDestinationService rideDestinationService,
                                     RideService rideService,
                                     EmailService emailService,
                                     RideOverviewService rideOverviewService,
                                     DriverRepository driverRepository,
                                     SimpMessagingTemplate simpMessagingTemplate) {
        this.rideDestinationService = rideDestinationService;
        this.rideService = rideService;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.rideOverviewService = rideOverviewService;
        this.driverRepository = driverRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Transactional
    public void finishRide(Long driverId, FinishRideDTO rideDTO){
        List<RideDestination> route = rideDestinationService.getOrderedDestinationsByRideId(rideDTO.getRideId());
        if (route.isEmpty()){
            throw new NoSuchElementException("Cannot find route for ride "+rideDTO.getRideId());
        }
        Ride ride = rideService.getRideById(rideDTO.getRideId());
        if (ride==null){
            throw new NoSuchElementException("Cannot find ride for ride "+rideDTO.getRideId());
        }

        CoordinatesDTO finalDestinationCoords;
        String finalDestinationAddress;

        if (rideDTO.isFinishedEarly()){
            if (rideDTO.getFinalDestination() == null) {
                throw new IllegalArgumentException("Final destination coordinates required for early finish");
            }

            RideDestination lastDestination = route.getLast();
            Address stoppingAddress = lastDestination.getAddress();

            stoppingAddress.setLatitude(rideDTO.getFinalDestination().getLatitude());
            stoppingAddress.setLongitude(rideDTO.getFinalDestination().getLongitude());

            stoppingAddress.setStreetName("Early Stop Location");
            stoppingAddress.setStreetNumber("");

            if (rideDTO.getDistance() != null && rideDTO.getDistance() > 0) {
                double newPrice = rideService.calculatePrice(ride.getDriver().getVehicle().getType(), rideDTO.getDistance());
                ride.setPrice((float) newPrice);
                ride.setDistance(rideDTO.getDistance().floatValue());
            }

            finalDestinationCoords = rideDTO.getFinalDestination();
            finalDestinationAddress = stoppingAddress.toString();
        } else {
            finalDestinationCoords = new CoordinatesDTO(
                route.getLast().getAddress().getLatitude(),
                route.getLast().getAddress().getLongitude()
            );
            finalDestinationAddress = route.getLast().getAddress().toString();
        }
        if (!ride.getDriver().getId().equals(driverId)){
            throw new IllegalStateException("Driver isn't driving this ride");
        }
        sendEmailsToPassengers(ride.getPassengers(), ride.getId());
        sendNotificationsToPassengers(ride.getPassengers(), ride.getId());
        ride.setRideStatus(RideStatus.FINISHED);
        ride.setEndDateTime(LocalDateTime.now());
        ride.getDriver().setDriving(false);
        driverRepository.save(ride.getDriver());
        notifySocket(ride.getId());

        // Reset can_order flag for the creator (passenger)
        if (ride.getCreator() != null) {
            ride.getCreator().setCanOrder(true);
        }

        // Apply pending status change if it exists
        if (ride.getDriver().getPendingStatusChange() != null) {
            ride.getDriver().setActive(ride.getDriver().getPendingStatusChange());
            ride.getDriver().setPendingStatusChange(null);
        }
        
        this.rideOverviewService.sendRideOverviewUpdateDTO(
            rideDTO.getRideId(),
            finalDestinationAddress,
            finalDestinationCoords
        );
    }

    private void sendEmailsToPassengers(Collection<RegularUser> passengers, Long rideId){
        String subject = "Your ride has been finished";
        String body = "Link to the ride: http/localhost:4200/" + rideId + "/ride-overview";
        for (RegularUser passenger : passengers) {
            emailService.sendEmail(passenger.getEmail(), subject, body);
        }
    }

    private void sendNotificationsToPassengers(Collection<RegularUser> passengers, Long rideId){
        for (RegularUser passenger : passengers) {
            Notification notification = notificationService.createWebRideFinishedNotification(rideId, passenger.getId());
            notificationService.sendNotificationToSocket(notification);
        }
    }

    private void notifySocket(Long rideId){
        this.simpMessagingTemplate.convertAndSend(
                "/socket-publisher/ride/finish",
                rideId
        );
    }
}
