package com.backend.lavugio.service.ride;

import com.backend.lavugio.model.ride.RideReport;
import java.util.List;

public interface RideReportService {
    RideReport createReport(RideReport report);
    RideReport getReportById(Long id);
    List<RideReport> getAllReports();
    List<RideReport> getReportsByRideId(Long rideId);
    List<RideReport> getReportsForDriver(Long driverId);
    long countReportsForRide(Long rideId);
    long countReportsForDriver(Long driverId);
    void deleteReport(Long id);
}