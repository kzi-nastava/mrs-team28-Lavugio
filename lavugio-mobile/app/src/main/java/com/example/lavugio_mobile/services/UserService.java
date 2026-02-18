package com.example.lavugio_mobile.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lavugio_mobile.LavugioApp;
import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.models.CanOrderRideResponse;
import com.example.lavugio_mobile.models.RideHistoryUserDetailedModel;
import com.example.lavugio_mobile.models.RideHistoryUserPagingModel;
import com.example.lavugio_mobile.services.auth.AuthService;
import com.example.lavugio_mobile.services.user.UserApi;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
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

    // ── Profile ──────────────────────────────────────────────────────

    public void uploadProfilePicture(File file, Callback<Object> callback) {
        RequestBody requestFile = RequestBody.create(
                MediaType.parse("image/*"),
                file
        );

        MultipartBody.Part body = MultipartBody.Part.createFormData(
                "file",
                file.getName(),
                requestFile
        );

        api.uploadProfilePicture(body).enqueue(wrapCallback(callback));
    }

    // ── Password ─────────────────────────────────────────────────────

    public void changePassword(String oldPassword, String newPassword,
                              Callback<String> callback) {
        Map<String, String> passwordData = new HashMap<>();
        passwordData.put("oldPassword", oldPassword);
        passwordData.put("newPassword", newPassword);

        api.changePassword(passwordData).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,
                                 @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body() != null ?
                                response.body().string() : "Success";
                        callback.onSuccess(result);
                    } catch (Exception e) {
                        callback.onSuccess("Password changed successfully");
                    }
                } else {
                    callback.onError(response.code(), parseErrorMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                callback.onError(-1, t.getMessage());
            }
        });
    }

    // ── Activation ───────────────────────────────────────────────────

    public void activateAccount(String token, String password, Callback<Object> callback) {
        Map<String, String> activationData = new HashMap<>();
        activationData.put("token", token);
        activationData.put("password", password);

        api.activateAccount(activationData).enqueue(wrapCallback(callback));
    }

    public void validateActivationToken(String token, Callback<Object> callback) {
        api.validateActivationToken(token).enqueue(wrapCallback(callback));
    }

    // ── Email Search ─────────────────────────────────────────────────

    public void searchUserEmails(String query,
                                Callback<List<UserApi.EmailSuggestion>> callback) {
        if (query == null || query.trim().length() < 2) {
            callback.onSuccess(java.util.Collections.emptyList());
            return;
        }

        api.searchUserEmails(query).enqueue(wrapCallback(callback));
    }

    // ── Blocking ─────────────────────────────────────────────────────

    public void blockUser(String email, String reason, Callback<Object> callback) {
        Map<String, String> blockData = new HashMap<>();
        blockData.put("email", email);
        blockData.put("reason", reason);

        api.blockUser(blockData).enqueue(wrapCallback(callback));
    }

    public void isUserBlocked(Callback<UserApi.BlockStatus> callback) {
        api.isUserBlocked().enqueue(wrapCallback(callback));
    }

    public void canUserOrderRide(Callback<CanOrderRideResponse> callback) {
        api.canUserOrderRide().enqueue(wrapCallback(callback));
    }

    // ── Ride History ─────────────────────────────────────────────────

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

    // ── Helper Methods ───────────────────────────────────────────────

    /**
     * Get current user ID from stored session/preferences.
     * Implement according to your authentication strategy.
     */
    public long getCurrentUserId() {
        return AuthService.getInstance().getUserId();
    }

    /**
     * Get current user name from stored session/preferences.
     * Implement according to your authentication strategy.
     */
    public String getCurrentUserName() {
        return AuthService.getInstance().getStoredUser().getName();
    }

    // ── Internal ─────────────────────────────────────────────────────

    private <T> retrofit2.Callback<T> wrapCallback(Callback<T> callback) {
        return new retrofit2.Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call,
                                 @NonNull Response<T> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(response.code(), parseErrorMessage(response));
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                Log.e(TAG, "Request failed", t);
                callback.onError(-1, t.getMessage());
            }
        };
    }

    private <T> String parseErrorMessage(Response<T> response) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                org.json.JSONObject json = new org.json.JSONObject(errorJson);
                if (json.has("message")) {
                    return json.getString("message");
                }
            }
        } catch (Exception ignored) {}
        return "Request failed (code " + response.code() + ")";
    }
}
