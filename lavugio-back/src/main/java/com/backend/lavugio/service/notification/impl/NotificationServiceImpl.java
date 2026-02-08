package com.backend.lavugio.service.notification.impl;

import com.backend.lavugio.dto.NotificationDTO;
import com.backend.lavugio.model.notification.Notification;
import com.backend.lavugio.model.enums.NotificationType;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.user.Account;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.repository.notification.NotificationRepository;
import com.backend.lavugio.repository.ride.RideRepository;
import com.backend.lavugio.repository.user.AccountRepository;
import com.backend.lavugio.service.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    @Transactional
    public Notification createNotification(Notification notification) {
        // Validacija
        validateNotification(notification);

        // Postavi trenutno vreme ako nije postavljeno
        if (notification.getSentDate() == null) {
            notification.setSentDate(LocalDateTime.now());
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
    public Notification createNotification(String title, String text, String linkToRide, Long sentToId, NotificationType type) {
        Notification notification = new Notification();
        Optional<Account> account = accountRepository.findById(sentToId);
        if (account.isEmpty()){
            throw new NoSuchElementException(String.format("User with id: %d not found", sentToId));
        }
        notification.setTitle(title);
        notification.setText(text);
        notification.setLinkToRide(linkToRide);
        notification.setSentTo(account.get());
        notification.setNotificationType(type);
        notification.setSentDate(LocalDateTime.now());
        notification.setRead(false);
        notificationRepository.save(notification);
        notificationRepository.flush();
        return notification;
    }

    @Override
    public Notification createWebRideFinishedNotification(Long rideId, Long sentToId) {
        String linkToRide = rideId + "/ride-overview";
        String title = "Your ride has been finished";
        Optional<Ride> rideOptional = rideRepository.findById(rideId);
        if (rideOptional.isEmpty()){
            throw new NoSuchElementException(String.format("Ride with id: %d not found", rideId));
        }
        Ride ride = rideOptional.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDateTime = ride.getStartDateTime().format(formatter);
        String text = String.format("Your ride from %s to %s that started at %s has been finished",
                ride.getStartAddress(),
                ride.getEndAddress(),
                formattedDateTime);
        return createNotification(title, text, linkToRide, sentToId, NotificationType.LINKED);
    }

    @Override
    public Notification createWebAddedToRideNotification(Long rideId, Long sentToId) {
        String linkToRide = rideId + "/ride-overview";
        String title = "You were added to a ride";
        Optional<Ride> rideOptional = rideRepository.findById(rideId);
        if (rideOptional.isEmpty()){
            throw new NoSuchElementException(String.format("Ride with id: %d not found", rideId));
        }
        Ride ride = rideOptional.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDateTime = ride.getStartDateTime().format(formatter);
        String text = String.format("You have been added to a ride that starts at %s",
                formattedDateTime);
        return createNotification(title, text, linkToRide, sentToId, NotificationType.LINKED);
    }

    @Override
    public Notification createWebCancelledRideNotification(Long rideId, Long sentToId) {
        String linkToRide = rideId + "/ride-overview";
        String title = "Your ride has been cancelled";
        Optional<Ride> rideOptional = rideRepository.findById(rideId);
        if (rideOptional.isEmpty()){
            throw new NoSuchElementException(String.format("Ride with id: %d not found", rideId));
        }
        Ride ride = rideOptional.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedDateTime = ride.getStartDateTime().format(formatter);
        String text = String.format("Your ride from %s to %s that starts at %s has been cancelled",
                ride.getStartAddress(),
                ride.getEndAddress(),
                formattedDateTime);
        return createNotification(title, text, linkToRide, sentToId, NotificationType.LINKED);
    }

    @Override
    public void sendNotificationToSocket(Notification notification){
        if (notification.getSentTo() == null || notification.getSentTo().getId() == null) {
            log.warn("Cannot send notification - recipient is null");
            return;
        }
        try {
            NotificationDTO notificationDTO = new NotificationDTO(notification);
            String destination = "/socket-publisher/notifications/" + notification.getSentTo().getId();

            this.simpMessagingTemplate.convertAndSend(destination, notificationDTO);

            log.info("Notification sent to user {} via WebSocket", notification.getSentTo().getId());
        } catch (Exception e) {
            log.error("Failed to send notification to user {}: {}",
                    notification.getSentTo().getId(),
                    e.getMessage());
        }
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
        return notificationRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Override
    public List<NotificationDTO> getNotificationDTOsByUserId(Long userId) {
        return this.getNotificationsByUserId(userId).stream().map(NotificationDTO::new).toList();
    }

    @Override
    public List<Notification> getNotificationsByType(NotificationType type) {
        return notificationRepository.findByNotificationType(type);
    }

    @Override
    public List<Notification> getNotificationsByDate(LocalDateTime date) {
        return notificationRepository.findBySentDate(date);
    }

    @Override
    public List<Notification> getNotificationsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return notificationRepository.findBySentDateBetween(startDate, endDate);
    }

    @Override
    public List<Notification> searchNotifications(Long userId, NotificationType type,
                                                  LocalDateTime startDate, LocalDateTime endDate) {
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
        notification.setSentDate(LocalDateTime.now());
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
        notification.setSentDate(LocalDateTime.now());
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
        notification.setSentDate(LocalDateTime.now());
        notification.setLinkToRide(rideLink);

        createNotification(notification);
    }

    @Override
    public void notifyPassengersAboutFinishedRide(Ride ride) {
        for (RegularUser passenger : ride.getPassengers()) {
            Notification notification = new Notification();
            notification.setNotificationType(NotificationType.REGULAR);
            notification.setTitle("Ride Completed");
            notification.setText("Your ride #" + ride.getId() + " has been completed.");
            notification.setLinkToRide("http://localhost:4200/" + ride.getId() + "/ride-overview");
            notification.setSentTo(passenger);
            notification.setRead(false);
            notification.setSentDate(LocalDateTime.now());
            createNotification(notification);
        }
    }

    @Override
    @Transactional
    public void notifyPassengersAboutCancellation(Ride ride, String reason, boolean byDriver) {
        String title = byDriver ? "Ride Cancelled by Driver" : "Ride Cancelled";
        String message = byDriver 
            ? "Your ride #" + ride.getId() + " has been cancelled by the driver. Reason: " + reason
            : "Ride #" + ride.getId() + " has been cancelled.";
        
        System.out.println("=== NOTIFICATION SERVICE: Creating cancellation notifications ===");
        System.out.println("Ride ID: " + ride.getId());
        System.out.println("Number of passengers: " + ride.getPassengers().size());
        System.out.println("By Driver: " + byDriver);
        System.out.println("Reason: " + reason);
            
        for (RegularUser passenger : ride.getPassengers()) {
            System.out.println("Processing passenger: " + passenger.getId() + " - " + passenger.getEmail());
            Notification notification = new Notification();
            notification.setNotificationType(NotificationType.REGULAR);
            notification.setTitle(title);
            notification.setText(message);
            notification.setLinkToRide(ride.getId() + "/ride-overview");
            notification.setSentTo(passenger);
            notification.setRead(false);
            notification.setSentDate(LocalDateTime.now());
            
            try {
                Notification saved = createNotification(notification);
                sendNotificationToSocket(saved);
                System.out.println("✓ Notification ID " + saved.getId() + " created for passenger: " + passenger.getId());
            } catch (Exception e) {
                System.err.println("✗ Failed to create notification for passenger " + passenger.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        System.out.println("=== NOTIFICATION SERVICE: Finished processing ===");
    }

    @Override
    @Transactional
    public void notifyDriverAboutPassengerCancellation(Ride ride) {
        System.out.println("=== NOTIFICATION SERVICE: Notifying driver about cancellation ===");
        if (ride.getDriver() != null) {
            System.out.println("Driver ID: " + ride.getDriver().getId());

            Notification notification = new Notification();
            notification.setNotificationType(NotificationType.REGULAR);
            notification.setTitle("Ride Cancelled by Passenger");
            notification.setText("Ride #" + ride.getId() + " has been cancelled by the passenger.");
            notification.setLinkToRide("driver-scheduled-rides");
            notification.setSentTo(ride.getDriver());
            notification.setRead(false);
            notification.setSentDate(LocalDateTime.now());

            try {
                Notification saved = createNotification(notification);
                sendNotificationToSocket(saved);
                System.out.println("✓ Notification ID " + saved.getId() + " created for driver: " + ride.getDriver().getId());
            } catch (Exception e) {
                System.err.println("✗ Failed to create notification for driver: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No driver assigned to ride");
        }
        System.out.println("=== NOTIFICATION SERVICE: Finished processing ===");
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
    public void deleteOldNotifications(LocalDateTime cutoffDate) {
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