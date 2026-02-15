package com.example.lavugio_mobile.models.user;

import com.google.gson.annotations.SerializedName;

public class DriverActiveTimeResponse {
    @SerializedName("timeActive")
    private String timeActive;

    public String getTimeActive() {
        return timeActive;
    }

    public void setTimeActive(String timeActive) {
        this.timeActive = timeActive;
    }
}
