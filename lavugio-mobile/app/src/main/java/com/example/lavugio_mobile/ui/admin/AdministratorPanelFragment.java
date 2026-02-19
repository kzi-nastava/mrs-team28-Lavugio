package com.example.lavugio_mobile.ui.admin;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lavugio_mobile.LavugioApp;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.services.WebSocketService;
import com.example.lavugio_mobile.ui.admin.history.AdminRideHistoryFragment;
import com.example.lavugio_mobile.ui.dialog.PriceDefinitionDialogFragment;
import com.example.lavugio_mobile.ui.reports.RidesReportsFragment;

import org.json.JSONObject;

public class AdministratorPanelFragment extends Fragment {

    private static final String TAG = "AdminPanel";

    private Button btnRegisterDriver;
    private Button btnDriverUpdateRequests;
    private Button btnBlockUser;
    private Button btnUserHistory;
    private Button btnSeeReports;
    private Button btnPanicAlerts;
    private Button btnPriceDefinition;
    private Button btnRideMonitoring;

    private WebSocketService webSocketService;
    private WebSocketService.StompSubscription panicSub = null;
    private int unreadPanicCount = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webSocketService = LavugioApp.getWebSocketService();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_panel, container, false);

        // Initialize buttons
        btnRegisterDriver = view.findViewById(R.id.btnRegisterDriver);
        btnDriverUpdateRequests = view.findViewById(R.id.btnDriverUpdateRequests);
        btnBlockUser = view.findViewById(R.id.btnBlockUser);
        btnUserHistory = view.findViewById(R.id.btnUserHistory);
        btnSeeReports = view.findViewById(R.id.btnSeeReports);
        btnPanicAlerts = view.findViewById(R.id.btnPanicAlerts);
        btnPriceDefinition = view.findViewById(R.id.btnPriceDefinition);
        btnRideMonitoring = view.findViewById(R.id.btnRideMonitoring);

        btnPriceDefinition.setOnClickListener(v -> {
            PriceDefinitionDialogFragment dialog = PriceDefinitionDialogFragment.newInstance();
            dialog.show(getChildFragmentManager(), "price_definition");
        });

        // Set click listeners
        btnRegisterDriver.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new RegisterDriverFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnDriverUpdateRequests.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new DriverUpdateRequestsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnBlockUser.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new BlockUserFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnUserHistory.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, AdminRideHistoryFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        });

        btnSeeReports.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new RidesReportsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnPanicAlerts.setOnClickListener(v -> {
            // Don't clear count here — user should see list to acknowledge
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new PanicAlertsFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnRideMonitoring.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_container, new RideMonitoringFragment())
                    .addToBackStack(null)
                    .commit();
        });

        webSocketService.connect(() -> subscribeToPanic());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (panicSub != null) {
            panicSub.unsubscribe();
            panicSub = null;
        }
    }

    private void subscribeToPanic() {
        panicSub = webSocketService.subscribe(
                "/socket-publisher/admin/panic",
                body -> {
                    if (!isAdded()) return;
                    PanicAlertsFragment.PanicAlertItem item = null;
                    try {
                        JSONObject json = new JSONObject(body);
                        long rideId = json.optLong("rideId", -1);
                        String passengerName = json.optString("passengerName", "Unknown");
                        String driverName = json.optString("driverName", "Unknown");
                        String vehicleType = json.optString("vehicleType", "");
                        String vehicleLicensePlate = json.optString("vehicleLicensePlate", "");
                        String message = json.optString("message", "");
                        String timestamp = json.optString("timestamp", "");
                        double latitude = 0, longitude = 0;
                        JSONObject location = json.optJSONObject("location");
                        if (location != null) {
                            latitude = location.optDouble("latitude", 0);
                            longitude = location.optDouble("longitude", 0);
                        }
                        item = new PanicAlertsFragment.PanicAlertItem(
                                rideId, passengerName, driverName,
                                vehicleType, vehicleLicensePlate,
                                latitude, longitude, message, timestamp);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse panic body: " + body, e);
                    }

                    if (item == null) return;
                    final PanicAlertsFragment.PanicAlertItem finalItem = item;
                    requireActivity().runOnUiThread(() -> {
                        PanicAlertsFragment.addAlert(finalItem);
                        unreadPanicCount++;
                        updatePanicButtonLabel();
                        playAlertSound();
                        Toast.makeText(requireContext(),
                                "🚨 PANIC ALERT for Ride #" + finalItem.rideId,
                                Toast.LENGTH_LONG).show();
                    });
                }
        );
    }

    private void updatePanicButtonLabel() {
        if (btnPanicAlerts == null) return;
        if (unreadPanicCount > 0) {
            btnPanicAlerts.setText("🚨 Panic Alerts (" + unreadPanicCount + ")");
        } else {
            btnPanicAlerts.setText("🚨 Panic Alerts");
        }
    }

    private void playAlertSound() {
        try {
            ToneGenerator toneGen = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 800);
            new android.os.Handler(android.os.Looper.getMainLooper())
                    .postDelayed(toneGen::release, 1000);
        } catch (Exception e) {
            Log.w(TAG, "Could not play alert sound: " + e.getMessage());
        }
    }
}
