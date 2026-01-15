package com.backend.lavugio.service.notification.impl;

import com.backend.lavugio.model.enums.NotificationType;
import com.backend.lavugio.service.notification.NotificationTypeService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class NotificationTypeServiceImpl implements NotificationTypeService {

    @Override
    public List<NotificationType> getAllNotificationTypes() {
        return Arrays.asList(NotificationType.values());
    }

    @Override
    public boolean isValidNotificationType(String type) {
        try {
            NotificationType.valueOf(type.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}