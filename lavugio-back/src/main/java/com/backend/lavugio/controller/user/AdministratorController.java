package com.backend.lavugio.controller.user;

import java.util.List;

import com.backend.lavugio.dto.user.*;
import com.backend.lavugio.dto.*;
import com.backend.lavugio.model.enums.DriverHistorySortFieldEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.lavugio.service.user.AdministratorService;
import com.backend.lavugio.service.ride.RideService;
import com.backend.lavugio.service.route.RideDestinationService;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/admins")
public class AdministratorController {
    @Autowired
    private AdministratorService administratorService;
    
    @Autowired
    private RideService rideService;
    
    @Autowired
    private RideDestinationService rideDestinationService;

    // REGISTRATION
    
    @PostMapping("/register")
    public ResponseEntity<?> registerAdministrator(
            @RequestBody AdministratorRegistrationDTO request) {
            // , @AuthenticationPrincipal UserDetails userDetails
        try {
            // String currentEmail = userDetails.getUsername();
            administratorService.register(request); // , currentEmail
            return ResponseEntity.status(HttpStatus.CREATED).body("Administrator registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // CRUD OPERATION
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdministrator(@PathVariable Long id) {
        try {
            AdministratorDTO admin = administratorService.getAdministratorDTOById(id);
            return ResponseEntity.ok(admin);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getAdministratorByEmail(@PathVariable String email) {
        try {
        	AdministratorDTO admin = administratorService.getAdministratorDTOByEmail(email);
            return ResponseEntity.ok(admin);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllAdministrators() {
            // , @AuthenticationPrincipal UserDetails userDetails
        try {
            // String currentEmail = userDetails.getUsername();
            List<AdministratorDTO> admins = administratorService.getAllAdministratorsDTO(); // , currentEmail
            return ResponseEntity.ok(admins);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdministrator(
            @PathVariable Long id,
            @RequestBody AccountUpdateAdministratorDTO request) {
            // , @AuthenticationPrincipal UserDetails userDetails
        try {
            // String currentEmail = userDetails.getUsername();
        	// AdministratorDTO admin = administratorService.updateAdministratorDTO(id, request); // , currentEmail
            return ResponseEntity.ok("Deprecated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdministrator(
            @PathVariable Long id) {
            // , @AuthenticationPrincipal UserDetails userDetails
        try {
            // String currentEmail = userDetails.getUsername();
            administratorService.deleteAdministrator(id); // , currentEmail
            return ResponseEntity.ok("Administrator deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PROFILE ENDPOINT
    
    @GetMapping("/profile")
    public ResponseEntity<?> getAdministratorProfile() {
            // , @AuthenticationPrincipal UserDetails userDetails
        try {
            // String email = userDetails.getUsername();
            AdministratorDTO profile = administratorService.getAdministratorProfileDTO(); // , email
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // RIDE HISTORY ENDPOINTS - View history of any driver or passenger
    
    @GetMapping(value = "/rides/history/driver/{driverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDriverRideHistory(
            @PathVariable Long driverId,
            @RequestParam(defaultValue = "false") boolean ascending,
            @RequestParam(defaultValue = "START_DATE") DriverHistorySortFieldEnum sortBy,
            @RequestParam(required = false) String dateRangeStart,
            @RequestParam(required = false) String dateRangeEnd) {
        try {
            List<DriverHistoryDTO> rides = new ArrayList<>();
            rides.add(new DriverHistoryDTO(1L, "Location A", "Location B", "11:23 15.03.2024", "12:01 15.03.2024"));
            rides.add(new DriverHistoryDTO(2L, "Location C", "Location D", "09:10 14.03.2024", "09:45 14.03.2024"));
            rides.add(new DriverHistoryDTO(3L, "Location E", "Location F", "14:05 13.03.2024", "14:50 13.03.2024"));
            return new ResponseEntity<>(rides, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving driver ride history: " + e.getMessage());
        }
    }
    
    @GetMapping(value = "/rides/history/passenger/{passengerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPassengerRideHistory(
            @PathVariable Long passengerId,
            @RequestParam(defaultValue = "false") boolean ascending,
            @RequestParam(defaultValue = "START_DATE") DriverHistorySortFieldEnum sortBy,
            @RequestParam(required = false) String dateRangeStart,
            @RequestParam(required = false) String dateRangeEnd) {
        try {
            List<DriverHistoryDTO> rides = new ArrayList<>();
            rides.add(new DriverHistoryDTO(1L, "Location A", "Location B", "11:23 15.03.2024", "12:01 15.03.2024"));
            rides.add(new DriverHistoryDTO(2L, "Location C", "Location D", "09:10 14.03.2024", "09:45 14.03.2024"));
            rides.add(new DriverHistoryDTO(3L, "Location E", "Location F", "14:05 13.03.2024", "14:50 13.03.2024"));
            return new ResponseEntity<>(rides, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving passenger ride history: " + e.getMessage());
        }
    }
    
    @GetMapping(value = "/rides/details/{rideId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getRideDetailedView(@PathVariable Long rideId) {
        try {
            List<PassengerTableRowDTO> passengers = new ArrayList<>();
            passengers.add(new PassengerTableRowDTO(
                    1L,
                    "Marko Marković",
                    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg=="
            ));
            passengers.add(new PassengerTableRowDTO(
                    2L,
                    "Ana Anić",
                    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8DwHwAFBQIAX8jx0gAAAABJRU5ErkJggg=="
            ));
            passengers.add(new PassengerTableRowDTO(
                    3L,
                    "Petar Petrović",
                    "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChAGA+G6D9wAAAABJRU5ErkJggg=="
            ));

            DriverHistoryDetailedDTO dto = new DriverHistoryDetailedDTO(
                    "11:23 15.03.2024",
                    "12:45 15.03.2024",
                    "Kneza Miloša 15, Beograd",
                    "Bulevar kralja Aleksandra 73, Beograd",
                    1250.50,
                    true,
                    true,
                    passengers,
                    new CoordinatesDTO[]{new CoordinatesDTO(44.8125, 20.4612),
                    new CoordinatesDTO(44.8023, 20.4856)}
            );

            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error retrieving ride details: " + e.getMessage());
        }
    }
    
}
