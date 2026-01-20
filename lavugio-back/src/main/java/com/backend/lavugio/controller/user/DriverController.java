package com.backend.lavugio.controller.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.*;


import com.backend.lavugio.dto.*;
import com.backend.lavugio.dto.ride.RideReportedDTO;
import com.backend.lavugio.dto.ride.ScheduledRideDTO;
import com.backend.lavugio.dto.user.*;
import com.backend.lavugio.model.enums.DriverHistorySortFieldEnum;
import com.backend.lavugio.model.enums.DriverStatusEnum;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.route.RideDestinationService;
import com.backend.lavugio.service.user.DriverRegistrationTokenService;
import org.apache.coyote.Response;
import com.backend.lavugio.service.user.DriverAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.backend.lavugio.service.user.DriverService;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {
	@Autowired
	private DriverService driverService;

    private RideService rideService;
    private RideDestinationService rideDestinationService;
    @Autowired
    private DriverRegistrationTokenService driverRegistrationTokenService;

    @Autowired
    private DriverAvailabilityService driverAvailabilityService;

    @Autowired
    public DriverController(RideService rideService, RideDestinationService rideDestinationService, DriverAvailabilityService driverAvailabilityService) {
        this.rideService = rideService;
        this.rideDestinationService = rideDestinationService;
        this.driverAvailabilityService = driverAvailabilityService;
    }
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

    @PostMapping("/activate")
    public ResponseEntity<?> activateDriver(@RequestBody DriverActivationRequestDTO request) {
        try {
            System.out.println("Activating driver activated");
            driverRegistrationTokenService.activateDriver(request.getToken(), request.getPassword());
            return ResponseEntity.ok().body(Map.of("Message", "Driver activated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /*@PostMapping("/validate-token")
    public ResponseEntity<?> validateToken() {
        //
    }*/

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
    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDriver(
            @PathVariable Long id,
            @RequestBody UpdateDriverDTO request) {
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
    public ResponseEntity<Collection<DriverHistoryDTO>> getAllDriverHistory(@PathVariable Long driverId,
                                                                            @RequestParam(defaultValue = "false") boolean ascending,
                                                                            @RequestParam(defaultValue = "START_DATE") DriverHistorySortFieldEnum sortBy,
                                                                            @RequestParam(required = false) String dateRangeStart,
                                                                            @RequestParam(required = false) String dateRangeEnd) {
//        List<Ride> rides = rideService.getFinishedRidesForDriver(driverId);
//        rideService.applyParametersToRides(rides, ascending, sortBy, dateRangeStart, dateRangeEnd);
//        List<DriverHistoryDTO> ridesDTO = new ArrayList<>();
//        for (Ride ride : rides){
//            ridesDTO.add(new DriverHistoryDTO(ride));
//        }
        List<DriverHistoryDTO> rides = new ArrayList<>();
        rides.add(new DriverHistoryDTO(1L, "Location A", "Location B", "11:23 15.03.2024", "12:01 15.03.2024"));
        rides.add(new DriverHistoryDTO(2L, "Location C", "Location D", "09:10 14.03.2024", "09:45 14.03.2024"));
        rides.add(new DriverHistoryDTO(3L, "Location E", "Location F", "14:05 13.03.2024", "14:50 13.03.2024"));
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }

    @GetMapping(value = "/{driverId}/history/{rideId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverHistoryDetailedDTO> getDriverHistoryByRideId(@PathVariable Long rideId){
//        Ride ride = rideService.getRideById(rideId);
//        List<RideDestination> destinations = rideDestinationService.getStartAndEndDestinationForRide(rideId);
//        RideDestination startDestination = destinations.get(0);
//        RideDestination endDestination = destinations.get(1);
//        DriverHistoryDetailedDTO rideDTO = new DriverHistoryDetailedDTO(ride, startDestination, endDestination);
        List<PassengerTableRowDTO> passengers = new ArrayList<>();
        passengers.add(new PassengerTableRowDTO(
                1L,
                "Marko Marković",
                new ImageDTO("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==", "image/png")
        ));
        passengers.add(new PassengerTableRowDTO(
                2L,
                "Ana Anić",
                new ImageDTO("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8DwHwAFBQIAX8jx0gAAAABJRU5ErkJggg==", "image/png")
        ));
        passengers.add(new PassengerTableRowDTO(
                3L,
                "Petar Petrović",
                new ImageDTO("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAGA+G6D9wAAAABJRU5ErkJggg==", "image/png")
        ));

        DriverHistoryDetailedDTO dto = new DriverHistoryDetailedDTO(
                "11:23 15.03.2024",
                "12:45 15.03.2024",
                "Kneza Miloša 15, Beograd",
                "Bulevar kralja Aleksandra 73, Beograd",
                1250.50,
                false,
                false,
                passengers,
                new CoordinatesDTO(44.8125, 20.4612),
                new CoordinatesDTO(44.8023, 20.4856)
        );

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

        List<ScheduledRideDTO> scheduledRides = new ArrayList<>();

        CoordinatesDTO[] checkpoints1 = {
                new CoordinatesDTO(44.7866, 20.4489),
                new CoordinatesDTO(44.8000, 20.4600)
        };

        CoordinatesDTO[] checkpoints2 = {
                new CoordinatesDTO(45.2671, 19.8335),
                new CoordinatesDTO(45.2500, 19.8200)
        };

        CoordinatesDTO[] checkpoints3 = {
                new CoordinatesDTO(43.3209, 21.8958),
                new CoordinatesDTO(43.3100, 21.9000)
        };

        scheduledRides.add(
                new ScheduledRideDTO(
                        1L,
                        "Location A",
                        "Location B",
                        LocalDateTime.of(2025, 2, 21, 10, 50),
                        checkpoints1,
                        500F,
                        RideStatus.ACTIVE,
                        true
                )
        );

        scheduledRides.add(
                new ScheduledRideDTO(
                        2L,
                        "Location C",
                        "Location D",
                        LocalDateTime.of(2025, 2, 22, 14, 30),
                        checkpoints2,
                        400F,
                        RideStatus.SCHEDULED,
                        false
                )
        );

        scheduledRides.add(
                new ScheduledRideDTO(
                        3L,
                        "Location E",
                        "Location F",
                        LocalDateTime.of(2025, 2, 23, 9, 15),
                        checkpoints3,
                        600F,
                        RideStatus.SCHEDULED,
                        false
                )
        );

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
