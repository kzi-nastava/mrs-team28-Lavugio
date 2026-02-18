package com.example.lavugio_mobile.services.auth;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lavugio_mobile.models.auth.LoginResponse;
import com.google.gson.Gson;

public class SessionManager {

    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "authToken";
    private static final String KEY_USER = "currentUser";

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // ── Token ────────────────────────────────────────────

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public boolean hasToken() {
        return getToken() != null;
    }

    // ── User ─────────────────────────────────────────────

    public void saveUser(LoginResponse user) {
        prefs.edit().putString(KEY_USER, gson.toJson(user)).apply();
    }

    public LoginResponse getUser() {
        String json = prefs.getString(KEY_USER, null);
        if (json == null) return null;
        return gson.fromJson(json, LoginResponse.class);
    }

    public Integer getUserId() {
        LoginResponse user = getUser();
        return user != null ? user.getUserId() : null;
    }

    public String getUserRole() {
        LoginResponse user = getUser();
        return user != null ? user.getRole() : null;
    }

    public void clear() {
        prefs.edit().remove(KEY_TOKEN).remove(KEY_USER).apply();
    }
}