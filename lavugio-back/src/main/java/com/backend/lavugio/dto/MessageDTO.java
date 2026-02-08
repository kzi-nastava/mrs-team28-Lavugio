package com.backend.lavugio.dto;

import com.backend.lavugio.model.chat.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    Long senderId;
    Long receiverId;
    String text;
    LocalDateTime timestamp;

    public MessageDTO(Message message){
        senderId = message.getSender() == null ? 0 : message.getSender().getId();
        receiverId = message.getReceiver() == null ? 0 : message.getReceiver().getId();
        text = message.getText();
        timestamp = message.getTimestamp();
    }
}
