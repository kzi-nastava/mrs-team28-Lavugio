package com.backend.lavugio.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Example controller demonstrating different ways to access authenticated user information
 * and use @PreAuthorize annotation
 */
@RestController
@RequestMapping("/api/example")
public class ExampleSecurityController {

    /**
     * Method 1: Using SecurityUtils (Recommended - cleanest approach)
     */
    @GetMapping("/current-user-utils")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUserWithUtils() {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", SecurityUtils.getCurrentUserId());
        response.put("email", SecurityUtils.getCurrentUserEmail());
        response.put("role", SecurityUtils.getCurrentUserRole());
        return ResponseEntity.ok(response);
    }

    /**
     * Method 2: Using @AuthenticationPrincipal annotation
     */
    @GetMapping("/current-user-principal")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUserWithPrincipal(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userPrincipal.getUserId());
        response.put("email", userPrincipal.getEmail());
        response.put("role", userPrincipal.getRole());
        return ResponseEntity.ok(response);
    }

    /**
     * Method 3: Using Authentication object
     */
    @GetMapping("/current-user-auth")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUserWithAuth(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userPrincipal.getUserId());
        response.put("email", userPrincipal.getEmail());
        response.put("role", userPrincipal.getRole());
        return ResponseEntity.ok(response);
    }

    /**
     * Example: Only drivers can access
     */
    @GetMapping("/driver-only")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<String> driverOnly() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok("Driver-only endpoint. Driver ID: " + userId);
    }

    /**
     * Example: Only administrators can access
     */
    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<String> adminOnly() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok("Admin-only endpoint. Admin ID: " + userId);
    }

    /**
     * Example: Only regular users can access
     */
    @GetMapping("/regular-user-only")
    @PreAuthorize("hasRole('REGULAR_USER')")
    public ResponseEntity<String> regularUserOnly() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok("Regular user endpoint. User ID: " + userId);
    }

    /**
     * Example: Multiple roles allowed
     */
    @GetMapping("/driver-or-admin")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMINISTRATOR')")
    public ResponseEntity<String> driverOrAdmin() {
        Long userId = SecurityUtils.getCurrentUserId();
        String role = SecurityUtils.getCurrentUserRole();
        return ResponseEntity.ok("Accessible by driver or admin. User ID: " + userId + ", Role: " + role);
    }

    /**
     * Example: Check if user owns resource (using SpEL expression)
     */
    @GetMapping("/check-ownership")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> checkOwnership() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        
        // Example: Simulate checking if user owns resource
        Long resourceOwnerId = 5L; // This would come from database
        boolean isOwner = currentUserId.equals(resourceOwnerId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("currentUserId", currentUserId);
        response.put("resourceOwnerId", resourceOwnerId);
        response.put("isOwner", isOwner);
        return ResponseEntity.ok(response);
    }
}
