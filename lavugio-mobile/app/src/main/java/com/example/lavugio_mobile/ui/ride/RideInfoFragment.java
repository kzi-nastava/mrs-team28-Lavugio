package com.example.lavugio_mobile.ui.ride;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.lavugio_mobile.models.RideOverviewModel;
import com.example.lavugio_mobile.models.enums.RideStatus;
import com.example.lavugio_mobile.services.RideService;
import com.example.lavugio_mobile.ui.dialog.ReportDialogFragment;
import com.example.lavugio_mobile.ui.dialog.ReviewDialogFragment;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RideInfoFragment extends Fragment {

    private static final String ARG_RIDE_ID = "rideId";

    private long rideId;
    private RideOverviewModel rideOverview;
    private boolean isReported = false;
    private boolean isReviewed = false;
    private boolean hasPanicBeenTriggered = false;
    private boolean isPanicLoading = false;

    private RideService rideService;
    private int lastCalculatedDuration = -1;

    private Handler elapsedHandler;
    private Runnable elapsedRunnable;

    private Button btnReport;
    private TextView tvDuration;
    private TextView tvElapsed;
    private FrameLayout statusContainer;
    private FrameLayout actionButtonContainer;
    private View rowPrice, rowDriver, rowPickup, rowDropoff, rowDeparture, rowArrival;

    public static RideInfoFragment newInstance(long rideId) {
        RideInfoFragment fragment = new RideInfoFragment();
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
        elapsedHandler = new Handler(Looper.getMainLooper());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ride_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnReport = view.findViewById(R.id.btn_report);
        tvDuration = view.findViewById(R.id.tv_duration);
        tvElapsed = view.findViewById(R.id.tv_elapsed);
        statusContainer = view.findViewById(R.id.status_container);
        actionButtonContainer = view.findViewById(R.id.action_button_container);

        rowPrice = view.findViewById(R.id.row_price);
        rowDriver = view.findViewById(R.id.row_driver);
        rowPickup = view.findViewById(R.id.row_pickup);
        rowDropoff = view.findViewById(R.id.row_dropoff);
        rowDeparture = view.findViewById(R.id.row_departure);
        rowArrival = view.findViewById(R.id.row_arrival);

        btnReport.setOnClickListener(v -> showReportDialog());
        startElapsedTimer();
    }

    // ── Public update methods ────────────────────────────

    public void updateRideOverview(RideOverviewModel overview) {
        this.rideOverview = overview;
        this.isReported = overview.isReported();
        this.isReviewed = overview.isReviewed();
        this.hasPanicBeenTriggered = overview.isHasPanic();

        if (!isAdded() || getView() == null) return;
        refreshUI();
    }

    public void updateDuration(int minutes) {
        lastCalculatedDuration = minutes;
        if (!isAdded() || tvDuration == null) return;
        tvDuration.setText(minutes + " min");
    }

    // ── UI refresh ───────────────────────────────────────

    private void refreshUI() {
        if (rideOverview == null) return;

        if (isReported) {
            btnReport.setText("Reported");
            btnReport.setEnabled(false);
        } else {
            btnReport.setText("Report");
            btnReport.setEnabled(true);
        }

        setRowData(rowPrice, "Price:",
                rideOverview.getPrice() > 0 ? rideOverview.getPrice() + " RSD" : "Loading...");
        setRowData(rowDriver, "Driver:",
                rideOverview.getDriverName() != null ? rideOverview.getDriverName() : "Loading...");
        setRowData(rowPickup, "Pickup:",
                rideOverview.getStartAddress() != null ? rideOverview.getStartAddress() : "Loading...");
        setRowData(rowDropoff, "Drop-off:",
                rideOverview.getEndAddress() != null ? rideOverview.getEndAddress() : "Loading...");
        setRowData(rowDeparture, "Departure:",
                formatDateTime(rideOverview.getDepartureTime()));
        setRowData(rowArrival, "Arrival:",
                rideOverview.getStatus() == RideStatus.FINISHED
                        ? formatDateTime(rideOverview.getArrivalTime())
                        : "Not finished");

        RideStatus status = rideOverview.getStatus();
        if (status == RideStatus.ACTIVE) {
            if (lastCalculatedDuration >= 0) {
                tvDuration.setText(lastCalculatedDuration + " min");
            } else {
                tvDuration.setText("Calculating...");
            }
        } else {
            tvDuration.setText(status != null ? status.name() : "Loading...");
        }

        refreshElapsed();
        refreshStatusBadge();
        refreshActionButton();
    }

    // ── Elapsed timer ────────────────────────────────────

    private void startElapsedTimer() {
        elapsedRunnable = new Runnable() {
            @Override
            public void run() {
                refreshElapsed();
                elapsedHandler.postDelayed(this, 60000);
            }
        };
        elapsedHandler.post(elapsedRunnable);
    }

    private void refreshElapsed() {
        if (!isAdded() || tvElapsed == null) return;

        if (rideOverview == null || rideOverview.getDepartureTime() == null) {
            tvElapsed.setText("--");
            return;
        }

        RideStatus status = rideOverview.getStatus();

        if (status != RideStatus.ACTIVE && status != RideStatus.FINISHED) {
            tvElapsed.setText("--");
            return;
        }

        LocalDateTime departure = rideOverview.getDepartureTime();
        LocalDateTime end;

        if (status == RideStatus.FINISHED && rideOverview.getArrivalTime() != null) {
            end = rideOverview.getArrivalTime();
        } else {
            end = LocalDateTime.now();
        }

        long minutes = Duration.between(departure, end).toMinutes();
        tvElapsed.setText(Math.max(minutes, 0) + " min");
    }

    // ── Status badge ─────────────────────────────────────

    private void refreshStatusBadge() {
        statusContainer.removeAllViews();
        if (rideOverview == null || rideOverview.getStatus() == null) return;

        TextView badge = new TextView(requireContext());
        badge.setTextSize(13);
        badge.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        badge.setPadding(24, 12, 24, 12);
        badge.setBackground(requireContext().getDrawable(R.drawable.status_badge_bg));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        badge.setLayoutParams(params);

        switch (rideOverview.getStatus()) {
            case SCHEDULED:
                badge.setText("● Ride Scheduled");
                badge.setTextColor(Color.parseColor("#1D4ED8"));
                badge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DBEAFE")));
                break;
            case ACTIVE:
                badge.setText("● Ride in Progress");
                badge.setTextColor(Color.parseColor("#15803D"));
                badge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DCFCE7")));
                break;
            case CANCELLED:
                badge.setText("● Ride Cancelled");
                badge.setTextColor(Color.parseColor("#B91C1C"));
                badge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FEE2E2")));
                break;
            case FINISHED:
                badge.setText("✓ Ride Completed");
                badge.setTextColor(Color.parseColor("#374151"));
                badge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F3F4F6")));
                break;
            case DENIED:
                badge.setText("✕ Ride Rejected");
                badge.setTextColor(Color.parseColor("#C2410C"));
                badge.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFEDD5")));
                break;
        }

        statusContainer.addView(badge);
    }

    // ── Action button ────────────────────────────────────

    private void refreshActionButton() {
        actionButtonContainer.removeAllViews();
        if (rideOverview == null || rideOverview.getStatus() == null) return;

        Button btn = new Button(requireContext());
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(14);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 120);
        btn.setLayoutParams(params);

        switch (rideOverview.getStatus()) {
            case SCHEDULED:
                btn.setText("Cancel");
                btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BC6C25")));
                btn.setOnClickListener(v -> cancelRide());
                actionButtonContainer.addView(btn);
                break;

            case ACTIVE:
                btn.setText(isPanicLoading ? "SENDING ALERT..." :
                        hasPanicBeenTriggered ? "✓ ALERT SENT" : "\uD83D\uDEA8 PANIC");
                btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DC2626")));
                btn.setEnabled(!isPanicLoading && !hasPanicBeenTriggered);
                btn.setOnClickListener(v -> onPanicClick());
                actionButtonContainer.addView(btn);
                break;

            case FINISHED:
                if (!isReviewed && canRateRide()) {
                    btn.setText("Rate Ride");
                    btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#606C38")));
                    btn.setOnClickListener(v -> showReviewDialog());
                    actionButtonContainer.addView(btn);
                } else if (isReviewed) {
                    btn.setText("Rated");
                    btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#606C38")));
                    btn.setEnabled(false);
                    actionButtonContainer.addView(btn);
                }
                break;

            default:
                break;
        }
    }

    // ── Actions ──────────────────────────────────────────

    private void cancelRide() {
        if (rideOverview == null || rideOverview.getDepartureTime() == null) {
            Toast.makeText(getContext(), "Unable to cancel: missing ride info", Toast.LENGTH_SHORT).show();
            return;
        }

        long minutesUntil = Duration.between(
                LocalDateTime.now(), rideOverview.getDepartureTime()).toMinutes();

        if (minutesUntil < 10) {
            Toast.makeText(getContext(),
                    "Cannot cancel less than 10 minutes before start", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Ride")
                .setMessage("Are you sure you want to cancel this ride? This action cannot be undone.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> executeCancelRide())
                .setNegativeButton("No", null)
                .show();
    }

    private void executeCancelRide() {
        rideService.cancelRideByPassenger(rideId, new RideService.Callback<Void>() {
            @Override
            public void onSuccess(Void result) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Ride cancelled successfully", Toast.LENGTH_SHORT).show();
                    if (rideOverview != null) {
                        rideOverview.setStatus(RideStatus.CANCELLED);
                        refreshUI();
                    }
                });
            }

            @Override
            public void onError(int code, String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Failed to cancel: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void onPanicClick() {
        if (hasPanicBeenTriggered) {
            Toast.makeText(getContext(), "Panic alert already sent", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Panic Confirmation")
                .setMessage("Are you sure you want to send a panic alert?")
                .setPositiveButton("Send Alert", (dialog, which) -> triggerPanic())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void triggerPanic() {
        isPanicLoading = true;
        refreshActionButton();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!isAdded()) return;
            isPanicLoading = false;
            hasPanicBeenTriggered = true;
            refreshActionButton();
            Toast.makeText(getContext(), "PANIC ALERT SENT!", Toast.LENGTH_LONG).show();
        }, 1000);
    }

    private boolean canRateRide() {
        if (rideOverview == null) return false;
        if (rideOverview.getStatus() != RideStatus.FINISHED) return false;
        if (rideOverview.getArrivalTime() == null) return false;
        if (isReviewed) return false;

        long hours = Duration.between(
                rideOverview.getArrivalTime(), LocalDateTime.now()).toHours();
        return hours <= 72; // 3 days = 72 hours
    }

    // ── Dialogs ──────────────────────────────────────────

    private void showReportDialog() {
        ReportDialogFragment dialog = ReportDialogFragment.newInstance(rideId);
        dialog.setOnReportSuccessListener(() -> { isReported = true; refreshUI(); });
        dialog.show(getChildFragmentManager(), "report");
    }

    private void showReviewDialog() {
        ReviewDialogFragment dialog = ReviewDialogFragment.newInstance(rideId);
        dialog.setOnReviewSuccessListener(() -> { isReviewed = true; refreshUI(); });
        dialog.show(getChildFragmentManager(), "review");
    }

    // ── Helpers ──────────────────────────────────────────

    private void setRowData(View row, String label, String value) {
        if (row == null) return;
        TextView labelView = row.findViewById(R.id.detail_label);
        TextView valueView = row.findViewById(R.id.detail_value);
        if (labelView != null) labelView.setText(label);
        if (valueView != null) valueView.setText(value);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "Loading...";
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (elapsedHandler != null && elapsedRunnable != null) {
            elapsedHandler.removeCallbacks(elapsedRunnable);
        }
    }
}