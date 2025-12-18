package model.vehicle;

public class Vehicle {
    private long id;
    private String make;
    private String model;
    private String licensePlate;
    private int seatsNumber;
    private boolean petFriendly;
    private boolean babyFriendly;
    private String color;
    private VehicleType type;

    public Vehicle() {

    }

    public Vehicle(String make, String model, String licensePlate, int seatsNumber, boolean petFriendly, boolean babyFriendly, String color, VehicleType type) {
        this.make = make;
        this.model = model;
        this.licensePlate = licensePlate;
        this.seatsNumber = seatsNumber;
        this.petFriendly = petFriendly;
        this.babyFriendly = babyFriendly;
        this.color = color;
        this.type = type;
    }

    public Vehicle(String make, String model, String licensePlate, int seatsNumber, boolean petFriendly, boolean babyFriendly, String color, VehicleType type, long id) {
        this.make = make;
        this.model = model;
        this.licensePlate = licensePlate;
        this.seatsNumber = seatsNumber;
        this.petFriendly = petFriendly;
        this.babyFriendly = babyFriendly;
        this.color = color;
        this.type = type;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getSeatsNumber() {
        return seatsNumber;
    }

    public void setSeatsNumber(int seatsNumber) {
        this.seatsNumber = seatsNumber;
    }

    public boolean isPetFriendly() {
        return petFriendly;
    }

    public void setPetFriendly(boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    public boolean isBabyFriendly() {
        return babyFriendly;
    }

    public void setBabyFriendly(boolean babyFriendly) {
        this.babyFriendly = babyFriendly;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }
}
