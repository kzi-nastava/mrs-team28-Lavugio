package com.example.lavugio_mobile.services;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.lavugio_mobile.api.DriverApi;
import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.models.Coordinates;
import com.example.lavugio_mobile.models.DriverActiveTimeDTO;
import com.example.lavugio_mobile.models.DriverLocation;
import com.example.lavugio_mobile.models.DriverRegistration;
import com.example.lavugio_mobile.models.DriverUpdateRequestDiffDTO;
import com.example.lavugio_mobile.models.EditDriverProfileRequestDTO;
import com.example.lavugio_mobile.models.RideHistoryDriverDetailedModel;
import com.example.lavugio_mobile.models.RideHistoryDriverPagingModel;
import com.example.lavugio_mobile.models.ScheduledRideModel;

import java.util.List;

import retrofit2.Call;

public class DriverService {

    private static final String TAG = "DriverService";
    private static final long TRACKING_INTERVAL_MS = 3000;

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(int code, String message);
    }

    private final DriverApi api;
    private final LocationService locationService;

    // Tracking state
    private Handler trackingHandler;
    private Runnable trackingRunnable;
    private boolean isTracking = false;

    public DriverService(LocationService locationService) {
        this.api = ApiClient.getDriverApi();
        this.locationService = locationService;
    }

    // ── Location ─────────────────────────────────────────

    public void getDriverLocations(Callback<List<DriverLocation>> callback) {
        api.getDriverLocations().enqueue(wrapCallback(callback));
    }

    public void getDriverLocation(long driverId, Callback<DriverLocation> callback) {
        api.getDriverLocation(driverId).enqueue(wrapCallback(callback));
    }

    public void putDriverCoordinates(Coordinates coords, Callback<DriverLocation> callback) {
        api.putDriverCoordinates(coords).enqueue(wrapCallback(callback));
    }

    // ── Registration ─────────────────────────────────────

    public void registerDriver(DriverRegistration data, Callback<Object> callback) {
        api.registerDriver(data).enqueue(wrapCallback(callback));
    }

    // ── Profile Edit Requests ────────────────────────────

    public void sendEditRequest(EditDriverProfileRequestDTO request, Callback<Object> callback) {
        api.sendEditRequest(request).enqueue(wrapCallback(callback));
    }

    public void getEditRequests(Callback<List<DriverUpdateRequestDiffDTO>> callback) {
        api.getEditRequests().enqueue(wrapCallback(callback));
    }

    public void approveEditRequest(long requestId, Callback<Void> callback) {
        api.approveEditRequest(requestId).enqueue(wrapCallback(callback));
    }

    public void rejectEditRequest(long requestId, Callback<Void> callback) {
        api.rejectEditRequest(requestId).enqueue(wrapCallback(callback));
    }

    // ── Scheduled Rides ──────────────────────────────────

    public void getScheduledRides(Callback<List<ScheduledRideModel>> callback) {
        api.getScheduledRides().enqueue(wrapCallback(callback));
    }

    // ── Ride History ─────────────────────────────────────

    public void getDriverRideHistory(int page, int pageSize, String sorting,
                                     String sortBy, String startDate, String endDate,
                                     Callback<RideHistoryDriverPagingModel> callback) {
        api.getDriverRideHistory(page, pageSize, sorting, sortBy, startDate, endDate)
                .enqueue(wrapCallback(callback));
    }

    public void getDriverRideHistoryDetailed(long rideId,
                                             Callback<RideHistoryDriverDetailedModel> callback) {
        api.getDriverRideHistoryDetailed(rideId).enqueue(wrapCallback(callback));
    }

    // ── Activation / Deactivation ────────────────────────

    /**
     * Activate the driver: get current location, send to backend, then start tracking.
     * Mirrors Angular: activateDriver() which chains getLocation → POST /activate → startTracking.
     */
    public void activateDriver(Callback<Object> callback) {
        locationService.getLocation(new LocationService.LocationCallback() {
            @Override
            public void onLocation(Coordinates coordinates) {
                api.activateDriver(coordinates).enqueue(new retrofit2.Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                        if (response.isSuccessful()) {
                            startTracking();
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError(response.code(), parseErrorMessage(response));
                        }
                    }

                    @Override
                    public void onFailure(Call<Object> call, Throwable t) {
                        callback.onError(-1, t.getMessage());
                    }
                });
            }

            @Override
            public void onError(String error) {
                callback.onError(-1, "Location error: " + error);
            }
        });
    }

    /**
     * Deactivate the driver: stop tracking, then tell backend.
     */
    public void deactivateDriver(Callback<Object> callback) {
        stopTracking();
        api.deactivateDriver().enqueue(wrapCallback(callback));
    }

    // ── Active Time ──────────────────────────────────────

    public void getDriverActiveLast24Hours(Callback<DriverActiveTimeDTO> callback) {
        api.getDriverActiveLast24Hours().enqueue(wrapCallback(callback));
    }

    // ── Location Tracking ────────────────────────────────

    /**
     * Start sending location to the backend every 3 seconds.
     * Mirrors Angular's startTracking() with interval(3000).
     */
    public void startTracking() {
        if (isTracking) return;
        isTracking = true;

        trackingHandler = new Handler(Looper.getMainLooper());

        // Send immediately
        updateLocation();

        // Then every 3 seconds
        trackingRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isTracking) return;
                updateLocation();
                trackingHandler.postDelayed(this, TRACKING_INTERVAL_MS);
            }
        };
        trackingHandler.postDelayed(trackingRunnable, TRACKING_INTERVAL_MS);

        Log.d(TAG, "Location tracking started (interval: " + TRACKING_INTERVAL_MS + "ms)");
    }

    /**
     * Stop sending location updates.
     */
    public void stopTracking() {
        isTracking = false;
        if (trackingHandler != null && trackingRunnable != null) {
            trackingHandler.removeCallbacks(trackingRunnable);
            trackingRunnable = null;
        }
        Log.d(TAG, "Location tracking stopped");
    }

    /**
     * Get current location and send it to the backend.
     */
    private void updateLocation() {
        locationService.getLocation(new LocationService.LocationCallback() {
            @Override
            public void onLocation(Coordinates coordinates) {
                putDriverCoordinates(coordinates, new Callback<DriverLocation>() {
                    @Override
                    public void onSuccess(DriverLocation result) {
                        Log.d(TAG, "Coordinates sent: " +
                                coordinates.getLatitude() + ", " + coordinates.getLongitude());
                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.e(TAG, "Error sending coordinates: " + message);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error getting location for tracking: " + error);
            }
        });
    }

    public boolean isTracking() {
        return isTracking;
    }

    // ── Internal ─────────────────────────────────────────

    private <T> retrofit2.Callback<T> wrapCallback(Callback<T> callback) {
        return new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, retrofit2.Response<T> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(response.code(), parseErrorMessage(response));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
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