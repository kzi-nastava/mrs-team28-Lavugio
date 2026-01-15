package com.backend.lavugio.service.notification;

import com.backend.lavugio.model.enums.NotificationType;

import java.util.List;

public interface NotificationTypeService {
    List<NotificationType> getAllNotificationTypes();
    boolean isValidNotificationType(String type);
}