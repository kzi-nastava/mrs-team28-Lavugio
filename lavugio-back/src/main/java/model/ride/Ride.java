package model.ride;

import model.route.Route;

import java.time.LocalDate;
import java.time.LocalTime;

public class Ride {
    private long id;
    private Route destinations;
    private LocalDate date;
    private LocalTime timeStart;
    private LocalTime timeEnd;
    private float price;
    private float distance;
    private boolean isCancelled;
    private RideStatus rideStatus;

}
