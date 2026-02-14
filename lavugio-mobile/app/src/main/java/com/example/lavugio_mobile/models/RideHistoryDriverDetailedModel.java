package com.example.lavugio_mobile.models;

import java.time.LocalDateTime;
import java.util.List;

public class RideHistoryDriverDetailedModel {
    private long rideId;
    private String status;
    private String startAddress;
    private String endAddress;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double price;
    private String passengerName;
    private List<Coordinates> checkpoints;

    public long getRideId() { return rideId; }
    public void setRideId(long rideId) { this.rideId = rideId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStartAddress() { return startAddress; }
    public void setStartAddress(String startAddress) { this.startAddress = startAddress; }

    public String getEndAddress() { return endAddress; }
    public void setEndAddress(String endAddress) { this.endAddress = endAddress; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public List<Coordinates> getCheckpoints() { return checkpoints; }
    public void setCheckpoints(List<Coordinates> checkpoints) { this.checkpoints = checkpoints; }
}