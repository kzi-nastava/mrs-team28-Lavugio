package com.backend.lavugio.controller.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.*;


import com.backend.lavugio.dto.*;
import com.backend.lavugio.dto.ride.RideReportedDTO;
import com.backend.lavugio.dto.ride.ScheduledRideDTO;
import com.backend.lavugio.dto.user.*;
import com.backend.lavugio.model.enums.DriverHistorySortFieldEnum;
import com.backend.lavugio.model.enums.DriverStatusEnum;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.DriverUpdateRequest;
import com.backend.lavugio.security.JwtUtil;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.ride.ScheduledRideService;
import com.backend.lavugio.service.route.RideDestinationService;
import com.backend.lavugio.service.user.DriverRegistrationTokenService;
import com.backend.lavugio.service.utils.DateTimeParserService;
import com.backend.lavugio.service.user.DriverAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.backend.lavugio.service.user.DriverService;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {
	@Autowired
	private DriverService driverService;
    @Autowired
    private RideService rideService;
    @Autowired
    private RideDestinationService rideDestinationService;
    @Autowired
    private DriverRegistrationTokenService driverRegistrationTokenService;
    @Autowired
    private DriverAvailabilityService driverAvailabilityService;
    @Autowired
    private ScheduledRideService  scheduledRideService;

    @Autowired
    private DateTimeParserService dateTimeParserService;

 // ========== REGISTRATION ==========
    
    @PostMapping("/register")
    public ResponseEntity<?> registerDriver(@RequestBody DriverRegistrationDTO request) {
        try {
            DriverDTO driver = driverService.register(request);
            driverRegistrationTokenService.sendActivationEmail(driver.getId(), driver.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Driver registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ========== CRUD OPERATIONS ==========
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getDriver(@PathVariable Long id) {
        try {
            DriverDTO driver = driverService.getDriverDTOById(id);
            return ResponseEntity.ok(driver);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getDriverByEmail(@PathVariable String email) {
        try {
            DriverDTO driver = driverService.getDriverDTOByEmail(email);
            return ResponseEntity.ok(driver);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllDrivers() {
        try {
            List<DriverDTO> drivers = driverService.getAllDriversDTO();
            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableDrivers() {
        try {
            List<DriverDTO> drivers = driverService.getAvailableDriversDTO();
            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    
    @PostMapping("/{driverId}/status")
    public ResponseEntity<?> changeDriverStatus(
            @PathVariable Long driverId,
            @RequestBody Map<String, Boolean> request) {
        try {
            Boolean active = request.get("active");
            if (active == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Active status is required"));
            }
            
            driverService.setDriverStatus(driverId, active);
            
            return ResponseEntity.ok(Map.of(
                "message", "Driver status updated successfully",
                "active", active
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    @GetMapping("/{driverId}/can-logout")
    public ResponseEntity<?> canDriverLogout(@PathVariable Long driverId) {
        try {
            boolean canLogout = driverService.canDriverLogout(driverId);
            return ResponseEntity.ok(Map.of(
                "canLogout", canLogout,
                "message", canLogout ? "Driver can logout" : "Driver has an active ride"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDriver(
            @PathVariable Long id,
            @RequestBody AccountUpdateDriverDTO request) {
    	// @AuthenticationPrincipal UserDetails userDetails
        try {
            //String currentEmail = userDetails.getUsername();
            String currentEmail = "currentEmail"; // Placeholder for current user's email
            // DriverDTO driver = driverService.updateDriverDTO(id, request, currentEmail);
            return ResponseEntity.ok("Deprecated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/edit-request")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> createDriverEditRequest(
            @RequestBody DriverUpdateRequestDTO request) {
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        Long creatorId = JwtUtil.extractAccountId(authentication);
        try {
            driverService.createDriverEditRequest(request, creatorId);
            return ResponseEntity.ok().body(Map.of("message","Driver edit request created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/edit-requests")
    public ResponseEntity<?> getDriverEditRequests() {
        try {
            List<DriverUpdateRequestDiffDTO> requests = driverService.getAllPendingDriverEditRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/edit-requests/{requestId}/approve")
    public ResponseEntity<?> approveEditRequest(@PathVariable Long requestId) {
        try {
            this.driverService.approveEditRequest(requestId);
            return ResponseEntity.ok().body(Map.of("message", "Edit request successfully approved."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/edit-requests/{requestId}/reject")
    public ResponseEntity<?> rejectEditRequest(@PathVariable Long requestId) {
        try {
            this.driverService.rejectEditRequest(requestId);
            return ResponseEntity.ok().body(Map.of("message", "Edit request successfully rejected."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDriver(
            @PathVariable Long id) {
    	// @AuthenticationPrincipal UserDetails userDetails
        try {
            // String currentEmail = userDetails.getUsername();
            driverService.deleteDriver(id);
            return ResponseEntity.ok("Driver deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PROFILE ENDPOINT
    
    @GetMapping("/profile")
    public ResponseEntity<?> getDriverProfile() {
    	// @AuthenticationPrincipal UserDetails userDetails
        try {
            // String email = userDetails.getId();
            String email = "currentEmail"; // Placeholder for current user's email
            DriverDTO profile = driverService.getDriverProfile(email);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getDriverProfileById(@PathVariable Long id) {
        try {
            DriverDTO profile = driverService.getDriverProfileById(id);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // RATING ENDPOINTS
    
    @PostMapping("/{id}/rate")
    public ResponseEntity<?> rateDriver(
            @PathVariable Long id) {
    	// @AuthenticationPrincipal UserDetails userDetails
        try {
            // String userEmail = userDetails.getUsername();
            // RatingDTO rating = ratingService.rateDriver(id, request, userEmail);
            String rating = "5 stars"; // Placeholder for rating result
            return ResponseEntity.ok(rating);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}/ratings")
    public ResponseEntity<?> getDriverRatings(@PathVariable Long id) {
        try {
            // List<RatingDTO> ratings = ratingService.getDriverRatings(id);
            List<String> ratings = List.of("5 stars", "4 stars"); // Placeholder for ratings list
            return ResponseEntity.ok(ratings);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    // ACTIVATION ENDPOINT

    @PostMapping("/activate-account")
    public ResponseEntity<?> activateDriverAccount(
            @RequestBody DriverActivationRequestDTO request) {
        try {
            System.out.println("Activating driver activated");
            driverRegistrationTokenService.activateDriver(request.getToken(), request.getPassword());
            return ResponseEntity.ok().body(Map.of("message", "Driver activated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateCurrentDriver() {
        try {
            Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
            Long accountId = JwtUtil.extractAccountId(authentication);
            Driver driver = driverService.activateDriver(accountId);
            System.out.println("Driver ID:" + accountId + " activated in controller");
            return ResponseEntity.ok().body(Map.of("message", "Driver activated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateCurrentDriver() {
        try {
            Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
            Long accountId = JwtUtil.extractAccountId(authentication);
            Driver driver = driverService.deactivateDriver(accountId);
            System.out.println("Driver ID:" + accountId + " deactivated in controller");
            return ResponseEntity.ok().body(Map.of("message", "Driver deactivated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activateDriver(
            @PathVariable Long id) {
    	// @AuthenticationPrincipal UserDetails userDetails
        try {
            DriverStatusDTO status = driverService.activateDriverDTO(id);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateDriver(
            @PathVariable Long id) {
    	// @AuthenticationPrincipal UserDetails userDetails
        try {
            DriverStatusDTO status = driverService.deactivateDriverDTO(id);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}/status")
    public ResponseEntity<?> getDriverStatus(@PathVariable Long id) {
        try {
            DriverStatusDTO status = driverService.getDriverStatusDTO(id);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/activity")
    public ResponseEntity<?> getDriverActivity(@PathVariable Long id) {
        try {
            DriverStatusDTO activity = driverService.getDriverActivityDTO(id);
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/{driverId}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverHistoryPagingDTO> getAllDriverHistory(
            @PathVariable Long driverId,
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(defaultValue = "DESC") String sorting,
            @RequestParam(defaultValue = "START_DATE") DriverHistorySortFieldEnum sortBy,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {

//        // MOCK PODACI (glume bazu)
//        List<DriverHistoryDTO> allRides = new ArrayList<>();
//        for (int i = 0; i < 1000; i++) {
//            allRides.add(new DriverHistoryDTO(
//                    (long) i,
//                    "Location A" + i,
//                    "Location B",
//                    "11:23 15.03.2024",
//                    "12:01 15.03.2024"
//            ));
//        }
//
//        int totalElements = allRides.size();
//
//        int fromIndex = page * pageSize;
//        int toIndex = Math.min(fromIndex + pageSize, totalElements);
//
//        List<DriverHistoryDTO> pageContent = new ArrayList<>();
//
//        if (fromIndex < totalElements) {
//            pageContent = allRides.subList(fromIndex, toIndex);
//        }
//
//        boolean reachedEnd = toIndex >= totalElements;
//
//        DriverHistoryPagingDTO pagingDTO = new DriverHistoryPagingDTO();
//        pagingDTO.setDriverHistory(pageContent.toArray(new DriverHistoryDTO[0]));
//        pagingDTO.setTotalElements((long) totalElements);
//        pagingDTO.setReachedEnd(reachedEnd);

        LocalDateTime start = dateTimeParserService.parseStartOfDay(startDate);
        LocalDateTime end = dateTimeParserService.parseEndOfDay(endDate);

        DriverHistoryPagingDTO dto = rideService.getDriverHistory(
                driverId,
                start,
                end,
                sortBy.toString(),
                sorting,
                pageSize,
                page
        );

        return ResponseEntity.ok(dto);
    }


    @GetMapping(value = "/history/{rideId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverHistoryDetailedDTO> getDriverHistoryByRideId(@PathVariable Long rideId){
//        Ride ride = rideService.getRideById(rideId);
//        List<RideDestination> destinations = rideDestinationService.getStartAndEndDestinationForRide(rideId);
//        RideDestination startDestination = destinations.get(0);
//        RideDestination endDestination = destinations.get(1);
//        DriverHistoryDetailedDTO rideDTO = new DriverHistoryDetailedDTO(ride, startDestination, endDestination);
//        List<PassengerTableRowDTO> passengers = new ArrayList<>();
//        passengers.add(new PassengerTableRowDTO(
//                1L,
//                "Marko Marković",
//                "imageUrl"
//        ));
//        passengers.add(new PassengerTableRowDTO(
//                2L,
//                "Ana Anić",
//                "imageUrl"
//        ));
//        passengers.add(new PassengerTableRowDTO(
//                3L,
//                "Petar Petrović",
//                "imageUrl"
//        ));
//        passengers.add(new PassengerTableRowDTO(4L,
//                "Petar Petrović",
//                "user_2_1768848779634.jpg"
//        ));
//        passengers.add(new PassengerTableRowDTO(
//                5L,
//                "Petar Petrović",
//                "default_avatar_photo.jpg"
//        ));
//
//        DriverHistoryDetailedDTO dto = new DriverHistoryDetailedDTO(
//                "11:23 15.03.2024",
//                "12:45 15.03.2024",
//                "Kneza Miloša 15, Beograd",
//                "Bulevar kralja Aleksandra 73, Beograd",
//                1250.50,
//                true,
//                false,
//                passengers,
//                new CoordinatesDTO[]{new CoordinatesDTO(44.8125, 20.4612),
//                                    new CoordinatesDTO(44.8023, 20.4856)}
//        );

        DriverHistoryDetailedDTO dto = rideService.getDriverHistoryDetailed(rideId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(value = "/{driverId}/reports", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(
            value = "/{driverId}/scheduled-rides",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Collection<ScheduledRideDTO>> getAllScheduledRides(
            @PathVariable Long driverId
    ) {
        System.out.println("Getting scheduled rides for driver ID: " + driverId);
        List<ScheduledRideDTO> scheduledRides = scheduledRideService.getScheduledRidesForDriver(driverId);
        return ResponseEntity.ok(scheduledRides);
    }

    @GetMapping(value = "/locations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<DriverLocationDTO>> getDriverLocations() {
        try {
            List<DriverLocationDTO> locationsDTO = driverAvailabilityService.getDriverLocationsDTO();
            return new ResponseEntity<>(locationsDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{driverId}/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverLocationDTO> getDriverLocation(@PathVariable Long driverId) {
        try {
            DriverLocationDTO driverLocation = driverAvailabilityService.getDriverLocationDTO(driverId);
            return new ResponseEntity<>(driverLocation, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(new DriverLocationDTO(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(new DriverLocationDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/{driverId}/activate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverLocationDTO> activateDriver(@PathVariable Long driverId,
                                                            @RequestBody CoordinatesDTO coordinates) {
//        DriverLocation status;
//        try{
//            status = driverService.activateDriver(driverId, longitude, latitude);
//        }catch (RuntimeException e){
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
        DriverLocationDTO activatedStatus = new DriverLocationDTO(driverId, coordinates, DriverStatusEnum.AVAILABLE);
        return new ResponseEntity<>(activatedStatus, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{driverId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverLocationDTO> updateDriver(@PathVariable Long driverId,
                                                          @RequestBody UpdateDriverStatusDTO driverStatusDTO) {
//        DriverLocation status;
//        try{
//            status = driverService.updateDriverLocation(driverId, longitude, latitude);
//        } catch (RuntimeException e){
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
        DriverLocationDTO updatedStatus = new DriverLocationDTO(driverId,
                driverStatusDTO.getDriverLocation(), DriverStatusEnum.AVAILABLE);
        return new ResponseEntity<>(updatedStatus, HttpStatus.OK);
    }
}
