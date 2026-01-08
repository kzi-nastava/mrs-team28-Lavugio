package com.backend.lavugio.service.chat.impl;

import com.backend.lavugio.model.chat.Message;
import com.backend.lavugio.repository.chat.MessageRepository;
import com.backend.lavugio.service.chat.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public List<Message> getChatHistory(Long userId) {
        return messageRepository.findUserMessagesOrderByDateDesc(userId);
    }

    @Override
    @Transactional
    public void markMessagesAsRead(Long userId, List<Long> messageIds) {
        List<Message> messages = messageRepository.findAllById(messageIds);
        for (Message message : messages) {
            if (message.getReceiver().getId().equals(userId)) {
                message.setRead(true);
                messageRepository.save(message);
            }
        }
    }

    @Override
    public List<Message> getUnreadMessages(Long userId) {
        return messageRepository.findByReceiverIdAndReadFalse(userId);
    }

    @Override
    public List<Message> searchInMessages(Long userId, String searchTerm) {
        List<Message> receivedMessages = messageRepository.searchInUserMessages(userId, searchTerm);
        List<Message> sentMessages = messageRepository.searchInSentMessages(userId, searchTerm);

        List<Message> allMessages = receivedMessages;
        allMessages.addAll(sentMessages);

        return allMessages.stream()
                .sorted((m1, m2) -> {
                    int dateComparison = m2.getMessageDate().compareTo(m1.getMessageDate());
                    if (dateComparison != 0) return dateComparison;
                    return m2.getMessageTime().compareTo(m1.getMessageTime());
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void clearChatHistory(Long userId) {
        List<Message> sentMessages = messageRepository.findBySenderId(userId);
        List<Message> receivedMessages = messageRepository.findByReceiverId(userId);

        messageRepository.deleteAll(sentMessages);
        messageRepository.deleteAll(receivedMessages);
    }

    @Override
    public List<Message> getMessagesBetweenUsers(Long user1Id, Long user2Id) {
        return messageRepository.findConversationBetweenUsers(user1Id, user2Id);
    }

    // Dodatne metode
    @Transactional
    public void markAllMessagesAsRead(Long userId) {
        List<Message> unreadMessages = getUnreadMessages(userId);
        for (Message message : unreadMessages) {
            message.setRead(true);
            messageRepository.save(message);
        }
    }

    public long getUnreadMessageCount(Long userId) {
        return messageRepository.countByReceiverIdAndReadFalse(userId);
    }

    public List<Message> getSentMessages(Long userId) {
        return messageRepository.findBySenderId(userId);
    }

    public List<Message> getReceivedMessages(Long userId) {
        return messageRepository.findByReceiverId(userId);
    }

    public List<Message> getTodayMessages(Long userId) {
        return messageRepository.findTodayMessagesByUser(userId);
    }

    public List<Message> getRecentMessages(Long userId, int limit) {
        return messageRepository.findRecentMessagesByUser(userId, limit);
    }

    public long getTotalMessageCountForUser(Long userId) {
        return messageRepository.countMessagesByUserId(userId);
    }
}