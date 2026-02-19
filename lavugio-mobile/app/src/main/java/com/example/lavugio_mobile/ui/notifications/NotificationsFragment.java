package com.example.lavugio_mobile.ui.notifications;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavugio_mobile.LavugioApp;
import com.example.lavugio_mobile.R;
import com.example.lavugio_mobile.models.NotificationModel;
import com.example.lavugio_mobile.services.NotificationService;
import com.example.lavugio_mobile.services.UserService;
import com.example.lavugio_mobile.services.WebSocketService;
import com.example.lavugio_mobile.ui.ride.RideOverviewFragment;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment implements
        NotificationAdapter.OnNotificationClickListener {

    private static final String TAG = "NotificationsFragment";

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private NotificationAdapter adapter;

    private final List<NotificationModel> notifications = new ArrayList<>();

    private NotificationService notificationService;
    private UserService userService;
    private WebSocketService webSocketService;

    private WebSocketService.StompSubscription notificationSubscription;

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationService = LavugioApp.getNotificationService();
        userService         = LavugioApp.getUserService();
        webSocketService    = LavugioApp.getWebSocketService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView  = view.findViewById(R.id.recyclerViewNotifications);
        emptyTextView = view.findViewById(R.id.textViewEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NotificationAdapter(notifications, this);
        recyclerView.setAdapter(adapter);

        loadNotifications();
        subscribeToWebSocket();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unsubscribeFromWebSocket();
        recyclerView  = null;
        emptyTextView = null;
        adapter       = null;
    }

    // ── Load initial notifications ────────────────────────────────────────────

    private void loadNotifications() {
        notificationService.getNotifications(new NotificationService.Callback<List<NotificationModel>>() {
            @Override
            public void onSuccess(List<NotificationModel> result) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    notifications.clear();
                    if (result != null) notifications.addAll(result);
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                });
            }

            @Override
            public void onError(int code, String message) {
                Log.e(TAG, "Failed to load notifications: " + message);
            }
        });
    }

    // ── WebSocket ─────────────────────────────────────────────────────────────

    private void subscribeToWebSocket() {
        long userId = userService.getCurrentUserId();

        webSocketService.connect(() -> {
            notificationSubscription = webSocketService.subscribeJson(
                    "/socket-publisher/notifications/" + userId,
                    NotificationModel.class,
                    notification -> {
                        if (!isAdded()) return;
                        Log.d(TAG, "New notification received: " + notification.getTitle());
                        requireActivity().runOnUiThread(() -> {
                            notifications.add(0, notification);
                            adapter.notifyItemInserted(0);
                            recyclerView.scrollToPosition(0);
                            updateEmptyState();
                        });
                    }
            );
        });
    }

    private void unsubscribeFromWebSocket() {
        if (notificationSubscription != null) {
            notificationSubscription.unsubscribe();
            notificationSubscription = null;
        }
    }

    // ── Click handler ─────────────────────────────────────────────────────────

    @Override
    public void onNotificationClicked(NotificationModel notification) {
        String link = notification.getLink();
        if (link == null || link.isEmpty()) return;

        // Format linka: "{rideId}/ride-overview"
        if (link.contains("ride-overview")) {
            try {
                long rideId = Long.parseLong(link.split("/")[0]);
                openRideOverview(rideId);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Failed to parse rideId from link: " + link, e);
            }
        }
        // dodaj else if ovdje za ostale tipove linkova
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void updateEmptyState() {
        if (emptyTextView == null) return;
        emptyTextView.setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openRideOverview(long rideId) {
        Bundle bundle = new Bundle();
        bundle.putLong("rideId", rideId);

        RideOverviewFragment fragment = new RideOverviewFragment();
        fragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}