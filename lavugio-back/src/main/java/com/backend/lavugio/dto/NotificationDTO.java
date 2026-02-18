package com.backend.lavugio.dto;

import com.backend.lavugio.model.notification.Notification;
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
public class NotificationDTO {
    @NotNull
    Long id;

    String link;
    String title;
    String text;

    @PastOrPresent
    LocalDateTime sendDate;

    public NotificationDTO(Notification notification){
        this.id = notification.getId();
        this.link = notification.getLinkToRide();
        this.title = notification.getTitle();
        this.text = notification.getText();
        this.sendDate = notification.getSentDate();
    }
}
