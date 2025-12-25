package com.backend.lavugio.controller.user;

import java.util.List;

import com.backend.lavugio.dto.user.*;
import com.backend.lavugio.model.user.Administrator;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.lavugio.service.user.AdministratorService;

@RestController
@RequestMapping("/api/admins")
public class AdministratorController {
    @Autowired
    private AdministratorService administratorService;

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
            @RequestBody UpdateAdministratorDTO request) {
            // , @AuthenticationPrincipal UserDetails userDetails
        try {
            // String currentEmail = userDetails.getUsername();
        	AdministratorDTO admin = administratorService.updateAdministratorDTO(id, request); // , currentEmail
            return ResponseEntity.ok(admin);
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
    
    
}
