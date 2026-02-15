package com.example.lavugio_mobile.models;

import java.time.LocalDateTime;
import java.util.List;

public class RideHistoryDriverDetailedModel {
    private String start;
    private String end;
    private String departure;

    private String destination;

    private double price;

    private boolean cancelled;

    private boolean panic;

    private List<PassengerTableRow> passengers;

    private List<Coordinates> checkpoints;


    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isPanic() {
        return panic;
    }

    public void setPanic(boolean panic) {
        this.panic = panic;
    }

    public List<PassengerTableRow> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerTableRow> passengers) {
        this.passengers = passengers;
    }

    public List<Coordinates> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(List<Coordinates> checkpoints) {
        this.checkpoints = checkpoints;
    }
}