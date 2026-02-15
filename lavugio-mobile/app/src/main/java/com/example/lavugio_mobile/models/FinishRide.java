package com.example.lavugio_mobile.models;

public class FinishRide {
    private long rideId;
    private Coordinates endCoordinates;
    private String endAddress;

    public FinishRide() {}

    public long getRideId() { return rideId; }
    public void setRideId(long rideId) { this.rideId = rideId; }

    public Coordinates getEndCoordinates() { return endCoordinates; }
    public void setEndCoordinates(Coordinates endCoordinates) { this.endCoordinates = endCoordinates; }

    public String getEndAddress() { return endAddress; }
    public void setEndAddress(String endAddress) { this.endAddress = endAddress; }
}