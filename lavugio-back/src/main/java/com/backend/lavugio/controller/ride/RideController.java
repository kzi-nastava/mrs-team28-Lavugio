package com.backend.lavugio.controller.ride;

import com.backend.lavugio.dto.*;
import com.backend.lavugio.dto.ride.*;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.enums.RideStatus;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.user.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
@CrossOrigin(origins = "http://localhost:4200")
public class RideController {

    @Autowired
    private RideService rideService;

    @Autowired
    private DriverService driverService;

    @PostMapping("/estimate")
    public ResponseEntity<?> estimateRideInfo(@RequestBody RideEstimateRequestDTO request) {
        try {
            //RideEstimateDTO estimate = rideService.estimateRide(request);
            RideEstimateDTO estimate = new RideEstimateDTO(300, 10.0f, 23); // Placeholder vrednosti
            return ResponseEntity.ok(estimate);
        } catch (Exception e) {
            return ResponseEntity.badRequest( ).body(e.getMessage());
        }
    }

    @PostMapping("/instant")
    public ResponseEntity<?> createInstantRide(
            @RequestBody RideRequestDTO request) {
        // @AuthenticationPrincipal UserDetails userDetails,
        try {
            //String userEmail = userDetails.getUsername();
            String userEmail = "michael.brown@example.com"; // Placeholder vrednost
            // WORK FROM HERE
            RideResponseDTO ride = rideService.createInstantRide(userEmail, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ride);
        } catch (Exception e) {
            // notificationService.sendNoDriversAvailable(userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
    }

    // 3. Create scheduled ride (for future)
    @PostMapping("/schedule")
    public ResponseEntity<?> createScheduledRide(
            @RequestBody RideRequestDTO request) {
        // @AuthenticationPrincipal UserDetails userDetails,
        try {
            //String userEmail = userDetails.getUsername();
            String userEmail = "email@emailovic.com"; // Placeholder vrednost
            //RideDTO ride = rideService.createScheduledRide(userEmail, request);
            RideResponseDTO ride = new RideResponseDTO(); // Placeholder vrednost
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
//        Collection<RideReport> reports = rideReportService.getReportsByRideId(rideId);
//        Collection<RideReportedDTO> reportDTOs = new ArrayList<>();
//        for (RideReport report : reports) {
//            RideReportedDTO rideReportedDTO = new RideReportedDTO();
//            rideReportedDTO.setReporterId(report.getReporter().getId());
//            rideReportedDTO.setReportId(report.getReportId());
//            rideReportedDTO.setReportText(report.getReportMessage());
//            reportDTOs.add(rideReportedDTO);
//        }
        List<RideReportedDTO> reportDTOs = new ArrayList<>();
        reportDTOs.add(new RideReportedDTO(1L, rideId, 2L, "Driver was late"));
        reportDTOs.add(new RideReportedDTO(2L, rideId, 3L, "Car was unclean"));
        return new ResponseEntity<>(reportDTOs, HttpStatus.OK);
    }

    @GetMapping(value = "/{rideId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideStatusDTO> getRideStatus(@PathVariable Long rideId) {
//        Ride ride = rideService.getRideById(rideId);
//        DriverLocation driverLocation = driverService.getDriverStatus(ride.getDriver().getId());
//        if (driverLocation == null){
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        List<RideDestination> rideDestinations = rideDestinationService.getOrderedDestinationsByRideId(rideId);
//        Address start = rideDestinations.getFirst().getAddress();
//        Address end = rideDestinations.getLast().getAddress();
//        RouteTimeEstimation eta;
//        try {
//            eta = etaService.calculateEta(driverLocation.getLongitude(), driverLocation.getLatitude(), end.getLongitude(), end.getLatitude());
//        }catch(Exception e){
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        RideStatusDTO status =  new RideStatusDTO();
//        status.setStartLatitude(start.getLatitude());
//        status.setStartLongitude(start.getLongitude());
//        status.setDestinationLatitude(end.getLatitude());
//        status.setDestinationLongitude(end.getLongitude());
//        status.setCurrentLatitude(driverLocation.getLatitude());
//        status.setCurrentLongitude(driverLocation.getLongitude());
//        status.setRemainingTimeSeconds(eta.getDurationSeconds());
        RideStatusDTO status =  new RideStatusDTO(
                1L,
                new CoordinatesDTO(30.2671, 19.8335),
                new CoordinatesDTO(30.2861, 19.8017),
                new CoordinatesDTO(30.2750, 19.8200),
                RideStatus.ACTIVE,
                "Petar Petrović",
                "Nemanjina 4",
                "Knez Mihailova 12",
                LocalDateTime.of(2026, 1, 8, 18, 30),
                null);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @GetMapping(value = "/statuses", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<RideStatusDTO>> getRideStatuses() {

        List<RideStatusDTO> statuses = new ArrayList<>();

        statuses.add(new RideStatusDTO(
                1L,
                new CoordinatesDTO(30.2671, 19.8335),
                new CoordinatesDTO(30.2861, 19.8017),
                new CoordinatesDTO(30.2750, 19.8200),
                RideStatus.ACTIVE,
                "Petar Petrović",
                "Nemanjina 4",
                "Knez Mihailova 12",
                LocalDateTime.of(2026, 1, 8, 18, 30),
                null // arrivalTime još ne postoji
        ));

        statuses.add(new RideStatusDTO(
                2L,
                new CoordinatesDTO(31.2671, 20.8335),
                new CoordinatesDTO(31.2861, 20.8017),
                new CoordinatesDTO(31.2750, 20.8200),
                RideStatus.FINISHED,
                "Marko Marković",
                "Bulevar Oslobođenja 88",
                "Aerodrom Nikola Tesla",
                LocalDateTime.of(2026, 1, 8, 17, 10),
                LocalDateTime.of(2026, 1, 8, 17, 45)
        ));

        statuses.add(new RideStatusDTO(
                3L,
                null, // driver još nije dodeljen
                new CoordinatesDTO(32.2861, 21.8017),
                new CoordinatesDTO(32.2750, 21.8200),
                com.backend.lavugio.model.enums.RideStatus.SCHEDULED,
                null,
                "Zmaj Jovina 15",
                "Studentski trg",
                LocalDateTime.of(2026, 1, 8, 19, 5),
                null
        ));

        return ResponseEntity.ok(statuses);
    }




    @GetMapping(value = "/{rideId}/review", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetRideReviewDTO> getRideReview(@PathVariable Long rideId){
        GetRideReviewDTO getRideReviewDTO = new GetRideReviewDTO(1L, 4, 5, "Great ride!");
        return new ResponseEntity<>(getRideReviewDTO, HttpStatus.OK);
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

    @PostMapping(value = "/{rideId}/report", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideReportedDTO> postRideReport(@PathVariable Long rideId, @RequestBody RideReportDTO reportDTO){
//        RideReport report = new RideReport();
//        Ride ride = rideService.getRideById(rideId);
//        RegularUser user =  regularUserService.getRegularUserById(reportDTO.getReporterId());
//        report.setRide(ride);
//        report.setReportMessage(reportDTO.getReportText());
//        report.setReporter(user);
//        rideReportService.createReport(report);
//
//        RideReportedDTO rideReportedDTO = new RideReportedDTO();
//        rideReportedDTO.setReporterId(report.getReporter().getId());
//        rideReportedDTO.setReportId(report.getReportId());
//        rideReportedDTO.setReportText(report.getReportMessage());
        RideReportedDTO rideReportedDTO = new RideReportedDTO(1L, rideId, reportDTO.getReporterId(), reportDTO.getReportText());
        return new ResponseEntity<>(rideReportedDTO, HttpStatus.OK);
    }

    @PostMapping("/{rideId}/review")
    public ResponseEntity<?> reviewRide(@PathVariable Long rideId, RideReviewDTO rideReviewDTO){
        //reviewService.createReview(rideId, rideReviewDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{rideId}/complete")
    public ResponseEntity<?> completeRide(@PathVariable Long rideId,
                                          @RequestBody FinishRideDTO finishRideDTO) {
//        Ride ride = rideService.getRideById(rideId);
//        Driver driver = ride.getDriver();
//        rideService.updateRideStatus(rideId, RideStatus.FINISHED);
//        driverService.updateDriverDriving(driver.getId(), false);
//        notificationService.notifyPassengersAboutFinishedRide(ride);
//        for (RegularUser user : ride.getPassengers()){
//            userService.enableUserOrdering(user.getId());
//        }
        // notificationService.notifyPassengersAboutCompletedRide();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
