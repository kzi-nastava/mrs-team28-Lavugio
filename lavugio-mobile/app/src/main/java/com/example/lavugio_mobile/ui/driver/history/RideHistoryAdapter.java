package com.example.lavugio_mobile.ui.driver.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.RideHistoryDriverModel;

import java.util.List;

public class RideHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_END = 1;

    private final List<RideHistoryDriverModel> rides;
    private final OnRideClickListener clickListener;

    private boolean hasMoreNewer = false;
    private boolean hasMoreOlder = true;

    public interface OnRideClickListener {
        void onRideClick(RideHistoryDriverModel ride);
    }

    public RideHistoryAdapter(List<RideHistoryDriverModel> rides, OnRideClickListener clickListener) {
        this.rides = rides;
        this.clickListener = clickListener;
    }

    public void setHasMoreNewer(boolean hasMoreNewer) {
        this.hasMoreNewer = hasMoreNewer;
    }

    public void setHasMoreOlder(boolean hasMoreOlder) {
        this.hasMoreOlder = hasMoreOlder;
    }

    @Override
    public int getItemViewType(int position) {
        if (!hasMoreOlder && position == rides.size() - 1 && !rides.isEmpty()) {
            return VIEW_TYPE_END;
        }
        return VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_END) {
            View endView = inflater.inflate(R.layout.item_end_message, parent, false);
            return new EndViewHolder(endView);
        }

        View itemView = inflater.inflate(R.layout.fragment_driver_ride_history_card, parent, false);
        return new RideViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RideViewHolder && position < rides.size()) {
            RideHistoryDriverModel ride = rides.get(position);
            ((RideViewHolder) holder).bind(ride, clickListener);
        }
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    // ── ViewHolder: ride item ────────────────────────────

    static class RideViewHolder extends RecyclerView.ViewHolder {
        private final TextView startDateText;
        private final TextView startTimeText;
        private final TextView endDateText;
        private final TextView endTimeText;
        private final TextView departureText;
        private final TextView destinationText;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            startDateText = itemView.findViewById(R.id.tripStartDate);
            startTimeText = itemView.findViewById(R.id.tripStartTime);
            endDateText = itemView.findViewById(R.id.tripEndDate);
            endTimeText = itemView.findViewById(R.id.tripEndTime);
            departureText = itemView.findViewById(R.id.tripDeparture);
            destinationText = itemView.findViewById(R.id.tripDestination);
        }

        public void bind(RideHistoryDriverModel ride, OnRideClickListener clickListener) {
            // Backend sends "HH:mm dd.MM.yyyy" — model splits it for us
            startTimeText.setText(ride.getStartTime());
            startDateText.setText(ride.getStartDateOnly());

            endTimeText.setText(ride.getEndTime());
            endDateText.setText(ride.getEndDateOnly());

            departureText.setText(
                    ride.getStartAddress() != null ? ride.getStartAddress() : "N/A");
            destinationText.setText(
                    ride.getEndAddress() != null ? ride.getEndAddress() : "N/A");

            itemView.setOnClickListener(v -> clickListener.onRideClick(ride));
        }
    }

    // ── ViewHolder: end message ──────────────────────────

    static class EndViewHolder extends RecyclerView.ViewHolder {
        public EndViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}