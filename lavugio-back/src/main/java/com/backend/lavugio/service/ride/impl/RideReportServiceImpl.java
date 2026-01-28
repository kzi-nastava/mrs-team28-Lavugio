package com.backend.lavugio.service.ride.impl;

import com.backend.lavugio.dto.ride.RideReportDTO;
import com.backend.lavugio.dto.ride.RideReportedDTO;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.ride.RideReport;
import com.backend.lavugio.model.user.Account;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.repository.ride.RideReportRepository;
import com.backend.lavugio.service.ride.RideReportService;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.user.AccountService;
import com.backend.lavugio.service.user.RegularUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class RideReportServiceImpl implements RideReportService {

    private final RideReportRepository rideReportRepository;

    private final RideService rideService;
    private final AccountService accountService;

    @Autowired
    public RideReportServiceImpl(RideReportRepository rideReportRepository, RideService rideService, AccountService accountService) {
        this.rideReportRepository = rideReportRepository;
        this.rideService = rideService;
        this.accountService = accountService;
    }

    @Override
    @Transactional
    public RideReport createReport(Long reporterId, RideReportDTO reportDTO) {
        if (reportDTO.getComment() == null || reportDTO.getComment().trim().isEmpty()) {
            throw new NoSuchElementException("Report message cannot be empty");
        }
        Ride ride = rideService.getRideById(reportDTO.getRideId());
        if  (ride == null) {
            throw new IllegalArgumentException("Ride not found");
        }
        if (ride.getPassengers().stream().map(Account::getId).noneMatch(id -> id.equals(reporterId))){
            throw new IllegalCallerException("Passenger did not participate in this ride");
        }
        if (hasReported(reporterId, reportDTO.getRideId())) {
            throw new IllegalStateException("User has already reported this ride");
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

    @Override
    public boolean hasReported(Long userId, Long rideId) {
        List<RideReport> reports =  getReportsByRideId(rideId);
        if (reports.isEmpty()) {
            return false;
        }
        for (RideReport report : reports) {
            if (report.getReporter().getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }
}