package com.backend.lavugio.controller.driver;

import com.backend.lavugio.dto.RideReportedDTO;
import com.backend.lavugio.model.ride.RideReport;

import com.backend.lavugio.service.ride.RideReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/drivers/{driverId}/reports")
public class DriverReportController {
    private final RideReportService rideReportService;

    @Autowired
    public DriverReportController(RideReportService rideReportService) {
        this.rideReportService = rideReportService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<RideReportedDTO>> getDriverReports(@PathVariable Long driverId){
//        Collection<RideReport> reports = rideReportService.getReportsForDriver(driverId);
//        Collection<RideReportedDTO> reportDTOs = new ArrayList<>();
//        for (RideReport report : reports) {
//            RideReportedDTO rideReportedDTO = new RideReportedDTO();
//            rideReportedDTO.setReporterId(report.getReporter().getId());
//            rideReportedDTO.setReportId(report.getReportId());
//            rideReportedDTO.setReportText(report.getReportMessage());
//            reportDTOs.add(rideReportedDTO);
//        }
        List<RideReportedDTO> reportDTOs = new ArrayList<>();
        reportDTOs.add(new RideReportedDTO(1L, 1L, 2L, "Driver was rude"));
        reportDTOs.add(new RideReportedDTO(2L, 1L, 3L, "Driver took a longer route"));
        return new ResponseEntity<>(reportDTOs, HttpStatus.OK);
    }

}
