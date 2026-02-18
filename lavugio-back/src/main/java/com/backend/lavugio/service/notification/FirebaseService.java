package com.backend.lavugio.service.notification;

public interface FirebaseService {
    public void sendPushNotification(String token, String message, String title);
}
