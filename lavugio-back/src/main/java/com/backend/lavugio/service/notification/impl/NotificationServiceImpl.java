package com.backend.lavugio.service.notification.impl;

import com.backend.lavugio.model.notification.Notification;
import com.backend.lavugio.model.enums.NotificationType;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.user.Account;
import com.backend.lavugio.repository.notification.NotificationRepository;
import com.backend.lavugio.repository.user.AccountRepository;
import com.backend.lavugio.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public Notification createNotification(Notification notification) {
        // Validacija
        validateNotification(notification);

        // Postavi trenutno vreme ako nije postavljeno
        if (notification.getSentDate() == null) {
            notification.setSentDate(LocalDate.now());
        }

        if (notification.getSentTime() == null) {
            notification.setSentTime(LocalTime.now());
        }

        // Provera da li korisnik postoji
        if (notification.getSentTo() != null && notification.getSentTo().getId() != null) {
            Account user = accountRepository.findById(notification.getSentTo().getId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + notification.getSentTo().getId()));
            notification.setSentTo(user);
        }

        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public Notification updateNotification(Long id, Notification notification) {
        Notification existing = getNotificationById(id);

        validateNotification(notification);

        existing.setTitle(notification.getTitle());
        existing.setText(notification.getText());
        existing.setLinkToRide(notification.getLinkToRide());
        existing.setNotificationType(notification.getNotificationType());

        return notificationRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        Notification notification = getNotificationById(id);
        notificationRepository.delete(notification);
    }

    @Override
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getNotificationsByUser(Account user) {
        return notificationRepository.findBySentTo(user);
    }

    @Override
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findBySentToId(userId);
    }

    @Override
    public List<Notification> getNotificationsByType(NotificationType type) {
        return notificationRepository.findByNotificationType(type);
    }

    @Override
    public List<Notification> getNotificationsByDate(LocalDate date) {
        return notificationRepository.findBySentDate(date);
    }

    @Override
    public List<Notification> getNotificationsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return notificationRepository.findBySentDateBetween(startDate, endDate);
    }

    @Override
    public List<Notification> searchNotifications(Long userId, NotificationType type,
                                                  LocalDate startDate, LocalDate endDate) {
        return notificationRepository.searchNotifications(userId, type, startDate, endDate);
    }

    @Override
    public List<Notification> getLinkedNotifications() {
        return notificationRepository.findLinkedNotifications();
    }

    @Override
    public List<Notification> getNotificationsByLinkedRide(String rideId) {
        return notificationRepository.findByLinkedRide(rideId);
    }

    @Override
    public List<Notification> getTodayNotifications() {
        return notificationRepository.findTodayNotifications();
    }

    @Override
    public List<Notification> getRecentPanicNotificationsByUser(Long userId) {
        return notificationRepository.findRecentPanicNotificationsByUser(userId);
    }

    @Override
    @Transactional
    public void sendPanicNotification(Long userId, String location, String message) {
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Notification notification = new Notification();
        notification.setTitle("PANIC ALERT - " + location);
        notification.setText("Panic alert from user " + user.getName() + ": " + message);
        notification.setSentTo(user);
        notification.setNotificationType(NotificationType.PANIC);
        notification.setSentDate(LocalDate.now());
        notification.setSentTime(LocalTime.now());
        notification.setLinkToRide(null);

        createNotification(notification);

        // TODO: Dodati logiku za slanje push notifikacija ili SMS-a ovde
    }

    @Override
    @Transactional
    public void sendRegularNotification(Long userId, String title, String message) {
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setText(message);
        notification.setSentTo(user);
        notification.setNotificationType(NotificationType.REGULAR);
        notification.setSentDate(LocalDate.now());
        notification.setSentTime(LocalTime.now());
        notification.setLinkToRide(null);

        createNotification(notification);
    }

    @Override
    @Transactional
    public void sendLinkedNotification(Long userId, String title, String message, String rideLink) {
        Account user = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setText(message);
        notification.setSentTo(user);
        notification.setNotificationType(NotificationType.LINKED);
        notification.setSentDate(LocalDate.now());
        notification.setSentTime(LocalTime.now());
        notification.setLinkToRide(rideLink);

        createNotification(notification);
    }

    @Override
    public void notifyPassengersAboutFinishedRide(Ride ride) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long countNotificationsByUser(Long userId) {
        return notificationRepository.countByUserId(userId);
    }

    @Override
    public long countNotificationsByType(NotificationType type) {
        return notificationRepository.countByType(type);
    }

    @Override
    @Transactional
    public void deleteOldNotifications(LocalDate cutoffDate) {
        notificationRepository.deleteBySentDateBefore(cutoffDate);
    }

    @Override
    @Transactional
    public void markAllAsReadForUser(Long userId) {
        // Ova metoda bi zahtevala dodatno polje u Notification entitetu
        // kao 'boolean read' za praćenje statusa notifikacija
        throw new UnsupportedOperationException("This feature requires a 'read' field in Notification entity");
    }

    private void validateNotification(Notification notification) {
        if (notification.getTitle() == null || notification.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Notification title is required");
        }

        if (notification.getText() == null || notification.getText().trim().isEmpty()) {
            throw new RuntimeException("Notification text is required");
        }

        if (notification.getNotificationType() == null) {
            throw new RuntimeException("Notification type is required");
        }

        if (notification.getSentTo() == null) {
            throw new RuntimeException("Notification recipient is required");
        }
    }
}