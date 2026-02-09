package com.backend.lavugio.repository.chat;

import com.backend.lavugio.model.chat.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderId(Long senderId);
    List<Message> findByReceiverId(Long receiverId);

    @Query("SELECT m FROM Message m WHERE " +
            "(m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR " +
            "(m.sender.id = :user2Id AND m.receiver.id = :user1Id) " +
            "ORDER BY m.timestamp ASC")
    List<Message> findConversationBetweenUsers(Long user1Id, Long user2Id);

    List<Message> findByTimestamp(LocalDateTime timestamp);
    List<Message> findByTimestampBetween(LocalDateTime timestamp, LocalDateTime timestamp2);

    @Query("SELECT m FROM Message m WHERE m.receiver.id = :userId AND " +
            "LOWER(m.text) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Message> searchInUserMessages(Long userId, String searchTerm);

    @Query("SELECT m FROM Message m WHERE m.sender.id = :userId AND " +
            "LOWER(m.text) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Message> searchInSentMessages(Long userId, String searchTerm);

    List<Message> findByReceiverIdAndReadFalse(Long receiverId);

    @Query("SELECT m FROM Message m WHERE m.receiver.id = :userId OR m.sender.id = :userId " +
            "ORDER BY m.timestamp DESC")
    List<Message> findUserMessagesOrderByTimestampDesc(Long userId);

    @Query("SELECT m FROM Message m WHERE m.receiver.id = :userId OR m.sender.id = :userId " +
            "ORDER BY m.timestamp ASC")
    List<Message> findUserMessagesOrderByTimestampAsc(Long userId);

    long countByReceiverIdAndReadFalse(Long receiverId);

    void deleteByTimestampBefore(LocalDateTime timestamp);

    @Query("SELECT m FROM Message m WHERE (m.receiver.id = :userId OR m.sender.id = :userId) " +
            "AND m.timestamp = CURRENT_DATE " +
            "ORDER BY m.timestamp ASC")
    List<Message> findTodayMessagesByUser(Long userId);

    @Query("SELECT m FROM Message m WHERE m.receiver.id = :userId OR m.sender.id = :userId " +
            "ORDER BY m.timestamp DESC, m.timestamp DESC " +
            "LIMIT :limit")
    List<Message> findRecentMessagesByUser(Long userId, int limit);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.sender.id = :userId OR m.receiver.id = :userId")
    long countMessagesByUserId(Long userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.sender.id = :userId")
    long countSentMessagesByUser(Long userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver.id = :userId")
    long countReceivedMessagesByUser(Long userId);

    List<Message> findAllBySenderIsNullOrderByTimestampDesc();
    List<Message> findAllBySenderIsNullOrderByTimestampAsc();
}