package com.backend.lavugio.controller;

import com.backend.lavugio.dto.RideReportDTO;
import com.backend.lavugio.dto.RideReportedDTO;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.ride.RideReport;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.service.ride.RideReportService;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.user.RegularUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/api/rides/{rideId}/reports")
public class RideReportController {
    private final RideReportService rideReportService;
    private final RideService rideService;
    private final RegularUserService regularUserService;

    @Autowired
    public RideReportController(RideReportService rideReportService,  RideService rideService,  RegularUserService regularUserService) {
        this.rideReportService = rideReportService;
        this.rideService = rideService;
        this.regularUserService = regularUserService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<RideReportedDTO>> getRideReports(@PathVariable Long rideId){
        Collection<RideReport> reports = rideReportService.getReportsByRideId(rideId);
        Collection<RideReportedDTO> reportDTOs = new ArrayList<>();
        for (RideReport report : reports) {
            RideReportedDTO rideReportedDTO = new RideReportedDTO();
            rideReportedDTO.setReporterId(report.getReporter().getId());
            rideReportedDTO.setReportId(report.getReportId());
            rideReportedDTO.setReportText(report.getReportMessage());
            reportDTOs.add(rideReportedDTO);
        }
        return new ResponseEntity<>(reportDTOs, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideReportedDTO> postRideReport(@PathVariable Long rideId, @RequestBody RideReportDTO reportDTO){
        RideReport report = new RideReport();
        Ride ride = rideService.getRideById(rideId);
        RegularUser user =  regularUserService.getRegularUserById(reportDTO.getReporterId());
        report.setRide(ride);
        report.setReportMessage(reportDTO.getReportText());
        report.setReporter(user);
        rideReportService.createReport(report);

        RideReportedDTO rideReportedDTO = new RideReportedDTO();
        rideReportedDTO.setReporterId(report.getReporter().getId());
        rideReportedDTO.setReportId(report.getReportId());
        rideReportedDTO.setReportText(report.getReportMessage());
        return new ResponseEntity<>(rideReportedDTO, HttpStatus.OK);
    }
}
