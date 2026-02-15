package com.example.lavugio_mobile.models;

import java.util.List;

/**
 * Matches the actual backend JSON:
 *   "rideId": 193,
 *   "startAddress": "Narodnog fronta 0 Novi Sad Serbia 0",
 *   "endAddress": "Бродарска 17 Belgrade Serbia 0",
 *   "startDate": "22:15 12.02.2026",
 *   "endDate": "22:15 12.02.2026"
 *
 * Dates come as pre-formatted strings "HH:mm dd.MM.yyyy" from the backend.
 */
public class RideHistoryDriverModel {
    private String departure;
    private String destination;
    private String start;
    private String end;

    private String status;
    private double price;
    private boolean cancelled;
    private boolean panic;
    private List<PassengerTableRow> passengers;
    private Coordinates[] checkpoints;

    public String getStartAddress() { return departure; }
    public void setStartAddress(String startAddress) { this.departure = startAddress; }

    public String getEndAddress() { return destination; }
    public void setEndAddress(String endAddress) { this.destination = endAddress; }

    public String getStartDate() { return start; }
    public void setStartDate(String startDate) { this.start = startDate; }

    public String getEndDate() { return end; }
    public void setEndDate(String endDate) { this.end = endDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    /**
     * Extracts the time portion from a date string.
     * Input:  "22:15 12.02.2026"
     * Output: "22:15"
     */
    public String getStartTime() {
        return extractTime(start);
    }

    public String getEndTime() {
        return extractTime(end);
    }

    /**
     * Extracts the date portion from a date string.
     * Input:  "22:15 12.02.2026"
     * Output: "12.02.2026"
     */
    public String getStartDateOnly() {
        return extractDate(start);
    }

    public String getEndDateOnly() {
        return extractDate(end);
    }

    private String extractTime(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        String[] parts = dateStr.split(" ", 2);
        return parts.length >= 1 ? parts[0] : "";
    }

    private String extractDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return "";
        String[] parts = dateStr.split(" ", 2);
        return parts.length >= 2 ? parts[1] : "";
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

    public Coordinates[] getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(Coordinates[] checkpoints) {
        this.checkpoints = checkpoints;
    }
}