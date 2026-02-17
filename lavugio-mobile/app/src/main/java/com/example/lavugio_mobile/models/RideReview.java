package com.example.lavugio_mobile.models;

public class RideReview {
    private long rideId;
    private int driverRating;
    private int vehicleRating;
    private String comment;

    public RideReview() {}

    public RideReview(long rideId, int driverRating, int vehicleRating, String comment) {
        this.rideId = rideId;
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.comment = comment;
    }

    public long getRideId() { return rideId; }
    public void setRideId(long rideId) { this.rideId = rideId; }

    public int getDriverRating() { return driverRating; }
    public void setDriverRating(int driverRating) { this.driverRating = driverRating; }

    public int getVehicleRating() { return vehicleRating; }
    public void setVehicleRating(int vehicleRating) { this.vehicleRating = vehicleRating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}