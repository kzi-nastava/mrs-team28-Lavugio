package com.example.lavugio_mobile.models.auth;

public class VerifyEmailRequest {
    private String token;

    public VerifyEmailRequest(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}