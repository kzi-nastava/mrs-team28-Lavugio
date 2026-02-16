package com.example.lavugio_mobile.models;

public class FinishRide {
    private long rideId;
    private Coordinates finalDestination;
    private boolean finishedEarly;

    private Double distance;

    public FinishRide() {}

    public long getRideId() { return rideId; }
    public void setRideId(long rideId) { this.rideId = rideId; }

    public Coordinates getFinalDestination() { return finalDestination; }
    public void setFinalDestination(Coordinates finalDestination) { this.finalDestination = finalDestination; }


    public boolean isFinishedEarly() {
        return finishedEarly;
    }

    public void setFinishedEarly(boolean finishedEarly) {
        this.finishedEarly = finishedEarly;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}