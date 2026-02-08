package com.backend.lavugio.service.chat;

import com.backend.lavugio.dto.MessageDTO;
import com.backend.lavugio.model.chat.Message;

import java.util.List;

public interface ChatService {
    List<MessageDTO> getChatHistory(Long userId);
    void markMessagesAsRead(Long userId, List<Long> messageIds);
    List<Message> getUnreadMessages(Long userId);
    List<Message> searchInMessages(Long userId, String searchTerm);
    void clearChatHistory(Long userId);
    List<Message> getMessagesBetweenUsers(Long user1Id, Long user2Id);
    void markAllMessagesAsRead(Long userId);
    long getUnreadMessageCount(Long userId);
    List<Message> getSentMessages(Long userId);
    List<Message> getReceivedMessages(Long userId);
    List<Message> getTodayMessages(Long userId);
    List<Message> getRecentMessages(Long userId, int limit);
    long getTotalMessageCountForUser(Long userId);
}