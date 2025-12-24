package com.backend.lavugio.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.lavugio.dto.user.DriverDTO;
import com.backend.lavugio.dto.user.DriverProfileDTO;
import com.backend.lavugio.dto.user.DriverRegistrationDTO;
import com.backend.lavugio.dto.user.UpdateDriverDTO;
import com.backend.lavugio.service.user.DriverService;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {
	@Autowired
	private DriverService driverService;
	
 // ========== REGISTRATION ==========
    
    @PostMapping("/register")
    public ResponseEntity<?> registerDriver(@RequestBody DriverRegistrationDTO request) {
        try {
            DriverDTO driver = driverService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("Driver registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ========== CRUD OPERATIONS ==========
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getDriver(@PathVariable Long id) {
        try {
            DriverDTO driver = driverService.getDriverById(id);
            return ResponseEntity.ok(driver);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getDriverByEmail(@PathVariable String email) {
        try {
            DriverDTO driver = driverService.getDriverByEmail(email);
            return ResponseEntity.ok(driver);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllDrivers() {
        try {
            List<DriverDTO> drivers = driverService.getAllDrivers();
            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableDrivers() {
        try {
            List<DriverDTO> drivers = driverService.getAvailableDrivers();
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
            DriverDTO driver = driverService.updateDriver(id, request, currentEmail);
            return ResponseEntity.ok(driver);
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
            driverService.deleteDriver(id, currentEmail);
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
            DriverProfileDTO profile = driverService.getDriverProfile(email);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/profile")
    public ResponseEntity<?> getDriverProfileById(@PathVariable Long id) {
        try {
            DriverProfileDTO profile = driverService.getDriverProfileById(id);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // RATING ENDPOINTS
    
    @PostMapping("/{id}/rate")
    public ResponseEntity<?> rateDriver(
            @PathVariable Long id,
            @RequestBody RatingRequest request) {
    	// @AuthenticationPrincipal UserDetails userDetails
        try {
            String userEmail = userDetails.getUsername();
            RatingDTO rating = ratingService.rateDriver(id, request, userEmail);
            return ResponseEntity.ok(rating);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}/ratings")
    public ResponseEntity<?> getDriverRatings(@PathVariable Long id) {
        try {
            List<RatingDTO> ratings = ratingService.getDriverRatings(id);
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
            DriverStatusDTO status = driverService.activateDriver(id);
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
            DriverStatusDTO status = driverService.deactivateDriver(id);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}/status")
    public ResponseEntity<?> getDriverStatus(@PathVariable Long id) {
        try {
            DriverStatusDTO status = driverService.getDriverStatus(id);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/activity")
    public ResponseEntity<?> getDriverActivity(@PathVariable Long id) {
        try {
            DriverStatusDTO activity = driverService.getDriverActivity(id);
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
	
}
