package com.example.lavugio_mobile.models;

/**
 * Model for user ride history list item.
 * Matches the backend JSON structure for regular user ride history.
 */
public class RideHistoryUserModel {
    private Long rideId;
    private String startAddress;
    private String endAddress;
    private String startDate;   // "HH:mm dd.MM.yyyy" or ""
    private String endDate;     // "HH:mm dd.MM.yyyy" or ""

    public long getRideId() { return rideId; }
    public void setRideId(long rideId) { this.rideId = rideId; }

    public String getStartAddress() { return startAddress; }
    public void setStartAddress(String startAddress) { this.startAddress = startAddress; }

    public String getEndAddress() { return endAddress; }
    public void setEndAddress(String endAddress) { this.endAddress = endAddress; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    /**
     * Extracts the time portion from a date string.
     * Input:  "22:15 12.02.2026"
     * Output: "22:15"
     */
    public String getStartTime() {
        return extractTime(startDate);
    }

    public String getEndTime() {
        return extractTime(endDate);
    }

    /**
     * Extracts the date portion from a date string.
     * Input:  "22:15 12.02.2026"
     * Output: "12.02.2026"
     */
    public String getStartDateOnly() {
        return extractDate(startDate);
    }

    public String getEndDateOnly() {
        return extractDate(endDate);
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
}
