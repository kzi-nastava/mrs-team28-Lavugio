package com.example.lavugio_mobile.ui.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.RideMonitoringModel;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RideMonitoringAdapter
        extends RecyclerView.Adapter<RideMonitoringAdapter.ViewHolder> {

    public interface OnRideClickListener {
        void onRideClick(RideMonitoringModel ride);
    }

    private final List<RideMonitoringModel> rides = new ArrayList<>();
    private final OnRideClickListener listener;

    public RideMonitoringAdapter(OnRideClickListener listener) {
        this.listener = listener;
    }

    public void setRides(List<RideMonitoringModel> newRides) {
        rides.clear();
        if (newRides != null) rides.addAll(newRides);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride_monitoring, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(rides.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvStartTime;
        private final TextView tvStartAddress;
        private final TextView tvEndAddress;
        private final TextView tvDriverName;
        private final View panicIndicator;
        private final TextView tvPanicBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStartTime    = itemView.findViewById(R.id.tv_start_time);
            tvStartAddress = itemView.findViewById(R.id.tv_start_address);
            tvEndAddress   = itemView.findViewById(R.id.tv_end_address);
            tvDriverName   = itemView.findViewById(R.id.tv_driver_name);
            panicIndicator = itemView.findViewById(R.id.panic_indicator);
            tvPanicBadge   = itemView.findViewById(R.id.tv_panic_badge);
        }

        void bind(RideMonitoringModel ride, OnRideClickListener listener) {
            tvStartTime.setText(formatDateTime(ride.getStartTime()));
            tvStartAddress.setText(ride.getStartAddress() != null ? ride.getStartAddress() : "Loading...");
            tvEndAddress.setText(ride.getEndAddress() != null ? ride.getEndAddress() : "Loading...");
            tvDriverName.setText(ride.getDriverName() != null ? ride.getDriverName() : "Loading...");

            boolean panicked = ride.isPanicked();
            panicIndicator.setVisibility(panicked ? View.VISIBLE : View.GONE);
            tvPanicBadge.setVisibility(panicked ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> listener.onRideClick(ride));
        }

        private String formatDateTime(LocalDateTime date) {
            if (date == null) return "Loading...";
            DateTimeFormatter sdf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.getDefault());
            return sdf.format(date);
        }
    }
}