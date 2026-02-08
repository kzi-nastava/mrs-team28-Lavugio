package com.backend.lavugio.controller.user;

import com.backend.lavugio.dto.user.AdminHistoryDetailedDTO;
import com.backend.lavugio.dto.user.AdminHistoryPagingDTO;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.utils.DateTimeParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private RideService rideService;

    @Autowired
    private DateTimeParserService dateTimeParserService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/user-history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminHistoryPagingDTO> getUserHistory(
            @RequestParam String email,
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(defaultValue = "DESC") String sorting,
            @RequestParam(defaultValue = "START") String sortBy,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        LocalDateTime start = dateTimeParserService.parseStartOfDay(startDate);
        LocalDateTime end = dateTimeParserService.parseEndOfDay(endDate);

        AdminHistoryPagingDTO dto = rideService.getAdminHistory(
                email,
                start,
                end,
                sortBy,
                sorting,
                pageSize,
                page
        );

        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/user-history/{rideId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminHistoryDetailedDTO> getRideDetails(@PathVariable Long rideId) {
        try {
            AdminHistoryDetailedDTO dto = rideService.getAdminHistoryDetailed(rideId);
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
