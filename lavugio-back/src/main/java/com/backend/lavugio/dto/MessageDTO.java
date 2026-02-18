package com.backend.lavugio.dto;

import com.backend.lavugio.model.chat.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    @NotNull
    Long senderId;

    @NotNull
    Long receiverId;

    @NotBlank
    String text;

    @PastOrPresent
    LocalDateTime timestamp;

    public MessageDTO(Message message){
        senderId = message.getSender() == null ? 0 : message.getSender().getId();
        receiverId = message.getReceiver() == null ? 0 : message.getReceiver().getId();
        text = message.getText();
        timestamp = message.getTimestamp();
    }
}
