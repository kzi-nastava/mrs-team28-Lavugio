package com.example.lavugio_mobile.ui.driver.scheduled_rides;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavugio_mobile.LavugioApp;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.Coordinates;
import com.example.lavugio_mobile.models.FinishRide;
import com.example.lavugio_mobile.models.ScheduledRideModel;
import com.example.lavugio_mobile.services.DriverService;
import com.example.lavugio_mobile.services.RideService;
import com.example.lavugio_mobile.services.UserService;
import com.example.lavugio_mobile.services.WebSocketService;
import com.example.lavugio_mobile.ui.map.OSMMapFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverScheduledRidesFragment extends Fragment implements
        ScheduledRidesAdapter.OnRideActionListener {

    private static final String TAG = "DriverScheduledRides";
    private static final int LOCATION_PERMISSION_REQUEST = 100;

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private ScheduledRidesAdapter adapter;
    private OSMMapFragment mapFragment;

    private RideService rideService;
    private DriverService driverService;
    private UserService userService;
    private WebSocketService webSocketService;
    private FusedLocationProviderClient fusedLocationClient;

    private List<ScheduledRideModel> rides = new ArrayList<>();
    private boolean hasActiveRide = false;
    private long selectedRideId = -1;

    // WebSocket subscriptions — tracked for cleanup
    private WebSocketService.StompSubscription rideStartSubscription;
    private WebSocketService.StompSubscription rideCancelSubscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rideService = LavugioApp.getRideService();
        driverService = LavugioApp.getDriverService();
        userService = LavugioApp.getUserService();
        webSocketService = LavugioApp.getWebSocketService();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_scheduled_rides, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewScheduledRides);
        emptyTextView = view.findViewById(R.id.textViewEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ScheduledRidesAdapter(rides, this);
        recyclerView.setAdapter(adapter);

        mapFragment = (OSMMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);

        loadRides();
        subscribeToWebSocketUpdates();
    }

    // ── WebSocket subscriptions ──────────────────────────

    private void subscribeToWebSocketUpdates() {
        long driverId = userService.getCurrentUserId();

        webSocketService.connect(() -> {
            // 1) New ride assigned to this driver
            rideStartSubscription = webSocketService.subscribeJson(
                    "/socket-publisher/drivers/" + driverId + "/ride/start",
                    ScheduledRideModel.class,
                    newRide -> {
                        if (!isAdded()) return;
                        Log.d(TAG, "WebSocket: new ride assigned — id=" + newRide.getRideId());
                        requireActivity().runOnUiThread(() -> onNewRideReceived(newRide));
                    }
            );

            // 2) Ride cancelled by passenger
            rideCancelSubscription = webSocketService.subscribe(
                    "/socket-publisher/drivers/" + driverId + "/ride/cancel",
                    body -> {
                        if (!isAdded()) return;
                        try {
                            long cancelledRideId = Long.parseLong(body.trim().replace("\"", ""));
                            Log.d(TAG, "WebSocket: ride cancelled — id=" + cancelledRideId);
                            requireActivity().runOnUiThread(() -> onRideCancelled(cancelledRideId));
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Failed to parse cancelled ride id: " + body, e);
                        }
                    }
            );

            Log.d(TAG, "WebSocket subscriptions active for driver " + driverId);
        });
    }

    private void onNewRideReceived(ScheduledRideModel newRide) {
        // Avoid duplicates
        for (ScheduledRideModel existing : rides) {
            if (existing.getRideId() == newRide.getRideId()) {
                Log.d(TAG, "Ride " + newRide.getRideId() + " already in list, skipping");
                return;
            }
        }

        rides.add(newRide);
        sortRidesByStatusAndTime();
        checkIfAnyActiveRides();

        adapter.setHasActiveRide(hasActiveRide);
        adapter.notifyDataSetChanged();
        updateEmptyState();

        Toast.makeText(requireContext(), "New ride assigned!", Toast.LENGTH_SHORT).show();
    }

    private void onRideCancelled(long rideId) {
        boolean removed = false;
        for (int i = 0; i < rides.size(); i++) {
            if (rides.get(i).getRideId() == rideId) {
                rides.remove(i);
                removed = true;
                break;
            }
        }

        if (!removed) {
            Log.d(TAG, "Cancelled ride " + rideId + " was not in list");
            return;
        }

        checkIfAnyActiveRides();
        adapter.setHasActiveRide(hasActiveRide);
        adapter.notifyDataSetChanged();
        updateEmptyState();

        Toast.makeText(requireContext(), "A ride has been cancelled by the passenger.", Toast.LENGTH_SHORT).show();
    }

    private void unsubscribeFromWebSocket() {
        if (rideStartSubscription != null) {
            rideStartSubscription.unsubscribe();
            rideStartSubscription = null;
        }
        if (rideCancelSubscription != null) {
            rideCancelSubscription.unsubscribe();
            rideCancelSubscription = null;
        }
        Log.d(TAG, "WebSocket subscriptions cleared");
    }

    // ── Load rides ───────────────────────────────────────

    private void loadRides() {
        driverService.getScheduledRides(new DriverService.Callback<List<ScheduledRideModel>>() {
            @Override
            public void onSuccess(List<ScheduledRideModel> result) {
                if (result != null) {
                    rides.clear();
                    rides.addAll(result);
                    sortRidesByStatusAndTime();
                    checkIfAnyActiveRides();

                    requireActivity().runOnUiThread(() -> {
                        adapter.setHasActiveRide(hasActiveRide);
                        adapter.notifyDataSetChanged();
                        updateEmptyState();
                    });

                    Log.d(TAG, "Loaded " + rides.size() + " scheduled rides");
                }
            }

            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "Error loading rides: " + message);
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Failed to load rides", Toast.LENGTH_SHORT).show();
                    updateEmptyState();
                });
            }
        });
    }

    private void updateEmptyState() {
        if (rides.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
    }

    private void sortRidesByStatusAndTime() {
        Collections.sort(rides, (a, b) -> {
            if ("ACTIVE".equals(a.getStatus()) && !"ACTIVE".equals(b.getStatus())) return -1;
            if (!"ACTIVE".equals(a.getStatus()) && "ACTIVE".equals(b.getStatus())) return 1;

            if (a.getScheduledTime() != null && b.getScheduledTime() != null) {
                return a.getScheduledTime().compareTo(b.getScheduledTime());
            }
            return 0;
        });
    }

    private void checkIfAnyActiveRides() {
        hasActiveRide = false;
        for (ScheduledRideModel ride : rides) {
            if ("ACTIVE".equals(ride.getStatus())) {
                hasActiveRide = true;
                Log.d(TAG, "Active ride found: " + ride.getRideId());
                break;
            }
        }
    }

    // ── Ride actions (unchanged) ─────────────────────────

    @Override
    public void onStartRide(long rideId) {
        rideService.startRide(rideId, new RideService.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "Ride started successfully: " + rideId);
                updateRideStatus(rideId, "ACTIVE");
                showToast("Ride started successfully");
            }

            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "Error starting ride: " + message);
                showToast("Failed to start ride");
            }
        });
    }

    @Override
    public void onPanicRide(long rideId) {
        ScheduledRideModel currentRide = findRideById(rideId);
        if (currentRide == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Panic Confirmation")
                .setMessage("Are you sure you want to send a panic alert? This will notify the administrators and mark your vehicle as in danger.")
                .setPositiveButton("Yes, Send Alert", (dialog, which) -> executePanicAlert(rideId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void executePanicAlert(long rideId) {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        showToast("Unable to get location");
                        return;
                    }

                    Map<String, Object> panicAlert = createPanicAlert(rideId, location);

                    rideService.triggerPanic(rideId, panicAlert, new RideService.Callback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Log.d(TAG, "Panic alert sent successfully for ride: " + rideId);
                            showToast("Panic alert sent successfully");
                            loadRides();
                        }

                        @Override
                        public void onError(int code, String message) {
                            Log.e(TAG, "Failed to send panic alert: " + message);
                            showToast("Failed to send panic alert: " + message);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Location error: " + e.getMessage());
                    showLocationErrorDialog();
                });
    }

    private Map<String, Object> createPanicAlert(long rideId, Location location) {
        Map<String, Object> panicAlert = new HashMap<>();
        Map<String, Double> locationMap = new HashMap<>();

        locationMap.put("latitude", location.getLatitude());
        locationMap.put("longitude", location.getLongitude());

        panicAlert.put("rideId", rideId);
        panicAlert.put("passengerId", userService.getCurrentUserId());
        panicAlert.put("passengerName", "Passenger(s) on Ride #" + rideId);
        panicAlert.put("driverName", userService.getCurrentUserName());
        panicAlert.put("location", locationMap);
        panicAlert.put("vehicleType", "Driver Vehicle");
        panicAlert.put("vehicleLicensePlate", "Check Driver Profile");
        panicAlert.put("message", "EMERGENCY: Driver triggered panic button during active ride");
        panicAlert.put("timestamp", new java.util.Date().toString());

        return panicAlert;
    }

    @Override
    public void onFinishRide(long rideId) {
        FinishRide finish = new FinishRide();
        finish.setRideId(rideId);
        finish.setFinalDestination(new Coordinates(0.0, 0.0));
        finish.setFinishedEarly(false);
        finish.setDistance(1.0);

        rideService.finishRide(finish, new RideService.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "Ride finished successfully: " + rideId);
                removeRideFromList(rideId);
                showToast("Ride finished successfully");
            }

            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "Error finishing ride: " + message);
                showToast("Failed to finish ride");
            }
        });
    }

    @Override
    public void onFinishEarly(long rideId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("End Ride Early")
                .setMessage("Are you sure you want to end this ride early? The destination will be updated to your current location and the price will be recalculated based on the distance traveled.")
                .setPositiveButton("Yes, End Early", (dialog, which) -> executeFinishEarly(rideId))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void executeFinishEarly(long rideId) {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location == null) {
                        showToast("Unable to get location");
                        return;
                    }

                    ScheduledRideModel currentRide = findRideById(rideId);
                    if (currentRide == null) {
                        showToast("Could not find ride information");
                        return;
                    }

                    FinishRide finishEarlyData = new FinishRide();
                    finishEarlyData.setRideId(rideId);
                    finishEarlyData.setFinalDestination(new Coordinates(
                            location.getLatitude(),
                            location.getLongitude()
                    ));
                    finishEarlyData.setFinishedEarly(true);
                    finishEarlyData.setDistance(currentRide.getDistance() != null ?
                            currentRide.getDistance() : 1.0);

                    rideService.finishRide(finishEarlyData, new RideService.Callback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            Log.d(TAG, "Ride finished early successfully: " + rideId);
                            showToast("Ride ended successfully. The price has been recalculated.");
                            removeRideFromList(rideId);
                            loadRides();
                        }

                        @Override
                        public void onError(int code, String message) {
                            Log.e(TAG, "Failed to finish ride early: " + message);
                            showToast("Failed to end ride early: " + message);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Location error: " + e.getMessage());
                    showLocationErrorDialog();
                });
    }

    @Override
    public void onDenyRide(long rideId) {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        input.setHint("e.g., Passenger not at pickup location, health emergency, etc.");
        input.setMinLines(2);

        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Ride")
                .setMessage("Please provide a reason for canceling this ride:")
                .setView(input)
                .setPositiveButton("Cancel Ride", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    if (reason.isEmpty()) {
                        showToast("Please provide a reason");
                        return;
                    }
                    executeDenyRide(rideId, reason);
                })
                .setNegativeButton("Back", null)
                .show();
    }

    private void executeDenyRide(long rideId, String reason) {
        rideService.cancelRideByDriver(rideId, reason, new RideService.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "Ride canceled successfully: " + rideId);
                showToast("Ride canceled successfully");
                removeRideFromList(rideId);
            }

            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "Failed to cancel ride: " + message);
                showToast("Failed to cancel ride: " + message);
            }
        });
    }

    @Override
    public void onRideClicked(ScheduledRideModel ride) {
        if (ride.getCheckpoints() != null && !ride.getCheckpoints().isEmpty() && mapFragment != null) {
            selectedRideId = ride.getRideId();
            mapFragment.createRoute(ride.getCheckpoints());
        }
    }

    private void updateRideStatus(long rideId, String newStatus) {
        for (int i = 0; i < rides.size(); i++) {
            if (rides.get(i).getRideId() == rideId) {
                ScheduledRideModel ride = rides.get(i);
                ride.setStatus(newStatus);
                rides.set(i, ride);
                break;
            }
        }

        sortRidesByStatusAndTime();
        checkIfAnyActiveRides();

        requireActivity().runOnUiThread(() -> {
            adapter.setHasActiveRide(hasActiveRide);
            adapter.notifyDataSetChanged();
        });
    }

    private void removeRideFromList(long rideId) {
        for (int i = 0; i < rides.size(); i++) {
            if (rides.get(i).getRideId() == rideId) {
                rides.remove(i);
                break;
            }
        }

        if (selectedRideId == rideId) {
            selectedRideId = -1;
            if (mapFragment != null) mapFragment.clearMap();
        }

        checkIfAnyActiveRides();

        requireActivity().runOnUiThread(() -> {
            adapter.setHasActiveRide(hasActiveRide);
            adapter.notifyDataSetChanged();
            updateEmptyState();
        });
    }

    private ScheduledRideModel findRideById(long rideId) {
        for (ScheduledRideModel ride : rides) {
            if (ride.getRideId() == rideId) {
                return ride;
            }
        }
        return null;
    }

    private void showToast(String message) {
        requireActivity().runOnUiThread(() ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        );
    }

    private void showLocationErrorDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Location Required")
                .setMessage("Unable to get your location. This feature requires location data.\n\n" +
                        "To enable location:\n" +
                        "1. Go to Settings\n" +
                        "2. Find this app's permissions\n" +
                        "3. Enable Location permission\n" +
                        "4. Try again")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast("Location permission granted. Please try again.");
            } else {
                showToast("Location permission is required for this feature");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unsubscribeFromWebSocket();

        recyclerView = null;
        emptyTextView = null;
        adapter = null;
        mapFragment = null;
    }
}