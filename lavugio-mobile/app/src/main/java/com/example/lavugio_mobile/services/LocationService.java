package com.example.lavugio_mobile.services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.lavugio_mobile.models.Coordinates;

/**
 * Unified location service that supports both Google Mobile Services (GMS)
 * and Huawei Mobile Services (HMS).
 *
 * Automatically detects which service is available at runtime and uses it.
 */
public class LocationService {

    private static final String TAG = "LocationService";

    public interface LocationCallback {
        void onLocation(Coordinates coordinates);
        void onError(String error);
    }

    public interface ContinuousLocationCallback {
        void onLocation(Coordinates coordinates);
        void onError(String error);
    }

    public enum ServiceType {
        GMS,
        HMS,
        NONE
    }

    private final Context context;
    private final ServiceType serviceType;

    // GMS
    private com.google.android.gms.location.FusedLocationProviderClient gmsClient;
    private com.google.android.gms.location.LocationCallback gmsLocationCallback;

    // HMS
    private com.huawei.hms.location.FusedLocationProviderClient hmsClient;
    private com.huawei.hms.location.LocationCallback hmsLocationCallback;

    public LocationService(Context context) {
        this.context = context.getApplicationContext();
        this.serviceType = detectServiceType();

        switch (serviceType) {
            case GMS:
                gmsClient = com.google.android.gms.location.LocationServices
                        .getFusedLocationProviderClient(this.context);
                Log.d(TAG, "Using Google Mobile Services for location");
                break;
            case HMS:
                hmsClient = com.huawei.hms.location.LocationServices
                        .getFusedLocationProviderClient(this.context);
                Log.d(TAG, "Using Huawei Mobile Services for location");
                break;
            case NONE:
                Log.e(TAG, "Neither GMS nor HMS available on this device");
                break;
        }
    }

    // ── Service Detection ────────────────────────────────

    private ServiceType detectServiceType() {
        // Check GMS first (most common)
        try {
            int gmsResult = com.google.android.gms.common.GoogleApiAvailability
                    .getInstance()
                    .isGooglePlayServicesAvailable(context);
            if (gmsResult == com.google.android.gms.common.ConnectionResult.SUCCESS) {
                return ServiceType.GMS;
            }
        } catch (Exception e) {
            Log.d(TAG, "GMS not available: " + e.getMessage());
        }

        // Check HMS
        try {
            int hmsResult = com.huawei.hms.api.HuaweiApiAvailability
                    .getInstance()
                    .isHuaweiMobileServicesAvailable(context);
            if (hmsResult == com.huawei.hms.api.ConnectionResult.SUCCESS) {
                return ServiceType.HMS;
            }
        } catch (Exception e) {
            Log.d(TAG, "HMS not available: " + e.getMessage());
        }

        return ServiceType.NONE;
    }

    /**
     * Returns which location service is being used.
     */
    public ServiceType getActiveServiceType() {
        return serviceType;
    }

    // ── Permission Check ─────────────────────────────────

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // ── Single Location Request ──────────────────────────

