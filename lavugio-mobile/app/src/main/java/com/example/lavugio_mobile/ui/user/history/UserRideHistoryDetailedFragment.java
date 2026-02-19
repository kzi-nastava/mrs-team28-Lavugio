package com.example.lavugio_mobile.ui.user.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.api.RideApi;
import com.example.lavugio_mobile.models.CanOrderRideResponse;
import com.example.lavugio_mobile.models.Coordinates;
import com.example.lavugio_mobile.models.RideHistoryUserDetailedModel;
import com.example.lavugio_mobile.models.RideRequestDTO;
import com.example.lavugio_mobile.services.UserService;
import com.example.lavugio_mobile.ui.dialog.ErrorDialogFragment;
import com.example.lavugio_mobile.ui.dialog.SuccessDialogFragment;
import com.example.lavugio_mobile.ui.map.OSMMapFragment;
import com.example.lavugio_mobile.ui.ride.FindRideScheduleFragment;

import java.time.LocalDateTime;
import com.google.android.material.button.MaterialButton;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRideHistoryDetailedFragment extends Fragment implements OSMMapFragment.MapInteractionListener {

    private static final String ARG_RIDE_ID = "rideId";
    private long rideId;
    private RideHistoryUserDetailedModel ride;
    private UserService userService;
    private RideApi rideApi;

    private OSMMapFragment mapFragment;

    // Views
    private TextView tripStart;
    private TextView tripEnd;
    private TextView tripDeparture;
    private TextView tripDestination;
    private TextView tripPrice;
    private TextView driverName;
    private TextView driverPhone;
    private TextView vehicleInfo;
    private TextView reviewRatings;
    private TextView reviewComment;
    private TextView noReviewText;
    private LinearLayout existingReviewContainer;
    private LinearLayout reportsList;
    private TextView noReportsText;
    private MaterialButton reorderButton;

    public static UserRideHistoryDetailedFragment newInstance(long rideId) {
        UserRideHistoryDetailedFragment fragment = new UserRideHistoryDetailedFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userService = new UserService();
        rideApi = ApiClient.getRideApi();
        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_ride_history_detailed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        // Load Map Fragment
        mapFragment = new OSMMapFragment();
        mapFragment.setMapInteractionListener(this);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();

        // Fetch ride data
        fetchRideDetails();

        // Set up reorder button
        reorderButton.setOnClickListener(v -> checkAndReorderRide());
    }

    private void initViews(View view) {
        tripStart = view.findViewById(R.id.tripStart);
        tripEnd = view.findViewById(R.id.tripEnd);
        tripDeparture = view.findViewById(R.id.tripDeparture);
        tripDestination = view.findViewById(R.id.tripDestination);
        tripPrice = view.findViewById(R.id.tripPrice);
        driverName = view.findViewById(R.id.driverName);
        driverPhone = view.findViewById(R.id.driverPhone);
        vehicleInfo = view.findViewById(R.id.vehicleInfo);
        reviewRatings = view.findViewById(R.id.reviewRatings);
        reviewComment = view.findViewById(R.id.reviewComment);
        noReviewText = view.findViewById(R.id.no_review_text);
        existingReviewContainer = view.findViewById(R.id.existing_review_container);
        reportsList = view.findViewById(R.id.reports_list);
        noReportsText = view.findViewById(R.id.no_reports_text);
        reorderButton = view.findViewById(R.id.reorderButton);
    }

    private void fetchRideDetails() {
        userService.getUserRideHistoryDetailed(rideId, new UserService.Callback<RideHistoryUserDetailedModel>() {
            @Override
            public void onSuccess(RideHistoryUserDetailedModel result) {
                if (!isAdded()) return;
                ride = result;
                updateUI();
                drawRouteOnMap();
            }

            @Override
            public void onError(int code, String message) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(),
                        "Error loading ride details: " + message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        // Trip Info
        tripStart.setText(ride.getStart());
        tripEnd.setText(ride.getEnd());
        tripDeparture.setText(ride.getDeparture());
        tripDestination.setText(ride.getDestination());
        tripPrice.setText(String.format("%.2f RSD", ride.getPrice()));

        // Driver Info
        if (ride.getDriverName() != null) {
            driverName.setText(ride.getDriverName() + " " +
                    (ride.getDriverLastName() != null ? ride.getDriverLastName() : ""));
            driverPhone.setText("Phone: " + (ride.getDriverPhoneNumber() != null ? ride.getDriverPhoneNumber() : "N/A"));

            String vehicleStr = "";
            if (ride.getVehicleMake() != null && ride.getVehicleModel() != null) {
                vehicleStr = ride.getVehicleMake() + " " + ride.getVehicleModel();
                if (ride.getVehicleColor() != null) {
                    vehicleStr += " (" + ride.getVehicleColor() + ")";
                }
                if (ride.getVehicleLicensePlate() != null) {
                    vehicleStr += " - " + ride.getVehicleLicensePlate();
                }
            }
            vehicleInfo.setText(vehicleStr.isEmpty() ? "N/A" : vehicleStr);
        } else {
            driverName.setText("Driver information not available");
            driverPhone.setText("");
            vehicleInfo.setText("");
        }

        // Review Info
        if (ride.isHasReview()) {
            existingReviewContainer.setVisibility(View.VISIBLE);
            noReviewText.setVisibility(View.GONE);

            String ratings = "Driver Rating: " + (ride.getDriverRating() != null ? ride.getDriverRating() : "N/A") +
                    " | Car Rating: " + (ride.getCarRating() != null ? ride.getCarRating() : "N/A");
            reviewRatings.setText(ratings);
            reviewComment.setText(ride.getReviewComment() != null ? ride.getReviewComment() : "No comment");
        } else {
            existingReviewContainer.setVisibility(View.GONE);
            noReviewText.setVisibility(View.VISIBLE);
        }

        // Reports
        if (ride.getReports() != null && !ride.getReports().isEmpty()) {
            noReportsText.setVisibility(View.GONE);
            reportsList.removeAllViews();

            for (RideHistoryUserDetailedModel.ReportInfo report : ride.getReports()) {
                TextView reportView = new TextView(requireContext());
                reportView.setText("• " + report.getReportMessage() +
                        " (by " + report.getReporterName() + ")");
                reportView.setTextColor(getResources().getColor(R.color.black, null));
                reportView.setPadding(0, 0, 0, 8);
                reportsList.addView(reportView);
            }
        } else {
            noReportsText.setVisibility(View.VISIBLE);
        }
    }

    private void drawRouteOnMap() {
        if (mapFragment != null && ride.getCheckpoints() != null && !ride.getCheckpoints().isEmpty()) {
            mapFragment.clearWaypoints();

            List<GeoPoint> routePoints = ride.getCheckpoints().stream()
                    .map(c -> new GeoPoint(c.getLatitude(), c.getLongitude()))
                    .collect(Collectors.toList());

            for (GeoPoint p : routePoints) {
                mapFragment.addWaypoint(p);
            }

            mapFragment.calculateRoute();

            if (!routePoints.isEmpty()) {
                mapFragment.centerMap(routePoints.get(0), 14.0);
            }
        }
    }

    private void checkAndReorderRide() {
        userService.canUserOrderRide(new UserService.Callback<CanOrderRideResponse>() {
            @Override
            public void onSuccess(CanOrderRideResponse response) {
                if (response.getBlock().isBlocked()) {
                    showError("Cannot Order Ride", "You are blocked: " + response.getBlock().getReason());
                } else if (response.isInRide()) {
                    showError("Cannot Order Ride", "You are already in an active ride and cannot order a new one.");
                } else {
                    requireActivity().runOnUiThread(() -> openScheduleDialog());
                }
            }

            @Override
            public void onError(int code, String message) {
                showError("Error", "Unable to check ride eligibility: " + message);
            }
        });
    }

    private void openScheduleDialog() {
        FindRideScheduleFragment dialog = new FindRideScheduleFragment();
        dialog.setOnRideScheduledListener((rideType, selectedTime) -> {
            if ("ride_now".equals(rideType)) {
                reorderRide(false, null);
            } else {
                reorderRide(true, parseScheduledTime(selectedTime));
            }
        });
        dialog.show(getParentFragmentManager(), "scheduleReorder");
    }

    private LocalDateTime parseScheduledTime(String selectedTime) {
        try {
            // selectedTime format from FindRideScheduleFragment: "HH:MM AM" or "HH:MM PM"
            String[] parts = selectedTime.trim().split(" ");
            String[] timeParts = parts[0].split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            String amPm = parts[1];

            if ("PM".equals(amPm) && hour != 12) hour += 12;
            if ("AM".equals(amPm) && hour == 12) hour = 0;

            LocalDateTime result = LocalDateTime.now()
                    .withHour(hour)
                    .withMinute(minute)
                    .withSecond(0)
                    .withNano(0);

            // If time has already passed today (e.g., near midnight edge case), move to tomorrow
            if (result.isBefore(LocalDateTime.now())) {
                result = result.plusDays(1);
            }
            return result;
        } catch (Exception e) {
            return LocalDateTime.now().plusMinutes(15);
        }
    }

    private void reorderRide(boolean scheduled, LocalDateTime scheduledTime) {
        if (ride == null || ride.getDestinations() == null) {
            Toast.makeText(requireContext(), "Cannot reorder ride - missing destination data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Build ride request from history data
        RideRequestDTO rideRequest = new RideRequestDTO();

        // Convert destinations
        List<com.example.lavugio_mobile.models.ride.RideDestinationDTO> destinations = new ArrayList<>();
        for (RideHistoryUserDetailedModel.DestinationDetail d : ride.getDestinations()) {
            com.example.lavugio_mobile.models.ride.RideDestinationDTO dest = new com.example.lavugio_mobile.models.ride.RideDestinationDTO();

            com.example.lavugio_mobile.models.ride.StopBaseDTO location = new com.example.lavugio_mobile.models.ride.StopBaseDTO();
            location.setOrderIndex(d.getOrderIndex());
            location.setLatitude(d.getLatitude());
            location.setLongitude(d.getLongitude());
            dest.setLocation(location);

            dest.setAddress(d.getAddress());
            dest.setStreetName(d.getStreetName());
            dest.setCity(d.getCity());
            dest.setCountry(d.getCountry());
            if (d.getStreetNumber() != null && !d.getStreetNumber().isEmpty()) {
                dest.setStreetNumber(d.getStreetNumber());
            } else {
                dest.setStreetNumber("0");
            }
            dest.setZipCode(d.getZipCode());

            destinations.add(dest);
        }
        rideRequest.setDestinations(destinations);

        rideRequest.setPassengerEmails(new ArrayList<>()); // Current user only
        rideRequest.setVehicleType(com.example.lavugio_mobile.data.model.vehicle.VehicleType.STANDARD);
        rideRequest.setBabyFriendly(false);
        rideRequest.setPetFriendly(false);
        rideRequest.setScheduled(scheduled);
        rideRequest.setScheduledTime(scheduledTime);
        rideRequest.setEstimatedDurationSeconds(0);
        rideRequest.setDistance(0);
        rideRequest.setPrice((int) ride.getPrice());

        // Make API call
        rideApi.findRide(rideRequest).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    showSuccess("Ride Ordered", "Your ride has been successfully ordered.");
                    // Navigate back to home after delay
                    new android.os.Handler().postDelayed(() -> {
                        if (isAdded()) {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    }, 2000);
                } else {
                    String errorMsg = "Unable to order ride. Please try again.";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    showError("Error", errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                showError("Error", "Network error: " + t.getMessage());
            }
        });
    }

    private void showError(String title, String message) {
        ErrorDialogFragment dialog = ErrorDialogFragment.newInstance(title, message);
        dialog.show(getChildFragmentManager(), "error");
    }

    private void showSuccess(String title, String message) {
        SuccessDialogFragment dialog = SuccessDialogFragment.newInstance(title, message);
        dialog.show(getChildFragmentManager(), "success");
    }

    // OSMMapFragment.MapInteractionListener methods
    @Override
    public void onMapClicked(GeoPoint point) {
        // Not needed for history view
    }

    @Override
    public void onMarkerClicked(org.osmdroid.views.overlay.Marker marker, GeoPoint point) {
        // Not needed for history view
    }

    @Override
    public void onRouteCalculated(org.osmdroid.bonuspack.routing.Road road) {
        // Route calculated successfully
    }
}
