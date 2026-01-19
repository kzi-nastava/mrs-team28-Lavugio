package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.dto.ride.RideReportDTO;
import com.backend.lavugio.dto.ride.RideReportedDTO;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.ride.RideReport;
import com.backend.lavugio.repository.ride.RideReportRepository;
import com.backend.lavugio.service.ride.RideReportService;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.user.RegularUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RideReportServiceImpl implements RideReportService {

    private final RideReportRepository rideReportRepository;

    private final RideService rideService;

    private final RegularUserService regularUserService;

    @Autowired
    public RideReportServiceImpl(RideReportRepository rideReportRepository, RideService rideService,  RegularUserService regularUserService) {
        this.rideReportRepository = rideReportRepository;
        this.rideService = rideService;
        this.regularUserService = regularUserService;
    }

    @Override
    @Transactional
    public RideReport createReport(RideReportDTO reportDTO) {
        if (reportDTO.getComment() == null || reportDTO.getComment().trim().isEmpty()) {
            throw new IllegalArgumentException("Report message cannot be empty");
        }
        Ride ride = rideService.getRideById(reportDTO.getRideId());
        if  (ride == null) {
            throw new IllegalArgumentException("Ride not found");
        }
        RideReport report = new RideReport(null, ride, reportDTO.getComment(), ride.getCreator());
        return rideReportRepository.save(report);
    }

    @Override
    public RideReport getReportById(Long id) {
        return rideReportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report not found with id: " + id));
    }

    @Override
    public RideReportedDTO getReportDTOById(Long id) {
        return new RideReportedDTO(getReportById(id));
    }

    @Override
    public List<RideReport> getAllReports() {
        return rideReportRepository.findAll();
    }

    @Override
    public List<RideReport> getReportsByRideId(Long rideId) {
        Ride ride = rideService.getRideById(rideId);
        if (ride == null) {
            throw new IllegalArgumentException("Ride not found with id: " + rideId);
        }
        return rideReportRepository.findByRideId(rideId);
    }

    @Override
    public List<RideReportedDTO> getReportDTOsByRideId(Long rideId) {
        List<RideReportedDTO> reportDTOs =  new ArrayList<>();
        for (RideReport report : getReportsByRideId(rideId)) {
            reportDTOs.add(new RideReportedDTO(report));
        }
        return reportDTOs;
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