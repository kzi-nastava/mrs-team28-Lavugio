package com.backend.lavugio.scheduler;

import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.notification.Notification;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.repository.ride.RideRepository;
import com.backend.lavugio.service.notification.FirebaseService;
import com.backend.lavugio.service.notification.NotificationService;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RideNotificationScheduler {

    private final RideRepository rideRepository;
    private final NotificationService notificationService;
    private final FirebaseService firebaseService;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("HH:mm")
            .toFormatter();

    private static final String TITLE = "Ride Reminder";

    @Scheduled(fixedRate = 60000) // every minute
    @Transactional
    public void checkRidesForNotifications() {

        LocalDateTime now = LocalDateTime.now();

        List<Ride> rides = rideRepository
                .findByNextNotificationTimeBeforeAndRideStatus(
                        now,
                        RideStatus.SCHEDULED
                );

        for (Ride ride : rides) {

            if (updateNextNotificationTime(ride, now)) {
                Long creatorId = ride.getCreator().getId();
                String fcmToken = ride.getCreator().getFcmToken();
                Notification notification = notificationService.createWebRideReminderNotification(creatorId, ride.getCreator().getId());
                notificationService.sendNotificationToSocket(notification);
                Map<String, String> data = new HashMap<>();
                data.put("type", "RIDE_OVERVIEW");
                data.put("rideId", String.valueOf(ride.getId()));
                firebaseService.sendPushNotificationWithDataPayload(fcmToken,
                        "You have ride scheduled at " + ride.getStartDateTime().format(DATE_TIME_FORMATTER),
                        TITLE,
                        data);
            }

            rideRepository.save(ride);
        }
    }

    private boolean updateNextNotificationTime(Ride ride, LocalDateTime now) {

        LocalDateTime start = ride.getStartDateTime();
        long minutesUntilStart = Duration.between(now, start).toMinutes();

        if (minutesUntilStart <= 0) {
            ride.setNextNotificationTime(null);
            return false;
        }

        if (minutesUntilStart <= 15) {
            ride.setNextNotificationTime(now.plusMinutes(5));
        } else {
            ride.setNextNotificationTime(now.plusMinutes(15));
        }
        return true;
    }
}