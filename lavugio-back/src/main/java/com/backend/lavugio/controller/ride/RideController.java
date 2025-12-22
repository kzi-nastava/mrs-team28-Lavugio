package com.backend.lavugio.controller.ride;

import com.backend.lavugio.dto.ride.RideRequestDTO;
import com.backend.lavugio.dto.ride.RideResponseDTO;
import com.backend.lavugio.model.ride.Ride;
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

    @PostMapping("/book")
    public ResponseEntity<?> bookRide(@RequestBody RideRequestDTO request) {
        // IMPLEMENTIRATI KADA SE URADI AUTENTIFIKACIJA LOGOVANOG KORISNIKA
        // @AuthenticationPrincipal UserDetails userDetails
        // String userEmail = userDetails.getUsername();
        try {
            String emailPlaceholder = "probni@email.com";
            RideResponseDTO response = rideService.bookRide(emailPlaceholder, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }
        /* IMPLEMENTIRATI EXCEPTIONS
        } catch (NoAvailableDriversException e) {
            notificationService.sendNoDriversAvailable(userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("No available drivers at the moment");

        } catch (InvalidRideException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }*/
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
}
