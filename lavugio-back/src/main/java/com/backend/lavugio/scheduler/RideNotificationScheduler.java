package com.backend.lavugio.scheduler;

import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.notification.Notification;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.repository.ride.RideRepository;
import com.backend.lavugio.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RideNotificationScheduler {

    private final RideRepository rideRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 60000) // every minute
    public void checkRidesForNotifications() {

        LocalDateTime now = LocalDateTime.now();

        List<Ride> rides = rideRepository
                .findByNextNotificationTimeBeforeAndRideStatus(
                        now,
                        RideStatus.SCHEDULED
                );

        for (Ride ride : rides) {

            Notification notification = notificationService.createWebRideReminderNotification(ride.getId(), ride.getCreator().getId());

            notificationService.sendNotificationToSocket(notification);

            updateNextNotificationTime(ride, now);

            rideRepository.save(ride);
        }
    }

    private void updateNextNotificationTime(Ride ride, LocalDateTime now) {

        LocalDateTime start = ride.getStartDateTime();
        long minutesUntilStart = Duration.between(now, start).toMinutes();

        if (minutesUntilStart <= 0) {
            ride.setNextNotificationTime(null);
            return;
        }

        if (minutesUntilStart <= 15) {
            ride.setNextNotificationTime(now.plusMinutes(5));
        } else {
            ride.setNextNotificationTime(now.plusMinutes(15));
        }
    }
}