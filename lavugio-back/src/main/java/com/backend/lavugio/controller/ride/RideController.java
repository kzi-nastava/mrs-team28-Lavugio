package com.backend.lavugio.controller.ride;

import com.backend.lavugio.dto.ride.RideEstimateDTO;
import com.backend.lavugio.dto.ride.RideEstimateRequestDTO;
import com.backend.lavugio.dto.ride.RideRequestDTO;
import com.backend.lavugio.dto.ride.RideResponseDTO;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.model.ride.RideStatus;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.user.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            String userEmail = "email@emailovic.com"; // Placeholder vrednost
            //RideDTO ride = rideService.createInstantRide(userEmail, request);
            RideResponseDTO ride = new RideResponseDTO(); // Placeholder vrednost
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

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelRide(@PathVariable Long id) {
        // IMPLEMENTIRATI KADA SE URADI AUTENTIFIKACIJA LOGOVANOG KORISNIKA
        // @AuthenticationPrincipal UserDetails userDetails
        // String userEmail = userDetails.getUsername();
        rideService.cancelRide(id);
        return ResponseEntity.ok("Ride cancelled successfully");
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
}
