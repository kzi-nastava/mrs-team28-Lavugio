package com.backend.lavugio.service.chat.impl;

import com.backend.lavugio.model.chat.Message;
import com.backend.lavugio.model.user.Account;
import com.backend.lavugio.repository.chat.MessageRepository;
import com.backend.lavugio.repository.user.AccountRepository;
import com.backend.lavugio.service.chat.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    @Transactional
    public Message sendMessage(Message message) {
        validateMessage(message);

        if (message.getMessageDate() == null) {
            message.setMessageDate(LocalDate.now());
        }

        if (message.getMessageTime() == null) {
            message.setMessageTime(LocalTime.now());
        }

        // Proveri da li pošiljalac i primalac postoje
        Account sender = accountRepository.findById(message.getSender().getId())
                .orElseThrow(() -> new RuntimeException("Sender not found with id: " + message.getSender().getId()));

        Account receiver = accountRepository.findById(message.getReceiver().getId())
                .orElseThrow(() -> new RuntimeException("Receiver not found with id: " + message.getReceiver().getId()));

        message.setSender(sender);
        message.setReceiver(receiver);
        message.setRead(false);

        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public Message updateMessage(Long id, String newText) {
        Message message = getMessageById(id);

        if (newText == null || newText.trim().isEmpty()) {
            throw new RuntimeException("Message text cannot be empty");
        }

        message.setText(newText);
        return messageRepository.save(message);
    }

    @Override
    @Transactional
    public void deleteMessage(Long id) {
        Message message = getMessageById(id);
        messageRepository.delete(message);
    }

    @Override
    public Message getMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + id));
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public List<Message> getConversation(Long user1Id, Long user2Id) {
        return messageRepository.findConversationBetweenUsers(user1Id, user2Id);
    }

    @Override
    public List<Message> getMessagesByDate(LocalDate date) {
        return messageRepository.findByMessageDate(date);
    }

    @Override
    public List<Message> getMessagesBetweenDates(LocalDate startDate, LocalDate endDate) {
        return messageRepository.findByMessageDateBetween(startDate, endDate);
    }

    @Override
    @Transactional
    public Message sendMessage(Long senderId, Long receiverId, String text) {
        Account sender = accountRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found with id: " + senderId));

        Account receiver = accountRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found with id: " + receiverId));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setText(text);
        message.setMessageDate(LocalDate.now());
        message.setMessageTime(LocalTime.now());
        message.setRead(false);

        return sendMessage(message);
    }

    @Override
    public long countUnreadMessages(Long userId) {
        return messageRepository.countByReceiverIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markMessageAsRead(Long messageId, Long userId) {
        Message message = getMessageById(messageId);

        if (!message.getReceiver().getId().equals(userId)) {
            throw new RuntimeException("Only the receiver can mark a message as read");
        }

        message.setRead(true);
        messageRepository.save(message);
    }

    private void validateMessage(Message message) {
        if (message.getSender() == null || message.getSender().getId() == null) {
            throw new RuntimeException("Message sender is required");
        }

        if (message.getReceiver() == null || message.getReceiver().getId() == null) {
            throw new RuntimeException("Message receiver is required");
        }

        if (message.getText() == null || message.getText().trim().isEmpty()) {
            throw new RuntimeException("Message text is required");
        }

        // Ne dozvoljavaj da korisnik šalje poruku samom sebi
        if (message.getSender().getId().equals(message.getReceiver().getId())) {
            throw new RuntimeException("Cannot send message to yourself");
        }
    }
}