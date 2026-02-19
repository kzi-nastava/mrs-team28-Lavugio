package com.example.lavugio_mobile.services.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.lavugio_mobile.MainActivity;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.api.NotificationApi;
import com.example.lavugio_mobile.api.RidesReportsApi;
import com.example.lavugio_mobile.models.notification.FcmTokenRequest;
import com.example.lavugio_mobile.services.utils.RetrofitClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LavugioFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Notification received");


        Map<String, String> data = remoteMessage.getData();

        String title = data.get("title");
        String body = data.get("body");
        String type = data.get("type");
        String rideId = data.get("rideId");

        showNotification(title, body, type, rideId);
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

    private void showNotification(String title, String body, String type, String rideId) {

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "lavugio_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Lavugio Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (type != null) {
            intent.putExtra("type", type);
        }

        if (rideId != null) {
            intent.putExtra("rideId", rideId);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.email_icon)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
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
