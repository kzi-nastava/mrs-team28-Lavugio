package com.backend.lavugio.service.notification;

import com.backend.lavugio.dto.NotificationDTO;
import com.backend.lavugio.model.notification.Notification;
import com.backend.lavugio.model.enums.NotificationType;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.user.Account;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {
    Notification createNotification(Notification notification);
    Notification createNotification(String title, String text, String linkToRide, Long sentToId, NotificationType type);

    Notification createWebRideFinishedNotification(Long rideId, Long sentToId);
    Notification createWebAddedToRideNotification(Long rideId, Long sentToId);
    Notification createWebCancelledRideNotification(Long rideId, Long SentToId);
    void sendNotificationToSocket(Notification notification);

    Notification updateNotification(Long id, Notification notification);
    void deleteNotification(Long id);
    Notification getNotificationById(Long id);
    List<Notification> getAllNotifications();
    List<Notification> getNotificationsByUser(Account user);
    List<Notification> getNotificationsByUserId(Long userId);
    List<NotificationDTO> getNotificationDTOsByUserId(Long userId);
    List<Notification> getNotificationsByType(NotificationType type);
    List<Notification> getNotificationsByDate(LocalDateTime date);
    List<Notification> getNotificationsBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    List<Notification> searchNotifications(Long userId, NotificationType type,
                                           LocalDateTime startDate, LocalDateTime endDate);
    List<Notification> getLinkedNotifications();
    List<Notification> getNotificationsByLinkedRide(String rideId);
    List<Notification> getTodayNotifications();
    List<Notification> getRecentPanicNotificationsByUser(Long userId);
    void sendPanicNotification(Long userId, String location, String message);
    void sendRegularNotification(Long userId, String title, String message);
    void sendLinkedNotification(Long userId, String title, String message, String rideLink);
    void notifyPassengersAboutFinishedRide(Ride ride);
    void notifyPassengersAboutCancellation(Ride ride, String reason, boolean byDriver);
    void notifyDriverAboutPassengerCancellation(Ride ride);
    long countNotificationsByUser(Long userId);
    long countNotificationsByType(NotificationType type);
    void deleteOldNotifications(LocalDateTime cutoffDate);
    void markAllAsReadForUser(Long userId);
}