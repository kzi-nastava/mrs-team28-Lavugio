package com.example.lavugio_mobile.models.ride;

public class StopBaseDTO {
    private int orderIndex;
    private double latitude;
    private double longitude;

    public StopBaseDTO() {
    }

    public StopBaseDTO(int orderIndex, double latitude, double longitude) {
        this.orderIndex = orderIndex;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
