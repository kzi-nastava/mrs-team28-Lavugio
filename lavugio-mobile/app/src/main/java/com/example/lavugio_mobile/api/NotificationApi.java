package com.example.lavugio_mobile.api;

import com.example.lavugio_mobile.models.notification.FcmTokenRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface NotificationApi {
    @POST("api/users/token")
    Call<Void> saveFcmToken(@Body FcmTokenRequest request);
}
