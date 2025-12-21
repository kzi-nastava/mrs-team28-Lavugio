package com.backend.lavugio.service.notification;

import com.backend.lavugio.model.notification.Notification;
import com.backend.lavugio.model.notification.NotificationType;
import com.backend.lavugio.model.user.Account;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface NotificationService {
    Notification createNotification(Notification notification);
    Notification updateNotification(Long id, Notification notification);
    void deleteNotification(Long id);
    Notification getNotificationById(Long id);
    List<Notification> getAllNotifications();
    List<Notification> getNotificationsByUser(Account user);
    List<Notification> getNotificationsByUserId(Long userId);
    List<Notification> getNotificationsByType(NotificationType type);
    List<Notification> getNotificationsByDate(LocalDate date);
    List<Notification> getNotificationsBetweenDates(LocalDate startDate, LocalDate endDate);
    List<Notification> searchNotifications(Long userId, NotificationType type,
                                           LocalDate startDate, LocalDate endDate);
    List<Notification> getLinkedNotifications();
    List<Notification> getNotificationsByLinkedRide(String rideId);
    List<Notification> getTodayNotifications();
    List<Notification> getRecentPanicNotificationsByUser(Long userId);
    void sendPanicNotification(Long userId, String location, String message);
    void sendRegularNotification(Long userId, String title, String message);
    void sendLinkedNotification(Long userId, String title, String message, String rideLink);
    long countNotificationsByUser(Long userId);
    long countNotificationsByType(NotificationType type);
    void deleteOldNotifications(LocalDate cutoffDate);
    void markAllAsReadForUser(Long userId);
}