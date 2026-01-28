package com.backend.lavugio.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.backend.lavugio.repository.user.RegularUserRepository;
import com.backend.lavugio.repository.user.DriverRepository;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.user.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Component
public class AuthVerificationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthVerificationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RegularUserRepository regularUserRepository;

    @Autowired
    private DriverRepository driverRepository;

    private static final String[] ADVANCED_FEATURE_PATHS = {
            "/api/rides"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        String method = request.getMethod();

        logger.info("AuthVerificationFilter - Processing: {} {}", method, path);

        // Check if this is an advanced feature endpoint
        boolean isAdvancedFeature = isAdvancedFeatureEndpoint(path);

        if (isAdvancedFeature) {
            logger.info("AuthVerificationFilter - Advanced feature detected: {}", path);
            String token = extractToken(request);

            if (token != null) {
                try {
                    Long userId = jwtUtil.extractUserId(token);
                    logger.info("AuthVerificationFilter - Checking verification for user: {}", userId);

                    // Check if user is email verified
                    RegularUser regularUser = regularUserRepository.findById(userId).orElse(null);
                    logger.info("AuthVerificationFilter - RegularUser loaded: {}", regularUser != null);
                    if (regularUser != null && !regularUser.isEmailVerified()) {
                        logger.warn("Unverified user attempting to access advanced feature: {}", userId);
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"message\": \"Please verify your email to access this feature\", \"status\": \"email_not_verified\"}");
                        return;
                    }

                    Driver driver = driverRepository.findById(userId).orElse(null);
                    logger.info("AuthVerificationFilter - Driver loaded: {}", driver != null);
                    if (driver != null && !driver.isEmailVerified()) {
                        logger.warn("Unverified driver attempting to access advanced feature: {}", userId);
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"message\": \"Please verify your email to access this feature\", \"status\": \"email_not_verified\"}");
                        return;
                    }
                    logger.info("AuthVerificationFilter - Verification passed for user: {}", userId);
                } catch (Exception e) {
                    logger.error("Error validating token: {}", e.getMessage());
                }
            } else {
                // No token - user is not authenticated
                logger.warn("Unauthenticated user attempting to access advanced feature: {}", path);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"You must be logged in to access this feature\", \"status\": \"unauthorized\"}");
                return;
            }
        }

        logger.info("AuthVerificationFilter - About to call filterChain.doFilter()");
        filterChain.doFilter(request, response);
        logger.info("AuthVerificationFilter - Returned from filterChain.doFilter()");
    }

    private boolean isAdvancedFeatureEndpoint(String path) {
        for (String advancedPath : ADVANCED_FEATURE_PATHS) {
            if (path.startsWith(advancedPath)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPublicEndpoint(String path) {
        return path.contains("/register") || path.contains("/login") || path.contains("/verify-email");
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
