package com.example.lavugio_mobile.ui.ride;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.lavugio_mobile.LavugioApp;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.Coordinates;
import com.example.lavugio_mobile.models.RideOverviewModel;
import com.example.lavugio_mobile.models.RideOverviewUpdate;
import com.example.lavugio_mobile.models.enums.DriverStatusEnum;
import com.example.lavugio_mobile.models.enums.RideStatus;
import com.example.lavugio_mobile.services.LocationService;
import com.example.lavugio_mobile.services.RideService;
import com.example.lavugio_mobile.ui.map.OSMMapFragment;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

public class RideOverviewFragment extends Fragment implements OSMMapFragment.MapInteractionListener {

    private static final String ARG_RIDE_ID = "rideId";
    private static final long DURATION_UPDATE_INTERVAL = 5000; // 5 seconds

    private long rideId;
    private RideOverviewModel rideOverview;
    private boolean isReported = false;
    private boolean isReviewed = false;
    private boolean isInfoOpen = false;

    private RideService rideService;
    private LocationService locationService;

    private FrameLayout mapContainer;
    private LinearLayout bottomSheet;
    private LinearLayout dragHandleContainer;
    private FrameLayout rideInfoContainer;

    private OSMMapFragment mapFragment;
    private RideInfoFragment rideInfoFragment;

    private Marker startMarker;
    private Marker endMarker;
    private Marker clientMarker;

    private boolean isTrackingLocation = false;
    private boolean mapReady = false;
    private boolean pendingTrackingStart = false;

    // Last known client location
    private Coordinates lastClientLocation;

    // Duration update timer
    private Handler durationHandler;
    private Runnable durationRunnable;
    private boolean isDurationTimerRunning = false;

