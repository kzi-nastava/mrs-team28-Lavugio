package model.user;

import model.vehicle.Vehicle;

public class Driver extends BlockableAccount{
    private boolean isActive;
    private Vehicle vehicle;

    public Driver() {

    }

    public Driver(boolean isActive, Vehicle vehicle) {
        this.isActive = isActive;
        this.vehicle = vehicle;
    }

    public Driver(boolean isBlocked, String blockReason, boolean isActive, Vehicle vehicle) {
        super(isBlocked, blockReason);
        this.isActive = isActive;
        this.vehicle = vehicle;
    }

    public Driver(String name, String lastName, String email, String password, String profilePhoto, boolean isBlocked, String blockReason, boolean isActive, Vehicle vehicle) {
        super(name, lastName, email, password, profilePhoto, isBlocked, blockReason);
        this.isActive = isActive;
        this.vehicle = vehicle;
    }

    public Driver(String name, String lastName, String email, String password, String profilePhoto, long id, boolean isBlocked, String blockReason, boolean isActive, Vehicle vehicle) {
        super(name, lastName, email, password, profilePhoto, id, isBlocked, blockReason);
        this.isActive = isActive;
        this.vehicle = vehicle;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
