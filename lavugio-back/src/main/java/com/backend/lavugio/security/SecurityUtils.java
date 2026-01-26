package com.backend.lavugio.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class to easily access current authenticated user information
 * Use this in your controllers to get logged-in user details
 */
public class SecurityUtils {

    /**
     * Get the current authenticated UserPrincipal
     * @return UserPrincipal or null if not authenticated
     */
    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        
        if (principal instanceof UserPrincipal) {
            return (UserPrincipal) principal;
        }
        
        return null;
    }

    /**
     * Get the current user's ID
     * @return User ID or null if not authenticated
     */
    public static Long getCurrentUserId() {
        UserPrincipal user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * Get the current user's email
     * @return Email or null if not authenticated
     */
    public static String getCurrentUserEmail() {
        UserPrincipal user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    /**
     * Get the current user's role
     * @return Role or null if not authenticated
     */
    public static String getCurrentUserRole() {
        UserPrincipal user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }

    /**
     * Check if there is an authenticated user
     * @return true if user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }

    /**
     * Check if current user has a specific role
     * @param role Role to check (e.g., "DRIVER", "ADMIN", "REGULAR_USER")
     * @return true if user has the role, false otherwise
     */
    public static boolean hasRole(String role) {
        UserPrincipal user = getCurrentUser();
        return user != null && role.equals(user.getRole());
    }

    /**
     * Check if current user is a driver
     * @return true if user is a driver, false otherwise
     */
    public static boolean isDriver() {
        return hasRole("DRIVER");
    }

    /**
     * Check if current user is an administrator
     * @return true if user is an administrator, false otherwise
     */
    public static boolean isAdministrator() {
        return hasRole("ADMINISTRATOR");
    }

    /**
     * Check if current user is a regular user
     * @return true if user is a regular user, false otherwise
     */
    public static boolean isRegularUser() {
        return hasRole("REGULAR_USER");
    }
}
