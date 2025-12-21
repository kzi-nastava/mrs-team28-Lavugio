package com.backend.lavugio.service.chat;

import com.backend.lavugio.model.chat.Message;
import com.backend.lavugio.model.user.AccountType;

import java.util.List;
import java.util.Map;

public interface ChatService {
    List<Message> getChatHistory(Long userId);
    void markMessagesAsRead(Long userId, List<Long> messageIds);
    List<Message> getUnreadMessages(Long userId);
    Map<AccountType, Long> getMessageStatisticsBySenderType();
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