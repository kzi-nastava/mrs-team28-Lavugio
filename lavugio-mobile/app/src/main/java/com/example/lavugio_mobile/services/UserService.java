package com.example.lavugio_mobile.services;

import android.util.Log;

import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.models.CanOrderRideResponse;
import com.example.lavugio_mobile.models.RideHistoryUserDetailedModel;
import com.example.lavugio_mobile.models.RideHistoryUserPagingModel;
import com.example.lavugio_mobile.services.user.UserApi;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class UserService {

    private static final String TAG = "UserService";

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(int code, String message);
    }

    private final UserApi api;

    public UserService() {
        this.api = ApiClient.getUserApi();
    }

    // ── Ride History ─────────────────────────────────────

    public void getUserRideHistory(int page, int pageSize, String sorting,
                                   String sortBy, String startDate, String endDate,
                                   Callback<RideHistoryUserPagingModel> callback) {
        api.getUserRideHistory(page, pageSize, sorting, sortBy, startDate, endDate)
                .enqueue(wrapCallback(callback));
    }

    public void getUserRideHistoryDetailed(long rideId,
                                          Callback<RideHistoryUserDetailedModel> callback) {
        api.getUserRideHistoryDetailed(rideId)
                .enqueue(wrapCallback(callback));
    }

    public void canUserOrderRide(Callback<CanOrderRideResponse> callback) {
        api.canUserOrderRide().enqueue(wrapCallback(callback));
    }

    // ── Helper ───────────────────────────────────────────

    private <T> retrofit2.Callback<T> wrapCallback(Callback<T> callback) {
        return new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Unknown error";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                    callback.onError(response.code(), errorMsg);
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                Log.e(TAG, "API call failed", t);
                callback.onError(-1, t.getMessage() != null ? t.getMessage() : "Network error");
            }
        };
    }
}
