package com.example.lavugio_mobile.ui.driver.scheduled_rides;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.ScheduledRideModel;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduledRidesAdapter extends RecyclerView.Adapter<ScheduledRidesAdapter.RideViewHolder> {

    public interface OnRideActionListener {
        void onStartRide(long rideId);
        void onPanicRide(long rideId);
        void onFinishRide(long rideId);
        void onFinishEarly(long rideId);
        void onCancelRide(long rideId);
        void onRideClicked(ScheduledRideModel ride);
    }

    private final List<ScheduledRideModel> rides;
    private final OnRideActionListener listener;
    private boolean hasActiveRide = false;

    public ScheduledRidesAdapter(List<ScheduledRideModel> rides, OnRideActionListener listener) {
        this.rides = rides;
        this.listener = listener;
    }

    public void setHasActiveRide(boolean hasActiveRide) {
        this.hasActiveRide = hasActiveRide;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scheduled_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        ScheduledRideModel ride = rides.get(position);
        boolean canStart = position == 0; // Only first ride can start
        
        holder.bind(ride, canStart, hasActiveRide, listener);
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        private final View rootView;
        private final View activeIndicator;
        private final TextView textViewDepartureTime;
        private final TextView textViewStartAddress;
        private final TextView textViewEndAddress;
        private final TextView textViewPrice;
        private final Button buttonPanic;
        private final TextView textViewAlertSent;
        private final Button buttonFinishEarly;
        private final Button buttonFinish;
        private final Button buttonDeny;
        private final Button buttonStart;
        private final View buttonsContainer;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.rootView);
            activeIndicator = itemView.findViewById(R.id.activeIndicator);
            textViewDepartureTime = itemView.findViewById(R.id.textViewDepartureTime);
            textViewStartAddress = itemView.findViewById(R.id.textViewStartAddress);
            textViewEndAddress = itemView.findViewById(R.id.textViewEndAddress);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            buttonPanic = itemView.findViewById(R.id.buttonPanic);
            textViewAlertSent = itemView.findViewById(R.id.textViewAlertSent);
            buttonFinishEarly = itemView.findViewById(R.id.buttonFinishEarly);
            buttonFinish = itemView.findViewById(R.id.buttonFinish);
            buttonDeny = itemView.findViewById(R.id.buttonDeny);
            buttonStart = itemView.findViewById(R.id.buttonStart);
            buttonsContainer = itemView.findViewById(R.id.buttonsContainer);
        }

        public void bind(ScheduledRideModel ride, boolean canStart, boolean hasActiveRide,
                        OnRideActionListener listener) {
            // Set ride data
            textViewDepartureTime.setText(formatDateTime(ride.getScheduledTime()));
            textViewStartAddress.setText(ride.getStartAddress() != null ? ride.getStartAddress() : "Loading...");
            textViewEndAddress.setText(ride.getEndAddress() != null ? ride.getEndAddress() : "Loading...");
            textViewPrice.setText(ride.getPrice() != null ? ride.getPrice() + " RSD" : "Loading...");

            // Show/hide active indicator
            boolean isActive = "ACTIVE".equals(ride.getStatus());
            activeIndicator.setVisibility(isActive ? View.VISIBLE : View.GONE);

            // Configure buttons based on ride status
            if (isActive) {
                // Active ride buttons
                if (ride.getPanicked() != null && ride.getPanicked()) {
                    buttonPanic.setVisibility(View.GONE);
                    textViewAlertSent.setVisibility(View.VISIBLE);
                } else {
                    buttonPanic.setVisibility(View.VISIBLE);
                    textViewAlertSent.setVisibility(View.GONE);
                }
                buttonFinishEarly.setVisibility(View.VISIBLE);
                buttonFinish.setVisibility(View.VISIBLE);
                buttonDeny.setVisibility(View.GONE);
                buttonStart.setVisibility(View.GONE);
            } else {
                // Scheduled ride buttons
                buttonPanic.setVisibility(View.GONE);
                textViewAlertSent.setVisibility(View.GONE);
                buttonFinishEarly.setVisibility(View.GONE);
                buttonFinish.setVisibility(View.GONE);
                buttonDeny.setVisibility(View.VISIBLE);
                
                // Show Start button only if no active ride and this is the first ride
                if (!hasActiveRide && canStart) {
                    buttonStart.setVisibility(View.VISIBLE);
                } else {
                    buttonStart.setVisibility(View.GONE);
                }
            }

            // Set click listeners
            rootView.setOnClickListener(v -> listener.onRideClicked(ride));
            buttonStart.setOnClickListener(v -> listener.onStartRide(ride.getRideId()));
            buttonPanic.setOnClickListener(v -> listener.onPanicRide(ride.getRideId()));
            buttonFinish.setOnClickListener(v -> listener.onFinishRide(ride.getRideId()));
            buttonFinishEarly.setOnClickListener(v -> listener.onFinishEarly(ride.getRideId()));
            buttonDeny.setOnClickListener(v -> listener.onCancelRide(ride.getRideId()));
        }

        private String formatDateTime(LocalDateTime dateTime) {
            if (dateTime == null) return "Loading...";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
            return dateTime.format(formatter);
        }
    }
}