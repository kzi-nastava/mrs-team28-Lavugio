package com.backend.lavugio.service.ride;

import com.backend.lavugio.dto.ride.RideReportDTO;
import com.backend.lavugio.dto.ride.RideReportedDTO;
import com.backend.lavugio.model.ride.RideReport;
import java.util.List;

public interface RideReportService {
    RideReport createReport(RideReportDTO report);
    RideReport getReportById(Long id);
    RideReportedDTO getReportDTOById(Long id);
    List<RideReport> getAllReports();
    List<RideReport> getReportsByRideId(Long rideId);
    List<RideReport> getReportsForDriver(Long driverId);
    List<RideReportedDTO> getReportDTOsByRideId(Long rideId);
    long countReportsForRide(Long rideId);
    long countReportsForDriver(Long driverId);
    void deleteReport(Long id);
    boolean hasReported(Long userId, Long rideId);
}