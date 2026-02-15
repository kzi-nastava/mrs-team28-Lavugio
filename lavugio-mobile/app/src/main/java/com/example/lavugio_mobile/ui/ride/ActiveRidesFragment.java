package com.example.lavugio_mobile.ui.ride;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.LavugioApp;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.ActiveRide;
import com.example.lavugio_mobile.services.RideService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ActiveRidesFragment extends Fragment {

    private RideService rideService;

    private LinearLayout loadingContainer;
    private LinearLayout errorContainer;
    private TextView tvError;
    private LinearLayout emptyContainer;
    private Button btnFindTrip;
    private LinearLayout ridesListContainer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rideService = LavugioApp.getRideService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_active_rides, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadingContainer = view.findViewById(R.id.loading_container);
        errorContainer = view.findViewById(R.id.error_container);
        tvError = view.findViewById(R.id.tv_error);
        emptyContainer = view.findViewById(R.id.empty_container);
        btnFindTrip = view.findViewById(R.id.btn_find_trip);
        ridesListContainer = view.findViewById(R.id.rides_list_container);

        btnFindTrip.setOnClickListener(v -> navigateToFindTrip());

        loadActiveRides();
    }

    // ── Data loading ─────────────────────────────────────

    private void loadActiveRides() {
        showLoading();

        rideService.getUserActiveRides(new RideService.Callback<List<ActiveRide>>() {
            @Override
            public void onSuccess(List<ActiveRide> rides) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    if (rides == null || rides.isEmpty()) {
                        showEmpty();
                    } else {
                        showRides(rides);
                    }
                });
            }

            @Override
            public void onError(int code, String message) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    showError("Failed to load active rides. Please try again later.");
                });
            }
        });
    }

    // ── UI state management ──────────────────────────────

    private void showLoading() {
        loadingContainer.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
        emptyContainer.setVisibility(View.GONE);
        ridesListContainer.setVisibility(View.GONE);
    }

    private void showError(String message) {
        loadingContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
        tvError.setText(message);
        emptyContainer.setVisibility(View.GONE);
        ridesListContainer.setVisibility(View.GONE);
    }

    private void showEmpty() {
        loadingContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        emptyContainer.setVisibility(View.VISIBLE);
        ridesListContainer.setVisibility(View.GONE);
    }

    private void showRides(List<ActiveRide> rides) {
        loadingContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.GONE);
        emptyContainer.setVisibility(View.GONE);
        ridesListContainer.setVisibility(View.VISIBLE);

        ridesListContainer.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(requireContext());

        for (ActiveRide ride : rides) {
            View card = inflater.inflate(R.layout.item_active_ride, ridesListContainer, false);
            bindRideCard(card, ride);
            ridesListContainer.addView(card);
        }
    }

    // ── Bind ride data to card ───────────────────────────

    private void bindRideCard(View card, ActiveRide ride) {
        TextView tvRideId = card.findViewById(R.id.tv_ride_id);
        TextView tvStatus = card.findViewById(R.id.tv_ride_status);
        Button btnViewDetails = card.findViewById(R.id.btn_view_details);
        TextView tvPickup = card.findViewById(R.id.tv_pickup);
        TextView tvDestination = card.findViewById(R.id.tv_destination);
        TextView tvStartTime = card.findViewById(R.id.tv_start_time);
        TextView tvPrice = card.findViewById(R.id.tv_price);
        FrameLayout statusBannerContainer = card.findViewById(R.id.status_banner_container);

        // Header
        tvRideId.setText("Ride #" + ride.getId());

        // Status badge
        String status = ride.getRideStatus() != null ? ride.getRideStatus() : "UNKNOWN";
        tvStatus.setText(status);
        applyStatusBadgeStyle(tvStatus, status);

        // Details
        tvPickup.setText(ride.getStartLocation() != null ? ride.getStartLocation() : "N/A");
        tvDestination.setText(ride.getEndLocation() != null ? ride.getEndLocation() : "N/A");
        tvStartTime.setText(formatDateTime(ride.getStartDateTime()));
        tvPrice.setText(ride.getPrice() > 0 ? ride.getPrice() + " РСД" : "N/A");

        // View details button
        btnViewDetails.setOnClickListener(v -> viewRideDetails(ride.getId()));

        // Status banner
        statusBannerContainer.removeAllViews();
        addStatusBanner(statusBannerContainer, ride);
    }

    // ── Status badge styling ─────────────────────────────

    private void applyStatusBadgeStyle(TextView tvStatus, String status) {
        switch (status.toUpperCase()) {
            case "ACTIVE":
                tvStatus.setBackgroundResource(R.drawable.bg_status_badge_green);
                tvStatus.setTextColor(Color.parseColor("#166534"));
                break;
            case "SCHEDULED":
                tvStatus.setBackgroundResource(R.drawable.bg_status_badge_blue);
                tvStatus.setTextColor(Color.parseColor("#1E40AF"));
                break;
            case "STOPPED":
                tvStatus.setBackgroundResource(R.drawable.bg_status_badge_red);
                tvStatus.setTextColor(Color.parseColor("#991B1B"));
                break;
            default:
                tvStatus.setBackgroundResource(R.drawable.bg_status_badge_gray);
                tvStatus.setTextColor(Color.parseColor("#374151"));
                break;
        }
    }

    // ── Status banner (bottom of card) ───────────────────

    private void addStatusBanner(FrameLayout container, ActiveRide ride) {
        String status = ride.getRideStatus() != null ? ride.getRideStatus().toUpperCase() : "";

        if ("ACTIVE".equals(status)) {
            // Green "currently active" banner
            TextView banner = new TextView(requireContext());
            banner.setText("✓ This ride is currently active");
            banner.setTextColor(Color.parseColor("#15803D"));
            banner.setTextSize(13);
            banner.setPadding(24, 12, 24, 12);
            banner.setBackgroundResource(R.drawable.bg_status_active);
            container.addView(banner);

        } else if ("SCHEDULED".equals(status)) {
            // Blue "scheduled" banner with optional cancel button
            LinearLayout row = new LinearLayout(requireContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.setPadding(24, 12, 24, 12);
            row.setBackgroundResource(R.drawable.bg_status_scheduled);

            TextView label = new TextView(requireContext());
            label.setText("\uD83D\uDCC5 This ride is scheduled");
            label.setTextColor(Color.parseColor("#1D4ED8"));
            label.setTextSize(13);
            label.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            row.addView(label);

            if (canCancelRide(ride)) {
                Button btnCancel = new Button(requireContext());
                btnCancel.setText("Cancel Ride");
                btnCancel.setTextColor(Color.WHITE);
                btnCancel.setTextSize(12);
                btnCancel.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#EF4444")));
                btnCancel.setPadding(32, 8, 32, 8);
                btnCancel.setOnClickListener(v -> cancelRide(ride.getId()));

                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                btnCancel.setLayoutParams(btnParams);
                row.addView(btnCancel);
            } else {
                TextView cantCancel = new TextView(requireContext());
                cantCancel.setText("Cannot cancel (less than 10min)");
                cantCancel.setTextColor(Color.parseColor("#DC2626"));
                cantCancel.setTextSize(11);
                row.addView(cantCancel);
            }

            container.addView(row);
        }
    }

    // ── Cancel logic ─────────────────────────────────────

    private boolean canCancelRide(ActiveRide ride) {
        if (ride.getStartDateTime() == null) return false;

        long minutesUntilStart = Duration.between(
                LocalDateTime.now(), ride.getStartDateTime()).toMinutes();

        return minutesUntilStart >= 10;
    }

    private void cancelRide(long rideId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Ride")
                .setMessage("Are you sure you want to cancel this ride? This action cannot be undone.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> executeCancelRide(rideId))
                .setNegativeButton("No", null)
                .show();
    }

    private void executeCancelRide(long rideId) {
        rideService.cancelRideByPassenger(rideId, new RideService.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(),
                            "✅ Ride cancelled successfully", Toast.LENGTH_SHORT).show();
                    loadActiveRides(); // Reload list
                });
            }

            @Override
            public void onError(int code, String message) {
                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(),
                            "❌ Failed to cancel ride: " + message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    // ── Helpers ──────────────────────────────────────────

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return dateTime.format(formatter);
    }

    private void viewRideDetails(long rideId) {
        RideOverviewFragment fragment = RideOverviewFragment.newInstance(rideId);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToFindTrip() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_container, new FindRideFragment())
                .addToBackStack(null)
                .commit();
    }
}