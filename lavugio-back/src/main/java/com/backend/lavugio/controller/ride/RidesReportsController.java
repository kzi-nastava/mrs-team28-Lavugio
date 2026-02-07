package com.backend.lavugio.controller.ride;

import com.backend.lavugio.dto.ride.RidesReportsAdminFiltersDTO;
import com.backend.lavugio.dto.ride.RidesReportsDateRangeDTO;
import com.backend.lavugio.dto.ride.RidesReportsResponseDTO;
import com.backend.lavugio.security.SecurityUtils;
import com.backend.lavugio.service.ride.RidesReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rides-reports")
public class RidesReportsController {

    @Autowired
    private RidesReportsService ridesReportsService;

    @PostMapping("/driver")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> generateDriverReport(@RequestBody RidesReportsDateRangeDTO dateRange) {
        Long driverId = SecurityUtils.getCurrentUserId();
        try {
            RidesReportsResponseDTO report = ridesReportsService.getRidesReportsDriver(dateRange, driverId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while generating the report: " + e.getMessage());
        }
    }

    @PostMapping("/regular-user")
    @PreAuthorize("hasRole('REGULAR_USER')")
    public ResponseEntity<?> generateRegularUserReport(@RequestBody RidesReportsDateRangeDTO dateRange) {
        Long userId = SecurityUtils.getCurrentUserId();
        try {
            RidesReportsResponseDTO report = ridesReportsService.getRidesReportsRegularUser(dateRange, userId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while generating the report: " + e.getMessage());
        }
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateAdministratorReport(@RequestBody RidesReportsAdminFiltersDTO adminFilters) {
        try {
            RidesReportsResponseDTO report = ridesReportsService.getRidesReportsAdmin(adminFilters);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while generating the report: " + e.getMessage());
        }
    }
}
