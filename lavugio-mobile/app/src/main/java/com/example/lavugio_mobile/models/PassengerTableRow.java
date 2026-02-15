package com.example.lavugio_mobile.models;

public class PassengerTableRow {
    private long id;
    private String name;
    private String passengerIconName;

    public PassengerTableRow(String passengerIconName, String name, long id) {
        this.passengerIconName = passengerIconName;
        this.name = name;
        this.id = id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassengerIconName(String passengerIconName) {
        this.passengerIconName = passengerIconName;
    }

    public String getPassengerIconName() {
        return passengerIconName;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }


}
