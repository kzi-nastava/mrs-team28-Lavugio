package com.example.lavugio_mobile.api;

import com.example.lavugio_mobile.models.NotificationModel;
import com.example.lavugio_mobile.models.notification.FcmTokenRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface NotificationApi {
    @POST("api/users/token")
    Call<Void> saveFcmToken(@Body FcmTokenRequest request);

    @GET("api/users/notifications")
    Call<List<NotificationModel>> getNotifications();
}
