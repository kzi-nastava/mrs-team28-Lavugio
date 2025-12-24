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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.lavugio.dto.user.AdministratorDTO;
import com.backend.lavugio.dto.user.AdministratorProfileDTO;
import com.backend.lavugio.dto.user.UpdateAdministratorDTO;
import com.backend.lavugio.dto.user.UserDTO;
import com.backend.lavugio.service.user.AdministratorService;

@RestController
@RequestMapping("/api/admins")
public class AdministratorController {
    @Autowired
    private AdministratorService adminService;

    // REGISTRATION
    
    @PostMapping("/register")
    public ResponseEntity<?> registerAdministrator(
            @RequestBody AdminRegistrationDTO request) {
            // , @AuthenticationPrincipal UserDetails userDetails
        try {
            // String currentEmail = userDetails.getUsername();
            Administrator admin = adminService.register(request); // , currentEmail
            return ResponseEntity.status(HttpStatus.CREATED).body("Administrator registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // CRUD OPERATION
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdministrator(@PathVariable Long id) {
        try {
            AdministratorDTO admin = adminService.getAdminById(id);
            return ResponseEntity.ok(admin);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getAdministratorByEmail(@PathVariable String email) {
        try {
        	AdministratorDTO admin = adminService.getAdminByEmail(email);
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
            List<AdministratorDTO> admins = adminService.getAllAdmins(); // , currentEmail
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
        	AdministratorDTO admin = adminService.updateAdmin(id, request); // , currentEmail
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
            adminService.deleteAdmin(id); // , currentEmail
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
            AdministratorProfileDTO profile = adminService.getAdminProfile(); // , email
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // USER MANAGEMENT ENDPOINT
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
            // , @AuthenticationPrincipal UserDetails userDetails
        try {
            // String adminEmail = userDetails.getUsername();
            List<UserDTO> users = adminService.getAllUsers(page, size); // , adminEmail
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.ok(new List<UserDTO>(0));
        }
    }
    
    
}
