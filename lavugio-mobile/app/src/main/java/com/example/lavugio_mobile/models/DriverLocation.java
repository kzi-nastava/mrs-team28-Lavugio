package com.example.lavugio_mobile.models;

import com.example.lavugio_mobile.models.enums.DriverStatusEnum;

public class DriverLocation {
    private Long id;
    private Coordinates location;
    private DriverStatusEnum status;

    public DriverLocation() {
    }

    public DriverLocation(Long id, Coordinates location, DriverStatusEnum status) {
        this.id = id;
        this.location = location;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Coordinates getLocation() {
        return location;
    }

    public void setLocation(Coordinates location) {
        this.location = location;
    }

    public DriverStatusEnum getStatus() {
        return status;
    }

    public void setStatus(DriverStatusEnum status) {
        this.status = status;
    }

}
