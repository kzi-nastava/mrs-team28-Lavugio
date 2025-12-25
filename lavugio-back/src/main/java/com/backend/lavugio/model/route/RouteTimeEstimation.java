package com.backend.lavugio.model.route;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteTimeEstimation {
    private double durationSeconds;
    private double distanceMeters;

    public double getDurationMinutes() {
        return durationSeconds / 60.0;
    }

    public double getDurationHours() {
        return durationSeconds / 3600.0;
    }

    public double getDistanceKilometers() {
        return distanceMeters / 1000.0;
    }

    @Override
    public String toString() {
        return String.format("Duration: %.2f minutes (%.2f hours), Distance: %.2f km",
                getDurationMinutes(), getDurationHours(), getDistanceKilometers());
    }
}