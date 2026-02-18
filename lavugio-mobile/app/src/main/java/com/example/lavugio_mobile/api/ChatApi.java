package com.example.lavugio_mobile.api;

import com.example.lavugio_mobile.models.ChatMessageModel;
import com.example.lavugio_mobile.models.UserChatModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ChatApi {

    /**
     * Load chat history between logged-in user and the given user ID.
     * Admins pass the user's ID; regular users pass 0 (or admin placeholder).
     */
    @GET("api/chat/history/{userId}")
    Call<List<ChatMessageModel>> getChatHistory(@Path("userId") int userId);

    /**
     * Get list of users that have ever chatted (admin only).
     */
    @GET("api/users/chattable")
    Call<List<UserChatModel>> getChattableUsers();
}