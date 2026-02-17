package com.example.lavugio_mobile.ui.admin.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.AdminHistoryModel;

import java.util.List;

public class AdminRideHistoryAdapter extends RecyclerView.Adapter<AdminRideHistoryAdapter.ViewHolder> {

    private final List<AdminHistoryModel> rides;
    private final OnRideClickListener listener;

    public interface OnRideClickListener {
        void onRideClick(AdminHistoryModel ride);
    }

    public AdminRideHistoryAdapter(List<AdminHistoryModel> rides, OnRideClickListener listener) {
        this.rides = rides;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_ride_history_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminHistoryModel ride = rides.get(position);
        holder.bind(ride, listener);
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView startDateTextView;
        private final TextView startTimeTextView;
        private final TextView endDateTextView;
        private final TextView endTimeTextView;
        private final TextView departureTextView;
        private final TextView destinationTextView;
        private final TextView priceTextView;
        private final TextView cancelledTextView;
        private final TextView panicTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            startDateTextView = itemView.findViewById(R.id.startDateTextView);
            startTimeTextView = itemView.findViewById(R.id.startTimeTextView);
            endDateTextView = itemView.findViewById(R.id.endDateTextView);
            endTimeTextView = itemView.findViewById(R.id.endTimeTextView);
            departureTextView = itemView.findViewById(R.id.departureTextView);
            destinationTextView = itemView.findViewById(R.id.destinationTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            cancelledTextView = itemView.findViewById(R.id.cancelledTextView);
            panicTextView = itemView.findViewById(R.id.panicTextView);
        }

        public void bind(AdminHistoryModel ride, OnRideClickListener listener) {
            // Parse start date and time
            String[] startParts = parseDateTime(ride.getStartDate());
            startDateTextView.setText(startParts[1]);
            startTimeTextView.setText(startParts[0]);

            // Parse end date and time
            if (ride.getEndDate() != null && !ride.getEndDate().isEmpty()) {
                String[] endParts = parseDateTime(ride.getEndDate());
                endDateTextView.setText(endParts[1]);
                endTimeTextView.setText(endParts[0]);
            } else {
                endDateTextView.setText("");
                endTimeTextView.setText("");
            }

            departureTextView.setText(ride.getStartAddress());
            destinationTextView.setText(ride.getEndAddress());
            priceTextView.setText(String.format("%.2f RSD", ride.getPrice()));

            // Cancelled status
            if (ride.isCancelled()) {
                cancelledTextView.setText(ride.getCancelledBy() != null
                        ? "Yes (" + ride.getCancelledBy() + ")"
                        : "Yes");
                cancelledTextView.setTextColor(itemView.getContext().getResources()
                        .getColor(android.R.color.holo_red_dark, null));
            } else {
                cancelledTextView.setText("No");
                cancelledTextView.setTextColor(itemView.getContext().getResources()
                        .getColor(android.R.color.holo_green_dark, null));
            }

            // Panic status
            if (ride.isPanic()) {
                panicTextView.setText("YES");
                panicTextView.setTextColor(itemView.getContext().getResources()
                        .getColor(android.R.color.holo_red_dark, null));
            } else {
                panicTextView.setText("No");
                panicTextView.setTextColor(itemView.getContext().getResources()
                        .getColor(android.R.color.holo_green_dark, null));
            }

            itemView.setOnClickListener(v -> listener.onRideClick(ride));
        }

        private String[] parseDateTime(String dateTimeStr) {
            // Format: "HH:mm dd.MM.yyyy"
            if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
                return new String[]{"", ""};
            }
            String[] parts = dateTimeStr.trim().split(" ");
            if (parts.length == 2) {
                return new String[]{parts[0], parts[1]};
            }
            return new String[]{"", ""};
        }
    }
}