    public static RideOverviewFragment newInstance(long rideId) {
        RideOverviewFragment fragment = new RideOverviewFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID);
        }
        rideService = LavugioApp.getRideService();
        locationService = LavugioApp.getLocationService();
        durationHandler = new Handler(Looper.getMainLooper());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ride_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapContainer = view.findViewById(R.id.map_container);
        bottomSheet = view.findViewById(R.id.bottom_sheet);
        dragHandleContainer = view.findViewById(R.id.drag_handle_container);
        rideInfoContainer = view.findViewById(R.id.ride_info_container);

        dragHandleContainer.setOnClickListener(v -> toggleInfo());

        // Load map fragment
        mapFragment = new OSMMapFragment();
        mapFragment.setMapInteractionListener(this);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();

        // Wait for map fragment view to be ready
        getChildFragmentManager().registerFragmentLifecycleCallbacks(
                new FragmentManager.FragmentLifecycleCallbacks() {
                    @Override
                    public void onFragmentViewCreated(@NonNull FragmentManager fm,
                                                      @NonNull Fragment f,
                                                      @NonNull View v,
                                                      @Nullable Bundle saved) {
                        if (f == mapFragment) {
                            mapReady = true;
                            fm.unregisterFragmentLifecycleCallbacks(this);
                            if (rideOverview != null) {
                                drawRideOnMap(rideOverview);
                            }
                            if (pendingTrackingStart) {
                                pendingTrackingStart = false;
                                startTrackingClientLocation();
                            }
                        }
                    }
                }, false);

        // Load ride info fragment
        rideInfoFragment = RideInfoFragment.newInstance(rideId);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.ride_info_container, rideInfoFragment)
                .commit();

        fetchRideOverview();
        listenToRideUpdates();
    }

    // ── Bottom sheet ─────────────────────────────────────

    private void toggleInfo() {
        isInfoOpen = !isInfoOpen;
        rideInfoContainer.setVisibility(isInfoOpen ? View.VISIBLE : View.GONE);
    }

    // ── Fetch ride data ──────────────────────────────────

    private void fetchRideOverview() {
        rideService.getRideOverview(rideId, new RideService.Callback<RideOverviewModel>() {
            @Override
            public void onSuccess(RideOverviewModel result) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    rideOverview = result;
                    isReported = result.isReported();
                    isReviewed = result.isReviewed();

                    if (rideInfoFragment != null) {
                        rideInfoFragment.updateRideOverview(result);
                    }

                    if (mapReady) {
                        drawRideOnMap(result);
                    }

                    if (result.getStatus() == RideStatus.ACTIVE) {
                        if (mapReady) {
                            startTrackingClientLocation();
                        } else {
                            pendingTrackingStart = true;
                        }
                    }
                });
            }

            @Override
            public void onError(int code, String message) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    if (code == 404 || code == 403) {
                        Toast.makeText(getContext(),
                                "This ride is not available.", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(),
                                "Error loading ride: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // ── Draw ride on map ─────────────────────────────────

    private void drawRideOnMap(RideOverviewModel overview) {
        if (mapFragment == null || overview == null || !mapReady) return;

        List<Coordinates> checkpoints = overview.getCheckpoints();
        if (checkpoints == null || checkpoints.isEmpty()) return;

        clearRideMarkers();

        // Add ALL waypoints IN ORDER: start → intermediates → end
        for (int i = 0; i < checkpoints.size(); i++) {
            Coordinates cp = checkpoints.get(i);
            Marker m = mapFragment.addWaypoint(toGeoPoint(cp));
            if (m == null) continue;

            if (i == 0) {
                m.setTitle("Pickup: " +
                        (overview.getStartAddress() != null ? overview.getStartAddress() : "Start"));
                startMarker = m;
            } else if (i == checkpoints.size() - 1) {
                m.setTitle("Drop-off: " +
                        (overview.getEndAddress() != null ? overview.getEndAddress() : "End"));
                endMarker = m;
            } else {
                m.setTitle("Waypoint " + i);
            }
        }

        mapFragment.calculateRoute();
        centerMapOnRoute(checkpoints);
    }

    private void centerMapOnRoute(List<Coordinates> checkpoints) {
        if (checkpoints == null || checkpoints.isEmpty()) return;
        double totalLat = 0, totalLon = 0;
        for (Coordinates c : checkpoints) {
            totalLat += c.getLatitude();
            totalLon += c.getLongitude();
        }
        mapFragment.centerMap(
                new GeoPoint(totalLat / checkpoints.size(), totalLon / checkpoints.size()),
                14.0);
    }

    private void clearRideMarkers() {
        if (mapFragment == null) return;
        if (startMarker != null) { mapFragment.removeWaypoint(startMarker); startMarker = null; }
        if (endMarker != null) { mapFragment.removeWaypoint(endMarker); endMarker = null; }
        if (clientMarker != null) { mapFragment.removeWaypoint(clientMarker); clientMarker = null; }
        mapFragment.clearWaypoints();
        mapFragment.clearRoute();
    }

    // ── Client location tracking ─────────────────────────

    private void startTrackingClientLocation() {
        if (isTrackingLocation) return;

        if (!mapReady) {
            pendingTrackingStart = true;
            return;
        }

        isTrackingLocation = true;

        android.util.Log.d("RideOverview", "Starting location tracking...");

        // Get location immediately
        locationService.getLocation(new LocationService.LocationCallback() {
            @Override
            public void onLocation(Coordinates coordinates) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> onClientLocationUpdate(coordinates));
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("RideOverview", "Initial location error: " + error);
            }
        });

        // Continuous updates every 5 seconds
        locationService.startLocationUpdates(5000, new LocationService.ContinuousLocationCallback() {
            @Override
            public void onLocation(Coordinates coordinates) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> onClientLocationUpdate(coordinates));
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("RideOverview", "Location update error: " + error);
            }
        });

        // Start the duration timer
        startDurationTimer();
    }

    private void onClientLocationUpdate(Coordinates coordinates) {
        lastClientLocation = coordinates;
        updateClientMarker(coordinates);
    }

    private void stopTrackingClientLocation() {
        isTrackingLocation = false;
        locationService.stopLocationUpdates();
        stopDurationTimer();

        if (clientMarker != null && mapFragment != null) {
            mapFragment.removeWaypoint(clientMarker);
            clientMarker = null;
        }

        lastClientLocation = null;
    }

    private void updateClientMarker(Coordinates coordinates) {
        if (mapFragment == null || !mapReady) return;
        if (clientMarker != null) {
            mapFragment.removeWaypoint(clientMarker);
        }
        clientMarker = mapFragment.addClientMarker(toGeoPoint(coordinates));
    }

    // ── Duration timer (every 5 seconds) ─────────────────

    private void startDurationTimer() {
        if (isDurationTimerRunning) return;
        isDurationTimerRunning = true;

        durationRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isAdded() || !isDurationTimerRunning) return;

                calculateDurationViaMap();

                durationHandler.postDelayed(this, DURATION_UPDATE_INTERVAL);
            }
        };

        // Run immediately, then every 5 seconds
        durationHandler.post(durationRunnable);
    }

    private void stopDurationTimer() {
        isDurationTimerRunning = false;
        if (durationHandler != null && durationRunnable != null) {
            durationHandler.removeCallbacks(durationRunnable);
            durationRunnable = null;
        }
    }

    private void calculateDurationViaMap() {
        if (lastClientLocation == null) {
            android.util.Log.w("RideOverview", "Duration skip: no client location yet");
            return;
        }
        if (rideOverview == null || mapFragment == null || !mapReady) {
            android.util.Log.w("RideOverview", "Duration skip: not ready");
            return;
        }

        List<Coordinates> checkpoints = rideOverview.getCheckpoints();
        if (checkpoints == null || checkpoints.isEmpty()) {
            android.util.Log.w("RideOverview", "Duration skip: no checkpoints");
            return;
        }

        Coordinates destination = checkpoints.get(checkpoints.size() - 1);

        android.util.Log.d("RideOverview", "Calculating duration: " +
                lastClientLocation.getLatitude() + "," + lastClientLocation.getLongitude() +
                " → " + destination.getLatitude() + "," + destination.getLongitude());

        mapFragment.calculateDuration(
                toGeoPoint(lastClientLocation),
                toGeoPoint(destination),
                new OSMMapFragment.DurationCallback() {
                    @Override
                    public void onDurationCalculated(int durationMinutes, double distanceKm) {
                        android.util.Log.d("RideOverview",
                                "Duration: " + durationMinutes + " min, " + distanceKm + " km");
                        if (rideInfoFragment != null) {
                            rideInfoFragment.updateDuration(durationMinutes);
                        }
                    }

                    @Override
                    public void onDurationError(String error) {
                        android.util.Log.e("RideOverview", "Duration error: " + error);
                    }
                }
        );
    }

    // ── WebSocket ────────────────────────────────────────

    private void listenToRideUpdates() {
        rideService.listenToRideUpdates(rideId, new RideService.Callback<RideOverviewUpdate>() {
            @Override
            public void onSuccess(RideOverviewUpdate update) {
                if (!isAdded() || rideOverview == null) return;
                requireActivity().runOnUiThread(() -> applyRideUpdate(update));
            }

            @Override
            public void onError(int code, String message) {}
        });
    }

    private void applyRideUpdate(RideOverviewUpdate update) {
        if (rideOverview == null) return;

        if (update.getStatus() != null) rideOverview.setStatus(update.getStatus());
        if (update.getEndAddress() != null) rideOverview.setEndAddress(update.getEndAddress());
        if (update.getPrice() != null) rideOverview.setPrice(update.getPrice());
        if (update.getDepartureTime() != null) rideOverview.setDepartureTime(update.getDepartureTime());
        if (update.getArrivalTime() != null) rideOverview.setArrivalTime(update.getArrivalTime());

        if (update.getDestinationCoordinates() != null) {
            List<Coordinates> checkpoints = rideOverview.getCheckpoints();
            if (checkpoints != null && !checkpoints.isEmpty()) {
                checkpoints.set(checkpoints.size() - 1, update.getDestinationCoordinates());
                drawRideOnMap(rideOverview);
            }
        }

        if (rideInfoFragment != null) rideInfoFragment.updateRideOverview(rideOverview);

        if (rideOverview.getStatus() == RideStatus.ACTIVE) {
            startTrackingClientLocation();
        } else {
            stopTrackingClientLocation();
        }
    }



    // ── Map callbacks ────────────────────────────────────

    @Override
    public void onMapClicked(GeoPoint point) {}

    @Override
    public void onMarkerClicked(Marker marker, GeoPoint point) {
        if (marker.getTitle() != null) {
            Toast.makeText(getContext(), marker.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRouteCalculated(Road road) {}

    // ── Helpers ──────────────────────────────────────────

    private GeoPoint toGeoPoint(Coordinates coords) {
        return new GeoPoint(coords.getLatitude(), coords.getLongitude());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopTrackingClientLocation();
        stopDurationTimer();
        rideService.closeConnection();
    }
}