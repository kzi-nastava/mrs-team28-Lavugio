package com.example.lavugio_mobile.ui.ride;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.example.lavugio_mobile.api.LocalDateTimeAdapter;
import com.example.lavugio_mobile.models.Coordinates;
import com.example.lavugio_mobile.models.RideOverviewModel;
import com.example.lavugio_mobile.models.RideOverviewUpdate;
import com.example.lavugio_mobile.models.enums.DriverStatusEnum;
import com.example.lavugio_mobile.models.enums.RideStatus;
import com.example.lavugio_mobile.services.LocationService;
import com.example.lavugio_mobile.services.RideService;
import com.example.lavugio_mobile.services.WebSocketService;
import com.example.lavugio_mobile.ui.map.OSMMapFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.time.LocalDateTime;
import java.util.List;

public class RideOverviewFragment extends Fragment implements OSMMapFragment.MapInteractionListener {

    private static final String TAG = "RideOverview";
    private static final String ARG_RIDE_ID = "rideId";
    private static final long DURATION_UPDATE_INTERVAL = 5000;

    private long rideId;
    private RideOverviewModel rideOverview;
    private boolean isReported  = false;
    private boolean isReviewed  = false;
    private boolean isInfoOpen  = false;

    private RideService rideService;
    private LocationService locationService;
    private WebSocketService webSocketService;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    private FrameLayout mapContainer;
    private LinearLayout bottomSheet;
    private LinearLayout dragHandleContainer;
    private FrameLayout rideInfoContainer;

    private OSMMapFragment mapFragment;
    private RideInfoFragment rideInfoFragment;

    private Marker startMarker;
    private Marker endMarker;
    private Marker clientMarker;

    private boolean isTrackingLocation  = false;
    private boolean mapReady            = false;
    private boolean pendingTrackingStart = false;

    private Coordinates lastClientLocation;

    private Handler durationHandler;
    private Runnable durationRunnable;
    private boolean isDurationTimerRunning = false;

    // WebSocket subscriptions
    private WebSocketService.StompSubscription rideUpdateSub = null;
    private WebSocketService.StompSubscription rideCancelSub = null;

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
        rideService      = LavugioApp.getRideService();
        locationService  = LavugioApp.getLocationService();
        webSocketService = LavugioApp.getWebSocketService();
        durationHandler  = new Handler(Looper.getMainLooper());
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

        mapContainer       = view.findViewById(R.id.map_container);
        bottomSheet        = view.findViewById(R.id.bottom_sheet);
        dragHandleContainer = view.findViewById(R.id.drag_handle_container);
        rideInfoContainer  = view.findViewById(R.id.ride_info_container);

        dragHandleContainer.setOnClickListener(v -> toggleInfo());

