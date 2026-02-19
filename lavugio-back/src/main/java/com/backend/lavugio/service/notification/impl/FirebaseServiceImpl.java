package com.backend.lavugio.service.notification.impl;

import com.backend.lavugio.service.notification.FirebaseService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FirebaseServiceImpl implements FirebaseService {

    @Override
    public void sendPushNotification(String token, String message, String title) {
        if (token == null || token.isEmpty()) {
            System.err.println("FCM token is null or empty");
            return;
        }
        Message firebaseMessage = Message.builder()
                .setToken(token)
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(message)
                                .build()
                )
                .build();

        try {
            FirebaseMessaging.getInstance().send(firebaseMessage);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendPushNotificationWithDataPayload(
            String token,
            String message,
            String title,
            Map<String, String> data
    ) {
        if (token == null || token.isEmpty()) {
            System.err.println("FCM token is null or empty");
            return;
        }

        data.put("title", title);
        data.put("body", message);

        Message.Builder builder = Message.builder().setToken(token);

        if (data != null) {
            data.forEach(builder::putData);
        }



        Message firebaseMessage = builder.build();

        try {
            FirebaseMessaging.getInstance().send(firebaseMessage);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
    }

}
