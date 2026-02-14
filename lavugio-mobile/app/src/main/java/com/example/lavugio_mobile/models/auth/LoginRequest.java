package com.example.lavugio_mobile.models.auth;

public class LoginRequest {
    private String email;
    private String password;
    private Double longitude;
    private Double latitude;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public LoginRequest(String email, String password, Double longitude, Double latitude) {
        this.email = email;
        this.password = password;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Getters & Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
}