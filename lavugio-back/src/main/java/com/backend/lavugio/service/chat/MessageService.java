package com.backend.lavugio.service.chat;

import com.backend.lavugio.dto.MessageDTO;
import com.backend.lavugio.model.chat.Message;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {
    Message sendMessage(Message message);
    Message updateMessage(Long id, String newText);
    void deleteMessage(Long id);
    Message getMessageById(Long id);
    List<Message> getAllMessages();
    List<Message> getConversation(Long user1Id, Long user2Id);
    List<Message> getMessagesByDate(LocalDateTime date);
    List<Message> getMessagesBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    Message sendMessage(Long senderId, Long receiverId, String text);
    long countUnreadMessages(Long userId);
    void markMessageAsRead(Long messageId, Long userId);
    void saveMessage(MessageDTO message);
}