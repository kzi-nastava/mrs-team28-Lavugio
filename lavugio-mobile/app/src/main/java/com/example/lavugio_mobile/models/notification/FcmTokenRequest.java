package com.example.lavugio_mobile.models.notification;

public class FcmTokenRequest {
    private String token;

    public FcmTokenRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
