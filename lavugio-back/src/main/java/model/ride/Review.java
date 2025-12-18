package model.ride;

public class Review {
    private long id;
    private int carRating;
    private int driverRating;
    private String comment;

    public Review() {

    }

    public Review(int carRating, int driverRating, String comment) {
        this.carRating = carRating;
        this.driverRating = driverRating;
        this.comment = comment;
    }

    public Review(long id, int carRating, int driverRating, String comment) {
        this.id = id;
        this.carRating = carRating;
        this.driverRating = driverRating;
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCarRating() {
        return carRating;
    }

    public void setCarRating(int carRating) {
        this.carRating = carRating;
    }

    public int getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(int driverRating) {
        this.driverRating = driverRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
