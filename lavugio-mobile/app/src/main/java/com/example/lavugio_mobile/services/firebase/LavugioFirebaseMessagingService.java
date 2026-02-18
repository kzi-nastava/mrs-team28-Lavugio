package com.example.lavugio_mobile.services.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.api.NotificationApi;
import com.example.lavugio_mobile.api.RidesReportsApi;
import com.example.lavugio_mobile.models.notification.FcmTokenRequest;
import com.example.lavugio_mobile.services.utils.RetrofitClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LavugioFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Primljena notifikacija");

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            showNotification(title, body);
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.d(TAG, "Novi FCM token: " + token);

        sendTokenToBackend(token);
    }

    private void sendTokenToBackend(String token) {
        NotificationApi api = ApiClient.getInstance().create(NotificationApi.class);
        FcmTokenRequest request = new FcmTokenRequest(token);
        api.saveFcmToken(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Token uspešno poslat na backend");
                } else {
                    Log.e(TAG, "Greška pri slanju tokena: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Neuspešan poziv: " + t.getMessage());
            }
        });
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "lavugio_channel";

        // ANDROID CHANNEL FOR NOTIFICATIONS (REQUIRED FOR ANDROID 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Lavugio Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.email_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }

    public static void requestAndSendToken(Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Failed to get FCM token", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);

                    // Pošalji na backend
                    NotificationApi api = ApiClient.getInstance().create(NotificationApi.class);
                    FcmTokenRequest request = new FcmTokenRequest(token);

                    api.saveFcmToken(request).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Log.d(TAG, "Token uspešno poslat na backend");
                            } else {
                                Log.e(TAG, "Greška pri slanju tokena: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "Neuspešan poziv: " + t.getMessage());
                        }
                    });
                });
    }
}
