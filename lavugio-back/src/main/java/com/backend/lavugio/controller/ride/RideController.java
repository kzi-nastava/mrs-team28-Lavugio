package com.backend.lavugio.controller.ride;

import com.backend.lavugio.dto.*;
import com.backend.lavugio.dto.ride.*;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.model.ride.RideReport;
import com.backend.lavugio.security.JwtUtil;
import com.backend.lavugio.security.SecurityUtils;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    public RideController(RideService rideService,
                          DriverService driverService,
                          RideReportService rideReportService,
                          ReviewService reviewService,
                          RideCompletionService rideCompletionService,
                          RideOverviewService rideOverviewService){
        this.rideService = rideService;
        this.driverService = driverService;
        this.rideReportService = rideReportService;
        this.reviewService = reviewService;
        this.rideCompletionService = rideCompletionService;
        this.rideOverviewService = rideOverviewService;
    }

    @PostMapping("/estimate-price")
    public ResponseEntity<?> estimateRidePrice(@RequestBody RideEstimateRequestDTO request) {
        try {
            double price = rideService.estimateRidePrice(request);
            return ResponseEntity.ok(price);
        } catch (Exception e) {
            return ResponseEntity.badRequest( ).body(e.getMessage());
        }
    }

    // 3. Create scheduled ride (for future)
    @PostMapping("/find-ride")
    public ResponseEntity<?> findRide(
            @RequestBody RideRequestDTO request) {
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
        // IMPLEMENTIRATI KADA SE URADI AUTENTIFIKACIJA LOGOVANOG KORISNIKA
        // @AuthenticationPrincipal UserDetails userDetails
        // String userEmail = userDetails.getUsername();
        Long idPlaceholder = 1L;
        List<Ride> rides = rideService.getRidesByPassengerId(idPlaceholder);
        return ResponseEntity.ok(rides);
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


    @PreAuthorize("hasRole('REGULAR_USER')")
    @GetMapping(value = "/{rideId}/overview", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRideStatus(@PathVariable Long rideId) {
//        RideOverviewDTO status =  new RideOverviewDTO(
//                1L,
//                1L,
//                500,
//                new CoordinatesDTO[]{new CoordinatesDTO(45.26430042229796, 19.830107688903812),
//                new CoordinatesDTO(45.23657222655474, 19.835062717102122)},
//                RideStatus.ACTIVE,
//                "Petar Petrović",
//                "Nemanjina 4",
//                "Knez Mihailova 12",
//                LocalDateTime.of(2026, 1, 8, 18, 30),
//                LocalDateTime.of(2026, 1, 8, 18, 40),
//                false,
//                false);
        try{
            Long userId = SecurityUtils.getCurrentUserId();
            RideOverviewDTO overviewDTO = rideOverviewService.getRideOverviewDTO(rideId, userId);
            return new ResponseEntity<>(overviewDTO, HttpStatus.OK);
        } catch (NoSuchElementException e){
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e){
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (Exception e){
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
                false
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
                false
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
                false
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
    public ResponseEntity<?> panicRide(@PathVariable Long id) {
        // IMPLEMENTIRATI KADA SE URADI AUTENTIFIKACIJA LOGOVANOG KORISNIKA
        // @AuthenticationPrincipal UserDetails userDetails
        // String userEmail = userDetails.getUsername();
        rideService.updateRideStatus(id, RideStatus.STOPPED);
        // send notifications to authorities and emergency contacts
        return ResponseEntity.ok("Ride panic activated successfully");
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

    @PreAuthorize("hasRole('REGULAR_USER')")
    @PostMapping(value = "/report", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postRideReport(@RequestBody RideReportDTO reportDTO){
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            RideReport report = rideReportService.createReport(userId, reportDTO);
            return new ResponseEntity<>(new RideReportedDTO(report), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.CONFLICT);
        } catch (IllegalCallerException e){
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('REGULAR_USER')")
    @PostMapping("/{rideId}/review")
    public ResponseEntity<?> reviewRide(@PathVariable Long rideId, RideReviewDTO rideReviewDTO){
        try{
            Long userId = SecurityUtils.getCurrentUserId();
            reviewService.createReview(rideId, userId, rideReviewDTO);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (IllegalCallerException e){
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (Exception e){
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/finish")
    public ResponseEntity<?> finishRide(@RequestBody FinishRideDTO finishRideDTO) {
        try{
            Long driverId = SecurityUtils.getCurrentUserId();
            rideCompletionService.finishRide(driverId, finishRideDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(NoSuchElementException e){
            return new  ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e){
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.FORBIDDEN);
        }  catch (Exception e){
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
