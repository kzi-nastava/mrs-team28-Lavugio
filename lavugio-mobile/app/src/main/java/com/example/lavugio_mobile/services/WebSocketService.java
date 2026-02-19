package com.example.lavugio_mobile.services;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.lavugio_mobile.BuildConfig;
import com.example.lavugio_mobile.api.LocalDateTimeAdapter;
import com.example.lavugio_mobile.services.auth.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.time.LocalDateTime;
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

public class WebSocketService {

    private static final String TAG = "WebSocketService";
    private static final String SOCKET_URL = "http://" + BuildConfig.SERVER_IP + ":8080/socket";
    private static final long RECONNECT_DELAY_MS = 5000;

    private StompClient stompClient;
    private SessionManager sessionManager;
    private boolean isConnectedFlag = false;
    private boolean isReconnecting = false;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    // Active subscription disposables so they can be individually unsubscribed
    private final Map<String, Disposable> activeSubscriptions = new HashMap<>();

    // Queued subscriptions waiting for connection
    private final List<PendingSubscription> pendingSubscriptions = new ArrayList<>();

    // FIX: čuvamo sve callback-ove za reconnect, ne samo jedan
    private final List<Runnable> reconnectCallbacks = new ArrayList<>();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    // ── Interfaces ────────────────────────────────────────────────────────

    public interface ParsedMessageCallback<T> {
        void onMessage(T body);
    }

    public interface MessageCallback {
        void onMessage(String body);
    }

    // ── StompSubscription ─────────────────────────────────────────────────

    /**
     * Handle to an active subscription. Call unsubscribe() to stop receiving messages.
     * FIX: Nikad ne vraćamo null — vraćamo placeholder koji se popuni kad konekcija bude ready.
     */
    public class StompSubscription {
        private String id; // može biti null dok je pending
        private final String destination;
        private boolean unsubscribed = false;

        StompSubscription(String id, String destination) {
            this.id = id;
            this.destination = destination;
        }

        void setId(String id) {
            this.id = id;
            // Ako je unsubscribe pozvan dok smo čekali konekciju
            if (unsubscribed && id != null) {
                WebSocketService.this.unsubscribe(id);
            }
        }

        public String getId() { return id; }
        public String getDestination() { return destination; }

        public void unsubscribe() {
            unsubscribed = true;
            if (id != null) {
                WebSocketService.this.unsubscribe(id);
            } else {
                // Ukloni iz pending liste
                pendingSubscriptions.removeIf(p -> p.subscription == this);
            }
        }
    }

    private static class PendingSubscription {
        final String destination;
        final MessageCallback callback;
        final StompSubscription subscription; // referenca na placeholder

        PendingSubscription(String destination, MessageCallback callback, StompSubscription subscription) {
            this.destination = destination;
            this.callback = callback;
            this.subscription = subscription;
        }
    }

    // ── Setup ─────────────────────────────────────────────────────────────

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    // ── Connect ───────────────────────────────────────────────────────────

    @SuppressLint("CheckResult")
    public void connect(@Nullable Runnable onConnect) {
        if (stompClient != null && stompClient.isConnected()) {
            if (onConnect != null) onConnect.run();
            return;
        }

        if (stompClient != null && !isConnectedFlag) {
            if (onConnect != null) reconnectCallbacks.add(onConnect);
            return;
        }

        if (onConnect != null) reconnectCallbacks.add(onConnect);

        stompClient = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                SOCKET_URL + "/websocket"
        );

        stompClient.withClientHeartbeat(10000);
        stompClient.withServerHeartbeat(10000);

        List<StompHeader> headers = new ArrayList<>();
        if (sessionManager != null && sessionManager.getToken() != null) {
            headers.add(new StompHeader("Authorization", "Bearer " + sessionManager.getToken()));
        }

        Disposable lifecycleDisposable = stompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    switch (event.getType()) {

                        case OPENED:
                            Log.d(TAG, "STOMP connection opened");
                            isConnectedFlag = true;
                            isReconnecting = false;

                            List<PendingSubscription> pending = new ArrayList<>(pendingSubscriptions);
                            pendingSubscriptions.clear();
                            for (PendingSubscription sub : pending) {
                                StompSubscription handle = doSubscribe(sub.destination, sub.callback);
                                sub.subscription.setId(handle.id);
                            }

                            List<Runnable> callbacks = new ArrayList<>(reconnectCallbacks);
                            reconnectCallbacks.clear();
                            for (Runnable cb : callbacks) {
                                cb.run();
                            }
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
        stompClient.connect(headers);
    }

    public void connect() {
        connect(null);
    }

    // ── Subscribe ─────────────────────────────────────────────────────────

    /**
     * FIX: Uvek vraća non-null StompSubscription (placeholder pattern).
     * Ako konekcija nije ready, subscription se popuni kada konekcija bude otvorena.
     */
    public StompSubscription subscribe(String destination, MessageCallback callback) {
        StompSubscription placeholder = new StompSubscription(null, destination);

        if (!isConnectedFlag || stompClient == null) {
            Log.d(TAG, "Not connected yet — queueing subscription to: " + destination);
            pendingSubscriptions.add(new PendingSubscription(destination, callback, placeholder));

            if (stompClient == null) {
                connect();
            }
            return placeholder; // FIX: vraćamo placeholder, ne null
        }

        StompSubscription real = doSubscribe(destination, callback);
        placeholder.setId(real.id);
        return placeholder;
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
        return new StompSubscription(subscriptionId, destination);
    }

    // ── Unsubscribe ───────────────────────────────────────────────────────

    public void unsubscribe(String subscriptionId) {
        Disposable disposable = activeSubscriptions.remove(subscriptionId);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
            Log.d(TAG, "Unsubscribed: " + subscriptionId);
        }
    }

    // ── Publish ───────────────────────────────────────────────────────────

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

    // ── Disconnect ────────────────────────────────────────────────────────

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
            stompClient = null;
        }

        compositeDisposable.clear();
        activeSubscriptions.clear();
        pendingSubscriptions.clear();
        reconnectCallbacks.clear(); // FIX: čistimo i callback listu
        isConnectedFlag = false;
        isReconnecting = false;

        Log.d(TAG, "Disconnected and cleared all subscriptions");
    }

    // ── Status ────────────────────────────────────────────────────────────

    public boolean isConnected() {
        return stompClient != null && stompClient.isConnected();
    }

    // ── Internal ──────────────────────────────────────────────────────────

    private void handleDisconnect() {
        isConnectedFlag = false;
    }

    private void scheduleReconnect() {
        // FIX: Sprečavamo višestruke reconnect timere
        if (isReconnecting) return;
        isReconnecting = true;

        Log.d(TAG, "Scheduling reconnect in " + RECONNECT_DELAY_MS + "ms");
        new android.os.Handler(android.os.Looper.getMainLooper())
                .postDelayed(() -> {
                    if (!isConnected()) {
                        Log.d(TAG, "Attempting reconnect...");
                        isReconnecting = false;
                        stompClient = null; // FIX: resetujemo klijent da connect() ne blokira
                        connect(null);
                    } else {
                        isReconnecting = false;
                    }
                }, RECONNECT_DELAY_MS);
    }

    public <T> StompSubscription subscribeJson(String destination, Class<T> clazz,
                                               ParsedMessageCallback<T> callback) {
        return subscribe(destination, body -> {
            try {
                T parsed = gson.fromJson(body, clazz);
                callback.onMessage(parsed);
            } catch (Exception e) {
                Log.e(TAG, "Failed to parse message on " + destination, e);
            }
        });
    }
}