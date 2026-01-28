package com.backend.lavugio.controller.ride;

import com.backend.lavugio.dto.*;
import com.backend.lavugio.dto.ride.*;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.ride.RideReport;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.security.JwtUtil;
import com.backend.lavugio.service.notification.PanicNotificationWebSocketService;
import com.backend.lavugio.service.notification.NotificationService;
import com.backend.lavugio.service.ride.ReviewService;
import com.backend.lavugio.service.ride.RideCompletionService;
import com.backend.lavugio.service.ride.RideOverviewService;
import com.backend.lavugio.service.ride.RideReportService;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.user.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    private final DriverService driverService;

    private final RideReportService rideReportService;

    private final ReviewService reviewService;
    private final RideOverviewService rideOverviewService;

    private final RideCompletionService  rideCompletionService;

    private final PanicNotificationWebSocketService panicWebSocketService;

    private final NotificationService notificationService;

    @Autowired
    public RideController(RideService rideService,
                          DriverService driverService,
                          RideReportService rideReportService,
                          ReviewService reviewService,
                          RideCompletionService rideCompletionService,
                          RideOverviewService rideOverviewService,
                          PanicNotificationWebSocketService panicWebSocketService,
                          NotificationService notificationService){
        this.rideService = rideService;
        this.driverService = driverService;
        this.rideReportService = rideReportService;
        this.reviewService = reviewService;
        this.rideCompletionService = rideCompletionService;
        this.rideOverviewService = rideOverviewService;
        this.panicWebSocketService = panicWebSocketService;
        this.notificationService = notificationService;
    }

    @PostMapping("/estimate-price")
    public ResponseEntity<?> estimateRidePrice(@Valid @RequestBody RideEstimateRequestDTO request) {
        try {
            double price = rideService.estimateRidePrice(request);
            return ResponseEntity.ok(price);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. Create scheduled ride (for future)
    @PostMapping("/find-ride")
    public ResponseEntity<?> findRide(
            @Valid @RequestBody RideRequestDTO request) {
        Authentication authentication = (Authentication) SecurityContextHolder.getContext().getAuthentication();
        Long creatorId = JwtUtil.extractAccountId(authentication);
        System.out.println("Trying to find ride for account with id: " + creatorId);
        try {
            RideResponseDTO ride;
            if (request.isScheduled()) {
                ride = rideService.createScheduledRide(creatorId, request);
            } else {
                System.out.println("Creating instant ride");
                ride = rideService.createInstantRide(creatorId, request);
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(ride);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getRegularUserRides() {
        // @AuthenticationPrincipal UserDetails userDetails
        Long idPlaceholder = 1L;
        try {
            List<Ride> rides = rideService.getRidesByPassengerId(idPlaceholder);
            // KONVERTOVATI U DTO
            return ResponseEntity.ok(rides);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRideDetails(@PathVariable Long id) {
        Ride ride = rideService.getRideById(id);
        // IMPLEMENTIRATI DTO
        return ResponseEntity.ok(ride);
    }

    @GetMapping("/user/active")
    public ResponseEntity<?> getUserActiveRides() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = JwtUtil.extractAccountId(authentication);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Get both ACTIVE and SCHEDULED rides
            List<Ride> activeRides = rideService.getRidesByCreatorAndStatus(userId, RideStatus.ACTIVE);
            List<Ride> scheduledRides = rideService.getRidesByCreatorAndStatus(userId, RideStatus.SCHEDULED);
            
            // Combine both lists
            List<Ride> allRides = new ArrayList<>();
            allRides.addAll(activeRides);
            allRides.addAll(scheduledRides);
            
            List<Map<String, Object>> rideMaps = new ArrayList<>();
            for (Ride ride : allRides) {
                Map<String, Object> rideMap = new HashMap<>();
                rideMap.put("id", ride.getId());
                rideMap.put("rideStatus", ride.getRideStatus().toString());
                rideMap.put("startDateTime", ride.getStartDateTime().toString());
                rideMap.put("endDateTime", ride.getEndDateTime() != null ? ride.getEndDateTime().toString() : null);
                rideMap.put("price", ride.getPrice());
                rideMap.put("distance", ride.getDistance());
                rideMap.put("hasPanic", ride.isHasPanic());
                rideMap.put("startLocation", ride.getStartAddress());
                rideMap.put("endLocation", ride.getEndAddress());
                rideMaps.add(rideMap);
            }
            
            return ResponseEntity.ok(rideMaps);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/active-full")
    public ResponseEntity<?> getUserActiveRidesFull() {
        try {
            System.out.println("=== GET USER ACTIVE RIDES START ===");
            // Get authenticated user ID from SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = JwtUtil.extractAccountId(authentication);
            System.out.println("User ID: " + userId);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // Get all ACTIVE rides where user is the creator
            System.out.println("Fetching rides from service...");
            List<Ride> activeRides = rideService.getRidesByCreatorAndStatus(userId, RideStatus.ACTIVE);
            System.out.println("Found " + activeRides.size() + " active rides");
            
            // Convert to Maps to avoid any serialization issues
            System.out.println("Starting map conversion...");
            List<Map<String, Object>> rideMaps = new ArrayList<>();
            for (int i = 0; i < activeRides.size(); i++) {
                Ride ride = activeRides.get(i);
                System.out.println("Converting ride " + (i+1) + " - ID: " + ride.getId());
                Map<String, Object> rideMap = new HashMap<>();
                rideMap.put("id", ride.getId());
                rideMap.put("rideStatus", ride.getRideStatus().toString());
                rideMap.put("startDateTime", ride.getStartDateTime().toString());
                rideMap.put("endDateTime", ride.getEndDateTime() != null ? ride.getEndDateTime().toString() : null);
                rideMap.put("price", ride.getPrice());
                rideMap.put("distance", ride.getDistance());
                rideMap.put("hasPanic", ride.isHasPanic());
                System.out.println("Getting start address for ride " + ride.getId());
                String startAddr = ride.getStartAddress();
                System.out.println("Start address: " + startAddr);
                rideMap.put("startLocation", startAddr);
                System.out.println("Getting end address for ride " + ride.getId());
                String endAddr = ride.getEndAddress();
                System.out.println("End address: " + endAddr);
                rideMap.put("endLocation", endAddr);
                System.out.println("Ride " + ride.getId() + " converted successfully");
                rideMaps.add(rideMap);
            }
            System.out.println("All maps created. Returning response...");
            
            ResponseEntity<?> response = ResponseEntity.ok(rideMaps);
            System.out.println("Response entity created. About to return...");
            return response;
        } catch (Exception e) {
            System.err.println("ERROR in getUserActiveRides: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping(value="/{rideId}/reports", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<RideReportedDTO>> getRideReports(@PathVariable Long rideId){
        try{
            List<RideReportedDTO> reportDTOs = rideReportService.getReportDTOsByRideId(rideId);
            return new ResponseEntity<>(reportDTOs, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            return new  ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{rideId}/overview", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRideStatus(@PathVariable Long rideId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = JwtUtil.extractAccountId(authentication);
            
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }
            
            RideOverviewDTO overviewDTO = rideOverviewService.getRideOverviewDTO(rideId, userId);
            return new ResponseEntity<>(overviewDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/overviews", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<RideOverviewDTO>> getRideOverviews() {

        List<RideOverviewDTO> statuses = new ArrayList<>();

        statuses.add(new RideOverviewDTO(
                1L,
                1L,
                500,
                new CoordinatesDTO[]{new CoordinatesDTO(30.2861, 19.8017),
                new CoordinatesDTO(30.2750, 19.8200)},
                RideStatus.ACTIVE,
                "Petar Petrović",
                "Nemanjina 4",
                "Knez Mihailova 12",
                LocalDateTime.of(2026, 1, 8, 18, 30),
                null, // arrivalTime još ne postoji
                false,
                false,
                false // hasPanic
        ));

        statuses.add(new RideOverviewDTO(
                2L,
                1L,
                500,
                new CoordinatesDTO[]{new CoordinatesDTO(31.2861, 20.8017),
                new CoordinatesDTO(31.2750, 20.8200)},
                RideStatus.FINISHED,
                "Marko Marković",
                "Bulevar Oslobođenja 88",
                "Aerodrom Nikola Tesla",
                LocalDateTime.of(2026, 1, 8, 17, 10),
                LocalDateTime.of(2026, 1, 8, 17, 45),
                false,
                false,
                false // hasPanic
        ));

        statuses.add(new RideOverviewDTO(
                3L,
                null,
                500,
                new CoordinatesDTO[]{new CoordinatesDTO(32.2861, 21.8017),
                new CoordinatesDTO(32.2750, 21.8200)},
                com.backend.lavugio.model.enums.RideStatus.SCHEDULED,
                null,
                "Zmaj Jovina 15",
                "Studentski trg",
                LocalDateTime.of(2026, 1, 8, 19, 5),
                null,
                false,
                false,
                false // hasPanic
        ));

        return ResponseEntity.ok(statuses);
    }

    @GetMapping(value = "/{rideId}/reviews", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRideReviews(@PathVariable Long rideId){
        try{
            List<GetRideReviewDTO> reviews = this.reviewService.getRideReviewDTOsByRideId(rideId);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelRide(@PathVariable Long id) {
        // IMPLEMENTIRATI KADA SE URADI AUTENTIFIKACIJA LOGOVANOG KORISNIKA
        // @AuthenticationPrincipal UserDetails userDetails
        // String userEmail = userDetails.getUsername();
        try {
            rideService.cancelRide(id);
            return ResponseEntity.ok("Ride cancelled successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activateRide(@PathVariable Long id) {
        // IMPLEMENTIRATI KADA SE URADI AUTENTIFIKACIJA LOGOVANOG KORISNIKA
        // @AuthenticationPrincipal UserDetails userDetails
        // String userEmail = userDetails.getUsername();
        rideService.updateRideStatus(id, RideStatus.ACTIVE);
        return ResponseEntity.ok("Ride activated successfully");
    }

    @PostMapping("/{id}/panic")
    @Transactional
    public ResponseEntity<?> panicRide(@PathVariable Long id, @RequestBody PanicNotificationDTO panicAlert) {
        try {
            // Get ride details
            Ride ride = rideService.getRideById(id);
            
            // Validate ride is ACTIVE
            if (ride.getRideStatus() != RideStatus.ACTIVE) {
                return ResponseEntity.badRequest().body("Panic can only be triggered on active rides");
            }
            
            // Get passenger names
            StringBuilder passengerNames = new StringBuilder();
            if (ride.getCreator() != null) {
                passengerNames.append(ride.getCreator().getName());
            }
            if (ride.getPassengers() != null && !ride.getPassengers().isEmpty()) {
                for (RegularUser passenger : ride.getPassengers()) {
                    if (passengerNames.length() > 0) {
                        passengerNames.append(", ");
                    }
                    passengerNames.append(passenger.getName());
                }
            }
            if (passengerNames.length() == 0) {
                passengerNames.append("Unknown Passenger(s)");
            }
            panicAlert.setPassengerName(passengerNames.toString());
            
            // Get driver and vehicle information
            if (ride.getDriver() != null) {
                panicAlert.setDriverName(ride.getDriver().getName());
                
                if (ride.getDriver().getVehicle() != null) {
                    Vehicle vehicle = ride.getDriver().getVehicle();
                    panicAlert.setVehicleType(vehicle.getType() != null ? vehicle.getType().toString() : "Standard");
                    panicAlert.setVehicleLicensePlate(vehicle.getLicensePlate() != null ? vehicle.getLicensePlate() : "N/A");
                }
            }
            
            // Change status to STOPPED
            rideService.updateRideStatus(id, RideStatus.STOPPED);
            
            // Set driver to not driving since ride is stopped
            if (ride.getDriver() != null) {
                ride.getDriver().setDriving(false);
                
                // Apply pending status change if it exists
                if (ride.getDriver().getPendingStatusChange() != null) {
                    ride.getDriver().setActive(ride.getDriver().getPendingStatusChange());
                    ride.getDriver().setPendingStatusChange(null);
                }
            }
            
            // Reset can_order flag for the creator (passenger)
            if (ride.getCreator() != null) {
                ride.getCreator().setCanOrder(true);
            }
            
            // Mark ride with panic flag
            rideService.markRideWithPanic(id);
            
            // Set additional panic information
            panicAlert.setRideId(id);
            panicAlert.setTimestamp(LocalDateTime.now());
            
            // Broadcast panic alert to all admins via WebSocket
            panicWebSocketService.broadcastPanicAlert(panicAlert);
            
            // Send notification to admins in database
            String message = "PANIC ALERT: " + panicAlert.getPassengerName() + 
                           " triggered panic. Location: " + panicAlert.getLocation() + 
                           ". Message: " + panicAlert.getMessage();
            notificationService.sendPanicNotification(panicAlert.getPassengerId(), 
                                                     panicAlert.getLocation().toString(), 
                                                     message);
            
            System.out.println("PANIC ACTIVATED - Ride: " + id + " | Passengers: " + panicAlert.getPassengerName() + " | Driver: " + panicAlert.getDriverName());
            
            return ResponseEntity.ok(Map.of(
                "status", "Panic activated successfully",
                "rideId", id,
                "timestamp", LocalDateTime.now()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to activate panic: " + e.getMessage()));
        }
    }

    @PostMapping("/{rideId}/start")
    public ResponseEntity<?> startRide(@PathVariable Long rideId) {
        try {
            rideService.startRide(rideId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/report", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postRideReport(@RequestBody RideReportDTO reportDTO){
        try {
            RideReport report = rideReportService.createReport(reportDTO);
            return new ResponseEntity<>(new RideReportedDTO(report), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{rideId}/review")
    public ResponseEntity<?> reviewRide(@PathVariable Long rideId, RideReviewDTO rideReviewDTO){
        Long userId = 1L; //placeholder
        try{
            reviewService.createReview(rideId, userId, rideReviewDTO);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/finish")
    public ResponseEntity<?> finishRide(@RequestBody FinishRideDTO finishRideDTO) {
        try{
            rideCompletionService.finishRide(finishRideDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(NoSuchElementException e){
            return new  ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e){
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.CONFLICT);
        }  catch (Exception e){
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{rideId}/cancel-by-driver")
    public ResponseEntity<?> cancelRideByDriver(@PathVariable Long rideId, @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Cancellation reason is required"));
            }
            
            rideService.cancelRideByDriver(rideId, reason);
            return ResponseEntity.ok(Map.of("message", "Ride cancelled successfully"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to cancel ride: " + e.getMessage()));
        }
    }

    @PostMapping("/{rideId}/cancel-by-passenger")
    public ResponseEntity<?> cancelRideByPassenger(@PathVariable Long rideId) {
        try {
            rideService.cancelRideByPassenger(rideId);
            return ResponseEntity.ok(Map.of("message", "Ride cancelled successfully"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to cancel ride: " + e.getMessage()));
        }
    }
}
