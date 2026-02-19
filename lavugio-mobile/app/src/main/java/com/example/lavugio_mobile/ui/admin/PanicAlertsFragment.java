package com.example.lavugio_mobile.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavugio_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class PanicAlertsFragment extends Fragment {

    // ── Shared static state (survives fragment navigation) ──────────────────
    static final List<PanicAlertItem> sharedAlerts = new ArrayList<>();
    private static Runnable onNewAlertCallback = null;

    static void addAlert(PanicAlertItem item) {
        sharedAlerts.add(0, item);
        if (onNewAlertCallback != null) {
            onNewAlertCallback.run();
        }
    }

    static void clearAlerts() {
        sharedAlerts.clear();
        if (onNewAlertCallback != null) {
            onNewAlertCallback.run();
        }
    }

    // ── Fragment ────────────────────────────────────────────────────────────

    private RecyclerView recyclerView;
    private TextView tvNoAlerts;
    private Button btnClearAll;
    private PanicAlertAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_panic_alerts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_panic_alerts);
        tvNoAlerts   = view.findViewById(R.id.tv_no_alerts);
        btnClearAll  = view.findViewById(R.id.btn_clear_all);

        adapter = new PanicAlertAdapter(sharedAlerts, this::onDismissAlert);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Register callback so AdministratorPanelFragment can push updates here
        onNewAlertCallback = () -> {
            if (isAdded() && requireActivity() != null) {
                requireActivity().runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                });
            }
        };

        btnClearAll.setOnClickListener(v -> {
            sharedAlerts.clear();
            adapter.notifyDataSetChanged();
            updateEmptyState();
        });

        updateEmptyState();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onNewAlertCallback = null;
    }

    private void onDismissAlert(int position) {
        if (position >= 0 && position < sharedAlerts.size()) {
            sharedAlerts.remove(position);
            adapter.notifyItemRemoved(position);
            updateEmptyState();
        }
    }

    private void updateEmptyState() {
        if (sharedAlerts.isEmpty()) {
            tvNoAlerts.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoAlerts.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // ── Model ───────────────────────────────────────────────────────────────

    public static class PanicAlertItem {
        final long rideId;
        final String passengerName;
        final String driverName;
        final String vehicleType;
        final String vehicleLicensePlate;
        final double latitude;
        final double longitude;
        final String message;
        final String timestamp;

        PanicAlertItem(long rideId, String passengerName, String driverName,
                       String vehicleType, String vehicleLicensePlate,
                       double latitude, double longitude,
                       String message, String timestamp) {
            this.rideId = rideId;
            this.passengerName = passengerName;
            this.driverName = driverName;
            this.vehicleType = vehicleType;
            this.vehicleLicensePlate = vehicleLicensePlate;
            this.latitude = latitude;
            this.longitude = longitude;
            this.message = message;
            this.timestamp = timestamp;
        }
    }

    // ── Adapter ─────────────────────────────────────────────────────────────

    interface OnDismissListener {
        void onDismiss(int position);
    }

    static class PanicAlertAdapter extends RecyclerView.Adapter<PanicAlertAdapter.ViewHolder> {

        private final List<PanicAlertItem> items;
        private final OnDismissListener dismissListener;

        PanicAlertAdapter(List<PanicAlertItem> items, OnDismissListener listener) {
            this.items = items;
            this.dismissListener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_panic_alert, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(items.get(position), position, dismissListener);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView tvRideId;
            final TextView tvPassenger;
            final TextView tvDriver;
            final TextView tvVehicle;
            final TextView tvLocation;
            final TextView tvMessage;
            final TextView tvTimestamp;
            final ImageButton btnDismiss;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvRideId    = itemView.findViewById(R.id.tv_alert_ride_id);
                tvPassenger = itemView.findViewById(R.id.tv_alert_passenger);
                tvDriver    = itemView.findViewById(R.id.tv_alert_driver);
                tvVehicle   = itemView.findViewById(R.id.tv_alert_vehicle);
                tvLocation  = itemView.findViewById(R.id.tv_alert_location);
                tvMessage   = itemView.findViewById(R.id.tv_alert_message);
                tvTimestamp = itemView.findViewById(R.id.tv_alert_timestamp);
                btnDismiss  = itemView.findViewById(R.id.btn_dismiss_alert);
            }

            void bind(PanicAlertItem item, int position, OnDismissListener listener) {
                tvRideId.setText(item.rideId > 0 ? "#" + item.rideId : "—");
                tvPassenger.setText(item.passengerName != null ? item.passengerName : "—");
                tvDriver.setText(item.driverName != null ? item.driverName : "—");
                tvVehicle.setText(buildVehicleText(item));
                tvLocation.setText(buildLocationText(item));
                tvMessage.setText(item.message != null && !item.message.isEmpty()
                        ? item.message : "Emergency panic triggered");
                tvTimestamp.setText(item.timestamp != null ? item.timestamp : "");
                btnDismiss.setOnClickListener(v -> listener.onDismiss(getAdapterPosition()));
            }

            private String buildVehicleText(PanicAlertItem item) {
                String type = item.vehicleType != null ? item.vehicleType : "";
                String plate = item.vehicleLicensePlate != null ? item.vehicleLicensePlate : "";
                if (!type.isEmpty() && !plate.isEmpty()) return type + " (" + plate + ")";
                if (!type.isEmpty()) return type;
                if (!plate.isEmpty()) return plate;
                return "—";
            }

            private String buildLocationText(PanicAlertItem item) {
                if (item.latitude == 0 && item.longitude == 0) return "—";
                return String.format("%.5f, %.5f", item.latitude, item.longitude);
            }
        }
    }
}