    /**
     * Get the device's current location once.
     */
    public void getLocation(LocationCallback callback) {
        if (!hasLocationPermission()) {
            callback.onError("Location permission not granted. " +
                    "Please enable location access in app settings.");
            return;
        }

        switch (serviceType) {
            case GMS:
                getLocationGMS(callback);
                break;
            case HMS:
                getLocationHMS(callback);
                break;
            case NONE:
                callback.onError("No location services available on this device.");
                break;
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLocationGMS(LocationCallback callback) {
        // Try last known location first (fast)
        gmsClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        callback.onLocation(new Coordinates(
                                location.getLatitude(),
                                location.getLongitude()
                        ));
                    } else {
                        // No cached location — request a fresh one
                        requestFreshLocationGMS(callback);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "GMS getLastLocation failed", e);
                    requestFreshLocationGMS(callback);
                });
    }

    @SuppressWarnings("MissingPermission")
    private void requestFreshLocationGMS(LocationCallback callback) {
        com.google.android.gms.location.LocationRequest request =
                new com.google.android.gms.location.LocationRequest.Builder(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 1000)
                        .setMaxUpdates(1)
                        .setMaxUpdateDelayMillis(5000)
                        .build();

        com.google.android.gms.location.LocationCallback gmsCallback =
                new com.google.android.gms.location.LocationCallback() {
                    @Override
                    public void onLocationResult(
                            @NonNull com.google.android.gms.location.LocationResult result) {
                        gmsClient.removeLocationUpdates(this);
                        android.location.Location loc = result.getLastLocation();
                        if (loc != null) {
                            callback.onLocation(new Coordinates(
                                    loc.getLatitude(), loc.getLongitude()));
                        } else {
                            callback.onError("Unable to determine location (GMS)");
                        }
                    }
                };

        gmsClient.requestLocationUpdates(request, gmsCallback, Looper.getMainLooper());
    }

    @SuppressWarnings("MissingPermission")
    private void getLocationHMS(LocationCallback callback) {
        hmsClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        callback.onLocation(new Coordinates(
                                location.getLatitude(),
                                location.getLongitude()
                        ));
                    } else {
                        requestFreshLocationHMS(callback);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "HMS getLastLocation failed", e);
                    requestFreshLocationHMS(callback);
                });
    }

    @SuppressWarnings("MissingPermission")
    private void requestFreshLocationHMS(LocationCallback callback) {
        com.huawei.hms.location.LocationRequest request =
                new com.huawei.hms.location.LocationRequest();
        request.setPriority(
                com.huawei.hms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);
        request.setNumUpdates(1);

        com.huawei.hms.location.LocationCallback hmsCallback =
                new com.huawei.hms.location.LocationCallback() {
                    @Override
                    public void onLocationResult(
                            com.huawei.hms.location.LocationResult result) {
                        hmsClient.removeLocationUpdates(this);
                        if (result != null && result.getLastLocation() != null) {
                            android.location.Location loc = result.getLastLocation();
                            callback.onLocation(new Coordinates(
                                    loc.getLatitude(), loc.getLongitude()));
                        } else {
                            callback.onError("Unable to determine location (HMS)");
                        }
                    }
                };

        hmsClient.requestLocationUpdates(request, hmsCallback, Looper.getMainLooper());
    }

    // ── Continuous Location Updates ──────────────────────

    /**
     * Start receiving location updates at the specified interval.
     *
     * @param intervalMs  how often to receive updates (milliseconds)
     * @param callback    called each time a new location is received
     */
    public void startLocationUpdates(long intervalMs, ContinuousLocationCallback callback) {
        if (!hasLocationPermission()) {
            callback.onError("Location permission not granted.");
            return;
        }

        switch (serviceType) {
            case GMS:
                startUpdatesGMS(intervalMs, callback);
                break;
            case HMS:
                startUpdatesHMS(intervalMs, callback);
                break;
            case NONE:
                callback.onError("No location services available.");
                break;
        }
    }

    @SuppressWarnings("MissingPermission")
    private void startUpdatesGMS(long intervalMs, ContinuousLocationCallback callback) {
        com.google.android.gms.location.LocationRequest request =
                new com.google.android.gms.location.LocationRequest.Builder(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                        intervalMs)
                        .setMinUpdateIntervalMillis(intervalMs / 2)
                        .build();

        gmsLocationCallback = new com.google.android.gms.location.LocationCallback() {
            @Override
            public void onLocationResult(
                    @NonNull com.google.android.gms.location.LocationResult result) {
                android.location.Location loc = result.getLastLocation();
                if (loc != null) {
                    callback.onLocation(new Coordinates(
                            loc.getLatitude(), loc.getLongitude()));
                }
            }
        };

        gmsClient.requestLocationUpdates(request, gmsLocationCallback, Looper.getMainLooper());
        Log.d(TAG, "GMS continuous location updates started (interval: " + intervalMs + "ms)");
    }

    @SuppressWarnings("MissingPermission")
    private void startUpdatesHMS(long intervalMs, ContinuousLocationCallback callback) {
        com.huawei.hms.location.LocationRequest request =
                new com.huawei.hms.location.LocationRequest();
        request.setPriority(
                com.huawei.hms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(intervalMs);
        request.setFastestInterval(intervalMs / 2);

        hmsLocationCallback = new com.huawei.hms.location.LocationCallback() {
            @Override
            public void onLocationResult(com.huawei.hms.location.LocationResult result) {
                if (result != null && result.getLastLocation() != null) {
                    android.location.Location loc = result.getLastLocation();
                    callback.onLocation(new Coordinates(
                            loc.getLatitude(), loc.getLongitude()));
                }
            }
        };

        hmsClient.requestLocationUpdates(request, hmsLocationCallback, Looper.getMainLooper());
        Log.d(TAG, "HMS continuous location updates started (interval: " + intervalMs + "ms)");
    }

    // ── Stop Updates ─────────────────────────────────────

    /**
     * Stop receiving continuous location updates.
     */
    public void stopLocationUpdates() {
        switch (serviceType) {
            case GMS:
                if (gmsClient != null && gmsLocationCallback != null) {
                    gmsClient.removeLocationUpdates(gmsLocationCallback);
                    gmsLocationCallback = null;
                    Log.d(TAG, "GMS location updates stopped");
                }
                break;
            case HMS:
                if (hmsClient != null && hmsLocationCallback != null) {
                    hmsClient.removeLocationUpdates(hmsLocationCallback);
                    hmsLocationCallback = null;
                    Log.d(TAG, "HMS location updates stopped");
                }
                break;
        }
    }
}