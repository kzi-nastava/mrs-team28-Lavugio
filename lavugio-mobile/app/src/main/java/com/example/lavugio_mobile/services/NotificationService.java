package com.example.lavugio_mobile.services;

import androidx.annotation.NonNull;

import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.api.LocalDateTimeAdapter;
import com.example.lavugio_mobile.api.NotificationApi;
import com.example.lavugio_mobile.models.NotificationModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationService {

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(int code, String message);
    }

    private final NotificationApi api;

    public NotificationService() {
        // Isti LocalDateTimeAdapter koji koristi i RideService
        this.api = ApiClient.getNotificationApi();
    }

    // ── API pozivi ────────────────────────────────────────────────────────────

    public void getNotifications(Callback<List<NotificationModel>> callback) {
        api.getNotifications().enqueue(wrapCallback(callback));
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private <T> retrofit2.Callback<T> wrapCallback(Callback<T> callback) {
        return new retrofit2.Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call,
                                   @NonNull Response<T> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Error";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorMsg = response.message();
                    }
                    callback.onError(response.code(), errorMsg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call,
                                  @NonNull Throwable t) {
                android.util.Log.e("NotificationService", "Request failed", t);
                callback.onError(-1, t.getMessage());
            }
        };
    }
}