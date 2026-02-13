package com.example.lavugio_mobile.services;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.lavugio_mobile.BuildConfig;
import com.example.lavugio_mobile.services.auth.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

/**
 * STOMP-over-SockJS WebSocket service for Android.
 * Uses NaikSoftware/StompProtocolAndroid — the Android equivalent
 * of Angular's @stomp/stompjs + SockJS.
 */
public class WebSocketService {

    private static final String TAG = "WebSocketService";
    private static final String SOCKET_URL = "http://" + BuildConfig.SERVER_IP + ":8080/socket";
    private static final long RECONNECT_DELAY_MS = 5000;

    private StompClient stompClient;
    private SessionManager sessionManager;
    private boolean isConnectedFlag = false;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    // Active subscription disposables so they can be individually unsubscribed
    private final Map<String, Disposable> activeSubscriptions = new HashMap<>();

    // Queued subscriptions waiting for connection
    private final List<PendingSubscription> pendingSubscriptions = new ArrayList<>();

    private Runnable onConnectCallback;

    // ── Inner classes ────────────────────────────────────

    public interface MessageCallback {
        void onMessage(String body);
    }

    /**
     * Handle to an active subscription. Call unsubscribe() to stop receiving messages.
     */
    public static class StompSubscription {
        private final String id;
        private final String destination;
        private final WebSocketService service;

        StompSubscription(String id, String destination, WebSocketService service) {
            this.id = id;
            this.destination = destination;
            this.service = service;
        }

        public String getId() { return id; }
        public String getDestination() { return destination; }

        public void unsubscribe() {
            service.unsubscribe(this.id);
        }
    }

    private static class PendingSubscription {
        final String destination;
        final MessageCallback callback;

        PendingSubscription(String destination, MessageCallback callback) {
            this.destination = destination;
            this.callback = callback;
        }
    }

    // ── Setup ────────────────────────────────────────────

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    // ── Connect ──────────────────────────────────────────

    @SuppressLint("CheckResult")
    public void connect(@Nullable Runnable onConnect) {
        if (stompClient != null && stompClient.isConnected()) {
            if (onConnect != null) onConnect.run();
            return;
        }

        this.onConnectCallback = onConnect;

        // Create STOMP client over SockJS transport
        stompClient = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                SOCKET_URL + "/websocket"  // SockJS requires /websocket suffix for raw WS
        );

        stompClient.withClientHeartbeat(10000);
        stompClient.withServerHeartbeat(10000);

        // Build auth headers
        List<StompHeader> headers = new ArrayList<>();
        if (sessionManager != null && sessionManager.getToken() != null) {
            headers.add(new StompHeader("Authorization", "Bearer " + sessionManager.getToken()));
        }

        // Listen to connection lifecycle
        Disposable lifecycleDisposable = stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    switch (event.getType()) {
                        case OPENED:
                            Log.d(TAG, "STOMP connection opened");
                            break;

                        case CLOSED:
                            Log.d(TAG, "STOMP connection closed");
                            handleDisconnect();
                            scheduleReconnect();
                            break;

                        case ERROR:
                            Log.e(TAG, "STOMP connection error", event.getException());
                            handleDisconnect();
                            scheduleReconnect();
                            break;
                    }
                }, throwable -> {
                    Log.e(TAG, "STOMP lifecycle error", throwable);
                });
        compositeDisposable.add(lifecycleDisposable);

        // Connect
        stompClient.connect(headers);
        isConnectedFlag = true;

        // Flush pending subscriptions
        List<PendingSubscription> pending = new ArrayList<>(pendingSubscriptions);
        pendingSubscriptions.clear();
        for (PendingSubscription sub : pending) {
            doSubscribe(sub.destination, sub.callback);
        }

        if (onConnectCallback != null) {
            onConnectCallback.run();
            onConnectCallback = null;
        }
    }

    public void connect() {
        connect(null);
    }

    // ── Subscribe ────────────────────────────────────────

    @Nullable
    public StompSubscription subscribe(String destination, MessageCallback callback) {
        if (stompClient == null || !stompClient.isConnected()) {
            Log.d(TAG, "Not connected yet — queueing subscription to: " + destination);
            pendingSubscriptions.add(new PendingSubscription(destination, callback));

            if (stompClient == null) {
                connect();
            }
            return null;
        }

        return doSubscribe(destination, callback);
    }

    @SuppressLint("CheckResult")
    private StompSubscription doSubscribe(String destination, MessageCallback callback) {
        String subscriptionId = "sub-" + System.currentTimeMillis();

        Disposable disposable = stompClient.topic(destination)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (StompMessage message) -> {
                            Log.d(TAG, "Received on " + destination + ": " + message.getPayload());
                            callback.onMessage(message.getPayload());
                        },
                        throwable -> {
                            Log.e(TAG, "Error on subscription " + destination, throwable);
                        }
                );

        activeSubscriptions.put(subscriptionId, disposable);
        compositeDisposable.add(disposable);

        Log.d(TAG, "Subscribed to " + destination + " (id=" + subscriptionId + ")");
        return new StompSubscription(subscriptionId, destination, this);
    }

    // ── Unsubscribe ──────────────────────────────────────

    public void unsubscribe(String subscriptionId) {
        Disposable disposable = activeSubscriptions.remove(subscriptionId);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            Log.d(TAG, "Unsubscribed: " + subscriptionId);
        }
    }

    // ── Publish ──────────────────────────────────────────

    @SuppressLint("CheckResult")
    public void publish(String destination, Object body) {
        if (stompClient == null || !stompClient.isConnected()) {
            Log.e(TAG, "Cannot publish — not connected");
            return;
        }

        String jsonBody;
        if (body instanceof String) {
            jsonBody = (String) body;
        } else if (body instanceof Map) {
            jsonBody = new JSONObject((Map) body).toString();
        } else {
            jsonBody = body.toString();
        }

        stompClient.send(destination, jsonBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> Log.d(TAG, "Sent to " + destination),
                        throwable -> Log.e(TAG, "Send error to " + destination, throwable)
                );
    }

    // ── Disconnect ───────────────────────────────────────

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
            stompClient = null;
        }

        compositeDisposable.clear();
        activeSubscriptions.clear();
        pendingSubscriptions.clear();
        isConnectedFlag = false;
        onConnectCallback = null;

        Log.d(TAG, "Disconnected and cleared all subscriptions");
    }

    // ── Status ───────────────────────────────────────────

    public boolean isConnected() {
        return stompClient != null && stompClient.isConnected();
    }

    // ── Internal ─────────────────────────────────────────

    private void handleDisconnect() {
        isConnectedFlag = false;
    }

    private void scheduleReconnect() {
        Log.d(TAG, "Scheduling reconnect in " + RECONNECT_DELAY_MS + "ms");
        new android.os.Handler(android.os.Looper.getMainLooper())
                .postDelayed(() -> {
                    if (!isConnected()) {
                        Log.d(TAG, "Attempting reconnect...");
                        connect(onConnectCallback);
                    }
                }, RECONNECT_DELAY_MS);
    }
}