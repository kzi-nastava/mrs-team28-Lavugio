package com.backend.lavugio.controller.ride;

import com.backend.lavugio.dto.ride.RidesReportsAdminFiltersDTO;
import com.backend.lavugio.dto.ride.RidesReportsDateRangeDTO;
import com.backend.lavugio.dto.ride.RidesReportsResponseDTO;
import com.backend.lavugio.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rides-reports")
public class RidesReportsController {

    @PostMapping("/driver")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> generateDriverReport(@RequestBody RidesReportsDateRangeDTO dateRange) {
        Long driverId = SecurityUtils.getCurrentUserId();
        try {
            // Call the service method to generate the report for the driver
            RidesReportsResponseDTO report = ridesReportsService.getRidesReportsDriver(dateRange, driverId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while generating the report: " + e.getMessage());
        }
    }

    @PostMapping("/regular-user")
    @PreAuthorize("hasRole('REGULAR_USER')")
    public ResponseEntity<?> generateRegularUserReport(@RequestBody RidesReportsDateRangeDTO dateRange) {

    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateAdministratorReport(@RequestBody RidesReportsAdminFiltersDTO adminFilters) {

    }
}
