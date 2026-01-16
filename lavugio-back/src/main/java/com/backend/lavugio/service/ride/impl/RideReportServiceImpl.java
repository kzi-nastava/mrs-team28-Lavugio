package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.model.ride.RideReport;
import com.backend.lavugio.repository.ride.RideReportRepository;
import com.backend.lavugio.service.ride.RideReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RideReportServiceImpl implements RideReportService {

    private final RideReportRepository rideReportRepository;

    @Override
    @Transactional
    public RideReport createReport(RideReport report) {
        if (report.getRide() == null) {
            throw new IllegalArgumentException("Ride cannot be null");
        }
        if (report.getReportMessage() == null || report.getReportMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Report message cannot be empty");
        }

        return rideReportRepository.save(report);
    }

    @Override
    public RideReport getReportById(Long id) {
        return rideReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with id: " + id));
    }

    @Override
    public List<RideReport> getAllReports() {
        return rideReportRepository.findAll();
    }

    @Override
    public List<RideReport> getReportsByRideId(Long rideId) {
        return rideReportRepository.findByRideId(rideId);
    }

    @Override
    public List<RideReport> getReportsForDriver(Long driverId) {
        return rideReportRepository.findAllReportsForDriver(driverId);
    }

    @Override
    public long countReportsForRide(Long rideId) {
        return rideReportRepository.countByRideId(rideId);
    }

    @Override
    public long countReportsForDriver(Long driverId) {
        return rideReportRepository.countReportsForDriver(driverId);
    }

    @Override
    @Transactional
    public void deleteReport(Long id) {
        if (!rideReportRepository.existsById(id)) {
            throw new RuntimeException("Report not found with id: " + id);
        }
        rideReportRepository.deleteById(id);
    }
}