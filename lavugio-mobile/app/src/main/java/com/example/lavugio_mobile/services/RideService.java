package com.example.lavugio_mobile.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.api.LocalDateTimeAdapter;
import com.example.lavugio_mobile.api.RideApi;
import com.example.lavugio_mobile.models.ActiveRide;
import com.example.lavugio_mobile.models.FinishRide;
import com.example.lavugio_mobile.models.RideEstimateRequest;
import com.example.lavugio_mobile.models.RideMonitoringModel;
import com.example.lavugio_mobile.models.RideOverviewModel;
import com.example.lavugio_mobile.models.RideOverviewUpdate;
import com.example.lavugio_mobile.models.RideReport;
import com.example.lavugio_mobile.models.RideRequestDTO;
import com.example.lavugio_mobile.models.RideReview;
import com.example.lavugio_mobile.models.ScheduleRideRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class RideService {

    private static final String TAG = "RideService";

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(int code, String message);
    }

    private final RideApi api;
    private final WebSocketService webSocketService;
    private WebSocketService.StompSubscription rideUpdateSubscription;

    public RideService(WebSocketService webSocketService) {
        this.api = ApiClient.getRideApi();
        this.webSocketService = webSocketService;
    }

    // ── Ride Overview ────────────────────────────────────

    public void getRideOverview(long rideId, Callback<RideOverviewModel> callback) {
        api.getRideOverview(rideId).enqueue(wrapCallback(callback));
    }

    public void getUpdatedRideOverview(long rideId, Callback<RideOverviewModel> callback) {
        api.getUpdatedRideOverview(rideId).enqueue(wrapCallback(callback));
    }

    // ── Ride Actions ─────────────────────────────────────

    public void startRide(long rideId, Callback<Void> callback) {
        api.startRide(rideId).enqueue(wrapCallback(callback));
    }

    public void finishRide(FinishRide finish, Callback<FinishRide> callback) {
        api.finishRide(finish).enqueue(wrapCallback(callback));
    }

    public void cancelRideByPassenger(long rideId, Callback<Void> callback) {
        api.cancelRideByPassenger(rideId).enqueue(wrapCallback(callback));
    }

    public void cancelRideByDriver(long rideId, String reason, Callback<Void> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("reason", reason);
        api.cancelRideByDriver(rideId, body).enqueue(wrapCallback(callback));
    }

    public void triggerPanic(long rideId, Object panicAlert, Callback<Void> callback) {
        api.triggerPanic(rideId, panicAlert).enqueue(wrapCallback(callback));
    }

    // ── Report & Review ──────────────────────────────────

    public void postRideReport(long rideId, RideReport report, Callback<RideReport> callback) {
        api.postRideReport(report).enqueue(wrapCallback(callback));
    }

    public void postRideReview(long rideId, RideReview review, Callback<Void> callback) {
        api.postRideReview(rideId, review).enqueue(wrapCallback(callback));
    }

    // ── Ride Booking ─────────────────────────────────────

    public void estimatePrice(RideEstimateRequest request, Callback<Object> callback) {
        api.estimatePrice(request).enqueue(wrapCallback(callback));
    }

    public void scheduleRide(ScheduleRideRequest request, Callback<Object> callback) {
        api.scheduleRide(request).enqueue(wrapCallback(callback));
    }

    public void findRide(RideRequestDTO request, Callback<Object> callback) {
        api.findRide(request).enqueue(wrapCallback(callback));
    }

    // ── Access Check ─────────────────────────────────────

    public void canAccess(long rideId, Callback<Boolean> callback) {
        api.canAccess(rideId).enqueue(wrapCallback(callback));
    }

    // ── Active Rides ─────────────────────────────────────

    public void getUserActiveRides(Callback<List<ActiveRide>> callback) {
        long timestamp = System.currentTimeMillis();
        api.getUserActiveRides(timestamp).enqueue(wrapCallback(callback));
    }

    public void getActiveRides(Callback<List<RideMonitoringModel>> callback) {
        api.getActiveRides().enqueue(wrapCallback(callback));
    }

    // ── WebSocket: Listen to ride updates ────────────────

    /**
     * Subscribe to real-time ride updates via STOMP WebSocket.
     * Mirrors Angular's listenToRideUpdates().
     */
    public void listenToRideUpdates(long rideId, Callback<RideOverviewUpdate> callback) {
        String destination = "/socket-publisher/rides/" + rideId + "/update";

        com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        webSocketService.connect(() ->
                rideUpdateSubscription = webSocketService.subscribe(destination, body -> {
                    try {
                        RideOverviewUpdate update = gson.fromJson(body, RideOverviewUpdate.class);
                        callback.onSuccess(update);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse ride update", e);
                        callback.onError(-1, "Failed to parse update: " + e.getMessage());
                    }
                })
        );
    }

    /**
     * Unsubscribe from ride updates and close the WebSocket connection.
     */
    public void closeConnection() {
        if (rideUpdateSubscription != null) {
            rideUpdateSubscription.unsubscribe();
            rideUpdateSubscription = null;
        }
        // Note: don't disconnect the shared WebSocketService here —
        // other parts of the app may still be using it.
        Log.d(TAG, "Ride update subscription closed");
    }

    // ── Internal ─────────────────────────────────────────

    private <T> retrofit2.Callback<T> wrapCallback(Callback<T> callback) {
        return new retrofit2.Callback<T>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<T> call,
                                   @NonNull retrofit2.Response<T> response) {
                if (response.isSuccessful()) {
                    // Allow null body for 200/204 responses
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
            public void onFailure(@NonNull retrofit2.Call<T> call,
                                  @NonNull Throwable t) {
                android.util.Log.e("RideService", "Request failed", t);
                callback.onError(-1, t.getMessage());
            }
        };
    }

    private <T> String parseErrorMessage(retrofit2.Response<T> response) {
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