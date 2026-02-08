package com.backend.lavugio.repository.notification;

import com.backend.lavugio.model.notification.Notification;
import com.backend.lavugio.model.enums.NotificationType;
import com.backend.lavugio.model.user.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findBySentTo(Account user);

    List<Notification> findBySentToId(Long userId);

    List<Notification> findByNotificationType(NotificationType type);

    List<Notification> findBySentDate(LocalDateTime date);

    List<Notification> findBySentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Notification> findByTitleContainingIgnoreCase(String titleKeyword);

    List<Notification> findByTextContainingIgnoreCase(String textKeyword);

    @Query("SELECT n FROM Notification n WHERE " +
            "(:userId IS NULL OR n.sentTo.id = :userId) AND " +
            "(:type IS NULL OR n.notificationType = :type) AND " +
            "(:startDate IS NULL OR n.sentDate >= :startDate) AND " +
            "(:endDate IS NULL OR n.sentDate <= :endDate) " +
            "ORDER BY n.sentDate DESC")
    List<Notification> searchNotifications(Long userId, NotificationType type,
                                           LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT n FROM Notification n WHERE n.linkToRide IS NOT NULL")
    List<Notification> findLinkedNotifications();

    @Query("SELECT n FROM Notification n WHERE n.linkToRide = :rideId")
    List<Notification> findByLinkedRide(String rideId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.sentTo.id = :userId AND n.notificationType = :type")
    long countByUserIdAndType(Long userId, NotificationType type);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.sentTo.id = :userId")
    long countByUserId(Long userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.notificationType = :type")
    long countByType(NotificationType type);

    @Query("SELECT n FROM Notification n WHERE n.sentDate = CURRENT_DATE")
    List<Notification> findTodayNotifications();

    void deleteBySentToId(Long userId);

    void deleteBySentDateBefore(LocalDateTime date);

    @Query("SELECT n FROM Notification n WHERE n.sentTo.id = :userId " +
            "ORDER BY n.sentDate DESC")
    List<Notification> findByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.sentTo.id = :userId " +
            "AND n.notificationType = 'PANIC' " +
            "ORDER BY n.sentDate DESC " +
            "LIMIT 5")
    List<Notification> findRecentPanicNotificationsByUser(Long userId);
}