        mapFragment = new OSMMapFragment();
        mapFragment.setMapInteractionListener(this);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();

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
                            if (rideOverview != null) drawRideOnMap(rideOverview);
                            if (pendingTrackingStart) {
                                pendingTrackingStart = false;
                                startTrackingClientLocation();
                            }
                        }
                    }
                }, false);

        rideInfoFragment = RideInfoFragment.newInstance(rideId);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.ride_info_container, rideInfoFragment)
                .commit();

        fetchRideOverview();
        listenToRideUpdates();
        listenToRideCancel();   // ← novo
    }

    // ── Bottom sheet ──────────────────────────────────────────────────────

    private void toggleInfo() {
        isInfoOpen = !isInfoOpen;
        rideInfoContainer.setVisibility(isInfoOpen ? View.VISIBLE : View.GONE);
    }

    // ── Fetch ──────────────────────────────────────────────────────────────

    private void fetchRideOverview() {
        rideService.getRideOverview(rideId, new RideService.Callback<RideOverviewModel>() {
            @Override
            public void onSuccess(RideOverviewModel result) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    rideOverview = result;
                    isReported   = result.isReported();
                    isReviewed   = result.isReviewed();

                    if (rideInfoFragment != null) rideInfoFragment.updateRideOverview(result);
                    if (mapReady) drawRideOnMap(result);

                    if (result.getStatus() == RideStatus.ACTIVE) {
                        if (mapReady) startTrackingClientLocation();
                        else pendingTrackingStart = true;
                    }
                });
            }

            @Override
            public void onError(int code, String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (code == 404 || code == 403) {
                        Toast.makeText(getContext(), "This ride is not available.", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Error loading ride: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // ── Map ────────────────────────────────────────────────────────────────

    private void drawRideOnMap(RideOverviewModel overview) {
        if (mapFragment == null || overview == null || !mapReady) return;

        List<Coordinates> checkpoints = overview.getCheckpoints();
        if (checkpoints == null || checkpoints.isEmpty()) return;

        mapFragment.createRoute(checkpoints);

        if (startMarker != null) mapFragment.removeWaypoint(startMarker);
        if (endMarker   != null) mapFragment.removeWaypoint(endMarker);

        if (overview.getStatus() == RideStatus.ACTIVE && lastClientLocation != null) {
            updateClientMarker(lastClientLocation);
        }
    }

    // ── Location tracking ──────────────────────────────────────────────────

    private void startTrackingClientLocation() {
        if (isTrackingLocation) return;
        if (!mapReady) { pendingTrackingStart = true; return; }

        isTrackingLocation = true;
        Log.d(TAG, "Starting location tracking...");

        locationService.getLocation(new LocationService.LocationCallback() {
            @Override public void onLocation(Coordinates coordinates) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> onClientLocationUpdate(coordinates));
            }
            @Override public void onError(String error) {
                Log.e(TAG, "Initial location error: " + error);
            }
        });

        locationService.startLocationUpdates(5000, new LocationService.ContinuousLocationCallback() {
            @Override public void onLocation(Coordinates coordinates) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> onClientLocationUpdate(coordinates));
            }
            @Override public void onError(String error) {
                Log.e(TAG, "Location update error: " + error);
            }
        });

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
        if (rideOverview == null || rideOverview.getStatus() != RideStatus.ACTIVE) {
            if (clientMarker != null) {
                mapFragment.removeWaypoint(clientMarker);
                clientMarker = null;
            }
            return;
        }
        if (clientMarker != null) mapFragment.removeWaypoint(clientMarker);
        clientMarker = mapFragment.addClientMarker(toGeoPoint(coordinates));
    }

    // ── Duration timer ─────────────────────────────────────────────────────

    private void startDurationTimer() {
        if (isDurationTimerRunning) return;
        isDurationTimerRunning = true;

        durationRunnable = new Runnable() {
            @Override public void run() {
                if (!isAdded() || !isDurationTimerRunning) return;
                calculateDurationViaMap();
                durationHandler.postDelayed(this, DURATION_UPDATE_INTERVAL);
            }
        };
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
        if (lastClientLocation == null || rideOverview == null ||
                mapFragment == null || !mapReady) return;

        List<Coordinates> checkpoints = rideOverview.getCheckpoints();
        if (checkpoints == null || checkpoints.isEmpty()) return;

        Coordinates destination = checkpoints.get(checkpoints.size() - 1);

        mapFragment.calculateDuration(
                toGeoPoint(lastClientLocation),
                toGeoPoint(destination),
                new OSMMapFragment.DurationCallback() {
                    @Override public void onDurationCalculated(int durationMinutes, double distanceKm) {
                        if (rideInfoFragment != null) rideInfoFragment.updateDuration(durationMinutes);
                    }
                    @Override public void onDurationError(String error) {
                        Log.e(TAG, "Duration error: " + error);
                    }
                }
        );
    }

    // ── WebSocket ──────────────────────────────────────────────────────────

    private void listenToRideUpdates() {
        rideService.listenToRideUpdates(rideId, new RideService.Callback<RideOverviewUpdate>() {
            @Override
            public void onSuccess(RideOverviewUpdate update) {
                if (!isAdded() || rideOverview == null) return;
                requireActivity().runOnUiThread(() -> applyRideUpdate(update));
            }
            @Override public void onError(int code, String message) {}
        });
    }

    private void listenToRideCancel() {
        webSocketService.connect(() -> {
            rideCancelSub = webSocketService.subscribeJson(
                    "/socket-publisher/rides/" + rideId + "/cancel",
                    RideOverviewUpdate.class,
                    update -> {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            Log.d(TAG, "Ride cancelled via WebSocket: " + rideId);
                            if (rideOverview != null) {
                                rideOverview.setStatus(RideStatus.CANCELLED);
                                if (rideInfoFragment != null) rideInfoFragment.updateRideOverview(rideOverview);
                            }
                            stopTrackingClientLocation();
                            Toast.makeText(getContext(), "This ride has been cancelled.", Toast.LENGTH_LONG).show();
                        });
                    }
            );
        });
    }

    private void applyRideUpdate(RideOverviewUpdate update) {
        if (rideOverview == null) return;

        if (update.getStatus()        != null) rideOverview.setStatus(update.getStatus());
        if (update.getEndAddress()    != null) rideOverview.setEndAddress(update.getEndAddress());
        if (update.getPrice()         != null) rideOverview.setPrice(update.getPrice());
        if (update.getDepartureTime() != null) rideOverview.setDepartureTime(update.getDepartureTime());
        if (update.getArrivalTime()   != null) rideOverview.setArrivalTime(update.getArrivalTime());

        if (update.getDestinationCoordinates() != null) {
            List<Coordinates> checkpoints = rideOverview.getCheckpoints();
            if (checkpoints != null && !checkpoints.isEmpty()) {
                checkpoints.set(checkpoints.size() - 1, update.getDestinationCoordinates());
                drawRideOnMap(rideOverview);
            }
        }

        if (rideInfoFragment != null) rideInfoFragment.updateRideOverview(rideOverview);

        if (rideOverview.getStatus() == RideStatus.ACTIVE) startTrackingClientLocation();
        else stopTrackingClientLocation();
    }

    // ── Map callbacks ──────────────────────────────────────────────────────

    @Override public void onMapClicked(GeoPoint point) {}
    @Override public void onRouteCalculated(Road road) {}

    @Override
    public void onMarkerClicked(Marker marker, GeoPoint point) {
        if (marker.getTitle() != null) {
            Toast.makeText(getContext(), marker.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private GeoPoint toGeoPoint(Coordinates coords) {
        return new GeoPoint(coords.getLatitude(), coords.getLongitude());
    }

    // ── Lifecycle ──────────────────────────────────────────────────────────

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rideUpdateSub != null) rideUpdateSub.unsubscribe();
        if (rideCancelSub != null) rideCancelSub.unsubscribe();
        stopTrackingClientLocation();
        stopDurationTimer();
        rideService.closeConnection();
    }
}