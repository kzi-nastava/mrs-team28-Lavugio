package com.backend.lavugio.service.notification;

import java.util.Map;

public interface FirebaseService {
    void sendPushNotification(String token, String message, String title);
    void sendPushNotificationWithDataPayload(String token, String message, String title, Map<String,String> payload);
}
