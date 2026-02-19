package com.example.lavugio_mobile.ui.admin;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavugio_mobile.LavugioApp;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.Coordinates;
import com.example.lavugio_mobile.models.DriverLocation;
import com.example.lavugio_mobile.models.RideMonitoringModel;
import com.example.lavugio_mobile.models.enums.DriverStatusEnum;
import com.example.lavugio_mobile.services.DriverService;
import com.example.lavugio_mobile.services.RideService;
import com.example.lavugio_mobile.services.WebSocketService;
import com.example.lavugio_mobile.ui.map.OSMMapFragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.lavugio_mobile.api.LocalDateTimeAdapter;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RideMonitoringFragment extends Fragment {

    private static final String TAG = "RideMonitoring";

    private RideService rideService;
    private DriverService driverService;
    private WebSocketService webSocketService;

    private RecyclerView recyclerRides;
    private RideMonitoringAdapter adapter;
    private EditText filterInput;
    private TextView emptyText;
    private OSMMapFragment mapFragment;

    private final List<RideMonitoringModel> allRides = new ArrayList<>();

    private Marker driverMarker = null;
    private Long selectedDriverId = null;

    private WebSocketService.StompSubscription rideStartSub = null;
    private WebSocketService.StompSubscription rideFinishSub = null;
    private WebSocketService.StompSubscription locationSub = null;
    private WebSocketService.StompSubscription panicSub = null;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    // ── Lifecycle ──────────────────────────────────────────────────────────

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rideService      = LavugioApp.getRideService();
        driverService    = LavugioApp.getDriverService();
        webSocketService = LavugioApp.getWebSocketService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ride_monitoring, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerRides = view.findViewById(R.id.recycler_rides);
        filterInput   = view.findViewById(R.id.filter_input);
        emptyText     = view.findViewById(R.id.empty_text);

        // Attach OSMMapFragment into the container
        mapFragment = new OSMMapFragment();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();

        setupRecyclerView();
        setupFilter();
        loadRides();

        webSocketService.connect(() -> {
            subscribeToRideStart();
            subscribeToRideFinish();
            subscribeToPanic();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rideStartSub != null) rideStartSub.unsubscribe();
        if (rideFinishSub != null) rideFinishSub.unsubscribe();
        if (panicSub != null) panicSub.unsubscribe();
        unsubscribeFromLocation();
    }

    // ── Map ────────────────────────────────────────────────────────────────

    private void setRoute(List<Coordinates> checkpoints) {
        if (mapFragment == null || checkpoints == null || checkpoints.size() < 2) return;
        mapFragment.createRoute(checkpoints);
    }

    private void removeRoute() {
        if (mapFragment != null) {
            mapFragment.clearRoute();
            mapFragment.clearWaypoints();
            mapFragment.clearDriverMarkers();
        }
    }

    private void placeOrMoveDriverMarker(Coordinates coords) {
        if (mapFragment == null) return;
        GeoPoint point = new GeoPoint(coords.getLatitude(), coords.getLongitude());

        // Determine status: use PANIC if the selected ride has panic active
        DriverStatusEnum status = DriverStatusEnum.AVAILABLE;
        if (selectedDriverId != null) {
            for (RideMonitoringModel r : allRides) {
                if (r.getDriverId() == selectedDriverId && r.isPanicked()) {
                    status = DriverStatusEnum.PANIC;
                    break;
                }
            }
        }

        mapFragment.clearDriverMarkers();
        driverMarker = mapFragment.addDriverMarker(point, "Driver", status);
    }

    private void removeDriverMarker() {
        if (mapFragment != null) mapFragment.clearDriverMarkers();
        driverMarker = null;
    }

    // ── RecyclerView ───────────────────────────────────────────────────────

    private void setupRecyclerView() {
        adapter = new RideMonitoringAdapter(this::onRideSelected);
        recyclerRides.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerRides.setAdapter(adapter);
    }

    // ── Filter ─────────────────────────────────────────────────────────────

    private void setupFilter() {
        filterInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s.toString());
            }
        });
    }

    private void applyFilter(String text) {
        if (!isAdded()) return;

        List<RideMonitoringModel> filtered;
        if (text == null || text.trim().isEmpty()) {
            filtered = new ArrayList<>(allRides);
        } else {
            String lower = text.toLowerCase();
            filtered = allRides.stream()
                    .filter(r -> r.getDriverName() != null &&
                            r.getDriverName().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
        }

        adapter.setRides(filtered);
        emptyText.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerRides.setVisibility(filtered.isEmpty() ? View.GONE : View.VISIBLE);
    }

    // ── Load rides ─────────────────────────────────────────────────────────

    private void loadRides() {
        rideService.getActiveRides(new RideService.Callback<List<RideMonitoringModel>>() {
            @Override
            public void onSuccess(List<RideMonitoringModel> rides) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    allRides.clear();
                    if (rides != null) allRides.addAll(rides);
                    applyFilter(filterInput.getText().toString());
                });
            }

            @Override
            public void onError(int code, String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    allRides.clear();
                    applyFilter("");
                    Toast.makeText(getContext(), "Failed to load active rides", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // ── WebSocket ──────────────────────────────────────────────────────────

    private void subscribeToRideStart() {
        rideStartSub = webSocketService.subscribeJson(
                "/socket-publisher/ride/start",
                RideMonitoringModel.class,
                newRide -> {
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        allRides.add(newRide);
                        applyFilter(filterInput.getText().toString());
                    });
                }
        );
    }

    private void subscribeToRideFinish() {
        rideFinishSub = webSocketService.subscribe(
                "/socket-publisher/ride/finish",
                body -> {
                    long rideId;
                    try {
                        rideId = new Gson().fromJson(body.trim(), Long.class);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse rideId: " + body, e);
                        return;
                    }

                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        boolean wasSelected = selectedDriverId != null && allRides.stream()
                                .anyMatch(r -> r.getRideId() == rideId &&
                                        r.getDriverId() == selectedDriverId);

                        if (wasSelected) {
                            unsubscribeFromLocation();
                            removeRoute();
                            removeDriverMarker();
                            selectedDriverId = null;
                        }

                        Log.d(TAG, "ride/finish rideId=" + rideId + ", rides before=" + allRides.size());
                        allRides.removeIf(r -> r.getRideId() == rideId);
                        Log.d(TAG, "rides after=" + allRides.size());
                        applyFilter(filterInput.getText().toString());
                    });
                }
        );
    }

    private void subscribeToPanic() {
        panicSub = webSocketService.subscribe(
                "/socket-publisher/admin/panic",
                body -> {
                    if (!isAdded()) return;
                    long panicRideId = -1;
                    try {
                        org.json.JSONObject json = new org.json.JSONObject(body);
                        panicRideId = json.optLong("rideId", -1);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse panic alert: " + body, e);
                        return;
                    }

                    final long finalRideId = panicRideId;
                    requireActivity().runOnUiThread(() -> {
                        for (RideMonitoringModel ride : allRides) {
                            if (ride.getRideId() == finalRideId) {
                                ride.setPanicked(true);
                                break;
                            }
                        }
                        applyFilter(filterInput.getText().toString());

                        // If the panicked ride's driver is currently selected, update marker
                        if (selectedDriverId != null) {
                            for (RideMonitoringModel ride : allRides) {
                                if (ride.getRideId() == finalRideId
                                        && ride.getDriverId() == selectedDriverId
                                        && driverMarker != null) {
                                    GeoPoint pos = driverMarker.getPosition();
                                    mapFragment.clearDriverMarkers();
                                    driverMarker = mapFragment.addDriverMarker(
                                            pos, "Driver", DriverStatusEnum.PANIC);
                                    break;
                                }
                            }
                        }
                    });
                }
        );
    }

    private void subscribeToDriverLocation(long driverId) {
        if (selectedDriverId != null && selectedDriverId == driverId) return;

        unsubscribeFromLocation();
        selectedDriverId = driverId;

        locationSub = webSocketService.subscribeJson(
                "/socket-publisher/location/" + driverId,
                Coordinates.class,
                coords -> {
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> placeOrMoveDriverMarker(coords));
                }
        );
    }

    private void unsubscribeFromLocation() {
        if (locationSub != null) {
            locationSub.unsubscribe();
            locationSub = null;
        }
        removeDriverMarker();
        selectedDriverId = null;
    }

    // ── Ride selected ──────────────────────────────────────────────────────

    private void onRideSelected(RideMonitoringModel ride) {
        if (selectedDriverId != null && selectedDriverId == ride.getDriverId()) return;

        if (ride.getCheckpoints() != null && ride.getCheckpoints().size() > 1) {
            setRoute(ride.getCheckpoints());
        }

        unsubscribeFromLocation();

        driverService.getDriverLocation(ride.getDriverId(),
                new DriverService.Callback<DriverLocation>() {
                    @Override
                    public void onSuccess(DriverLocation result) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            Coordinates coords = new Coordinates();
                            coords.setLatitude(result.getLocation().getLatitude());
                            coords.setLongitude(result.getLocation().getLongitude());
                            placeOrMoveDriverMarker(coords);
                            subscribeToDriverLocation(ride.getDriverId());
                        });
                    }

                    @Override
                    public void onError(int code, String message) {
                        Log.e(TAG, "Error loading driver location: " + message);
                    }
                });
    }
}