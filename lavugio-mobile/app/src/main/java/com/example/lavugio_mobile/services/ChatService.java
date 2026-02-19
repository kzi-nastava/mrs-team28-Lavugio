package com.example.lavugio_mobile.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.lavugio_mobile.api.ApiClient;
import com.example.lavugio_mobile.api.ChatApi;
import com.example.lavugio_mobile.models.ChatMessageModel;
import com.example.lavugio_mobile.models.UserChatModel;
import com.example.lavugio_mobile.api.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ChatService {

    private static final String TAG = "ChatService";

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(int code, String message);
    }

    private final ChatApi api;
    private final WebSocketService webSocketService;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    // Keep reference so it can be unsubscribed on disconnect
    private WebSocketService.StompSubscription chatSubscription;

    public ChatService(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
        this.api = ApiClient.getChatApi();
    }

    // ── REST ──────────────────────────────────────────────────────────────────

    /**
     * Fetches chat history for the given user.
     * Admins pass the target user's ID.
     * Regular users pass 0 (backend resolves from JWT).
     */
    public void getChatHistory(int userId, Callback<List<ChatMessageModel>> callback) {
        api.getChatHistory(userId).enqueue(new retrofit2.Callback<List<ChatMessageModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ChatMessageModel>> call,
                                   @NonNull Response<List<ChatMessageModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(response.code(), "Failed to load history");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ChatMessageModel>> call,
                                  @NonNull Throwable t) {
                callback.onError(-1, t.getMessage());
            }
        });
    }

    /**
     * Admin-only: get list of users that can be chatted with.
     */
    public void getChattableUsers(Callback<List<UserChatModel>> callback) {
        api.getChattableUsers().enqueue(new retrofit2.Callback<List<UserChatModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserChatModel>> call,
                                   @NonNull Response<List<UserChatModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(response.code(), "Failed to load users");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserChatModel>> call,
                                  @NonNull Throwable t) {
                callback.onError(-1, t.getMessage());
            }
        });
    }

    // ── WebSocket ─────────────────────────────────────────────────────────────

    public void connectToChat(int userId, Callback<ChatMessageModel> onMessage) {
        String topic = "/socket-publisher/chat/" + userId;

        webSocketService.connect(() -> {
            chatSubscription = webSocketService.subscribe(topic, body -> {
                try {
                    ChatMessageModel message = gson.fromJson(body, ChatMessageModel.class);
                    onMessage.onSuccess(message);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse chat message", e);
                    onMessage.onError(-1, "Parse error");
                }
            });
        });
    }

    public void sendMessage(ChatMessageModel message) {
        String json = gson.toJson(message);
        webSocketService.publish("/socket-subscriber/chat/send", json);
    }

    public void disconnectFromChat() {
        if (chatSubscription != null) {
            chatSubscription.unsubscribe();
            chatSubscription = null;
        }
    }
}