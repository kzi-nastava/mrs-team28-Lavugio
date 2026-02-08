package com.backend.lavugio.controller;

import com.backend.lavugio.dto.MessageDTO;
import com.backend.lavugio.security.SecurityUtils;
import com.backend.lavugio.service.chat.ChatService;
import com.backend.lavugio.service.chat.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.Role;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat/send")
    public MessageDTO sendMessage(MessageDTO message) {
        if (message != null && message.getText() != null && !message.getText().isEmpty()) {

            messageService.saveMessage(message);
            this.simpMessagingTemplate.convertAndSend(
                    "/socket-publisher/chat/" + message.getReceiverId(),
                    message
            );

            return message;
        }

        return null;
    }

    @GetMapping("/api/chat/history/{userId}")
    public ResponseEntity<List<MessageDTO>> getChatHistory(@PathVariable Long userId) {
        List<MessageDTO> history = chatService.getChatHistory(userId);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }
}