package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.dto.CoordinatesDTO;
import com.backend.lavugio.dto.ride.FinishRideDTO;
import com.backend.lavugio.model.enums.NotificationType;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.notification.Notification;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.route.RideDestination;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.service.notification.NotificationService;
import com.backend.lavugio.service.ride.RideCompletionService;
import com.backend.lavugio.service.ride.RideOverviewService;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.route.RideDestinationService;
import com.backend.lavugio.service.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    @Autowired
    public RideCompletionServiceImpl(NotificationService notificationService, RideDestinationService rideDestinationService,  RideService rideService, EmailService emailService, RideOverviewService rideOverviewService) {
        this.rideDestinationService = rideDestinationService;
        this.rideService = rideService;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.rideOverviewService = rideOverviewService;
    }

    public void finishRide(FinishRideDTO rideDTO){
        List<RideDestination> route = rideDestinationService.getOrderedDestinationsByRideId(rideDTO.getRideId());
        if (route.isEmpty()){
            throw new NoSuchElementException("Cannot find route for ride "+rideDTO.getRideId());
        }
        Ride ride = rideService.getRideById(rideDTO.getRideId());
        if (ride==null){
            throw new NoSuchElementException("Cannot find ride for ride "+rideDTO.getRideId());
        }
        if (rideDTO.isFinishedEarly()){
            throw new UnsupportedOperationException("Ride is finished early - not implemented yet");
        }
        ride.setRideStatus(RideStatus.FINISHED);
        ride.setEndDateTime(LocalDateTime.now());
        ride.getDriver().setDriving(false);
        
        // Reset can_order flag for the creator (passenger)
        if (ride.getCreator() != null) {
            ride.getCreator().setCanOrder(true);
        }
        
        // Apply pending status change if it exists
        if (ride.getDriver().getPendingStatusChange() != null) {
            ride.getDriver().setActive(ride.getDriver().getPendingStatusChange());
            ride.getDriver().setPendingStatusChange(null);
        }
        
        sendEmailsToPassengers(ride.getPassengers(), ride.getId());
        sendNotificationsToPassengers(ride.getPassengers(), ride.getId());
        this.rideOverviewService.sendRideOverviewUpdateDTO(rideDTO.getRideId(), route.getLast().getAddress().toString(),
                new CoordinatesDTO(route.getLast().getAddress().getLatitude(), route.getLast().getAddress().getLongitude()));
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
            Notification notification = new Notification();
            notification.setNotificationType(NotificationType.REGULAR);
            notification.setText("Your ride has been finished - not implemented yet");
            notification.setLinkToRide("http/localhost:4200/" + rideId + "/ride-overview");
            notification.setTitle("Your ride has been finished - not implemented yet");
            notification.setSentTo(passenger);
            notification.setRead(false);
            notification.setSentTime(LocalTime.now());
            notification.setSentDate(LocalDate.now());
            notificationService.createNotification(notification);
        }

    }
}
