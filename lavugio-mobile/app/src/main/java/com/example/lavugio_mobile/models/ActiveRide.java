package com.example.lavugio_mobile.models;

import java.time.LocalDateTime;

public class ActiveRide {
    private long id;
    private String rideStatus;
    private String startLocation;
    private String endLocation;
    private LocalDateTime startDateTime;
    private double price;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getRideStatus() { return rideStatus; }
    public void setRideStatus(String rideStatus) { this.rideStatus = rideStatus; }

    public String getStartLocation() { return startLocation; }
    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }

    public String getEndLocation() { return endLocation; }
    public void setEndLocation(String endLocation) { this.endLocation = endLocation; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}