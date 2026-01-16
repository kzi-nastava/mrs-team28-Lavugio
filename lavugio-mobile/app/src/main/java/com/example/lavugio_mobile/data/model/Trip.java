package com.example.lavugio_mobile.data.model;

public class Trip {
    public String id;
    public String startDate;
    public String endDate;
    public String startTime;
    public String endTime;
    public String departure;
    public String destination;

    public Trip(String id, String startDate, String endDate, String startTime,
         String endTime, String departure, String destination) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.departure = departure;
        this.destination = destination;
    }
}