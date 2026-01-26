package com.backend.lavugio.controller.user;

import com.backend.lavugio.dto.user.LoginRequestDTO;
import com.backend.lavugio.dto.user.LoginResponseDTO;
import com.backend.lavugio.dto.user.AccountUpdateDTO;
import com.backend.lavugio.dto.user.UserDTO;
import com.backend.lavugio.dto.user.UserRegistrationDTO;
import com.backend.lavugio.dto.user.EmailVerificationDTO;
import com.backend.lavugio.model.user.Account;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.security.JwtUtil;
import com.backend.lavugio.service.user.AccountService;
import com.backend.lavugio.service.user.RegularUserService;
import com.backend.lavugio.service.user.UserRegistrationTokenService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/regularUsers")
public class RegularUserController {

    private static final Logger logger = LoggerFactory.getLogger(RegularUserController.class);

    @Autowired
    private RegularUserService regularUserService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRegistrationTokenService userRegistrationTokenService;
    
    // REGISTRATION & AUTHENTICATION

    @PostMapping("/register")
    public ResponseEntity<?> registerRegularUser(@Valid @RequestBody UserRegistrationDTO request) {
        try {
            logger.info("Registration attempt for email: {}", request.getEmail());
            UserDTO user = regularUserService.createRegularUser(request);
            
            // Send verification email
            userRegistrationTokenService.sendVerificationEmail(user.getId(), user.getEmail(), user.getName());
            
            logger.info("Registration successful for email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (com.backend.lavugio.exception.EmailAlreadyExistsException e) {
            logger.warn("Registration failed - Email already exists: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new com.backend.lavugio.dto.ErrorResponseDTO(
                            HttpStatus.CONFLICT.value(),
                            "Email already registered: " + e.getMessage(),
                            "EMAIL_ALREADY_EXISTS",
                            java.time.LocalDateTime.now(),
                            "/api/regularUsers/register"
                    ));
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new com.backend.lavugio.dto.ErrorResponseDTO(
                            HttpStatus.BAD_REQUEST.value(),
                            e.getMessage(),
                            "REGISTRATION_ERROR",
                            java.time.LocalDateTime.now(),
                            "/api/regularUsers/register"
                    ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            logger.info("Login attempt for email: {}", request.getEmail());
            
            // Authenticate user
            Account account = accountService.authenticate(request.getEmail(), request.getPassword());
            
            // Generate JWT token
            String token = jwtUtil.generateToken(account.getEmail(), account.getId());
            
            // Prepare response
            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(token);
            response.setUserId(account.getId());
            response.setEmail(account.getEmail());
            response.setName(account.getName());
            response.setMessage("Login successful");
            
            logger.info("Login successful for email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (com.backend.lavugio.exception.UserNotFoundException e) {
            logger.warn("Login failed - User not found: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new com.backend.lavugio.dto.ErrorResponseDTO(
                            HttpStatus.UNAUTHORIZED.value(),
                            "Email not found or invalid credentials",
                            "USER_NOT_FOUND",
                            java.time.LocalDateTime.now(),
                            "/api/regularUsers/login"
                    ));
        } catch (com.backend.lavugio.exception.InvalidCredentialsException e) {
            logger.warn("Login failed - Invalid credentials for: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new com.backend.lavugio.dto.ErrorResponseDTO(
                            HttpStatus.UNAUTHORIZED.value(),
                            "Invalid email or password",
                            "INVALID_CREDENTIALS",
                            java.time.LocalDateTime.now(),
                            "/api/regularUsers/login"
                    ));
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new com.backend.lavugio.dto.ErrorResponseDTO(
                            HttpStatus.UNAUTHORIZED.value(),
                            e.getMessage(),
                            "LOGIN_ERROR",
                            java.time.LocalDateTime.now(),
                            "/api/regularUsers/login"
                    ));
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody EmailVerificationDTO request) {
        try {
            logger.info("Email verification attempt with token: {}", request.getToken());
            userRegistrationTokenService.verifyEmail(request.getToken());
            logger.info("Email verified successfully");
            return ResponseEntity.ok(new java.util.HashMap<String, String>() {{
                put("message", "Email verified successfully");
                put("status", "success");
            }});
        } catch (RuntimeException e) {
            logger.error("Email verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new java.util.HashMap<String, String>() {{
                put("message", e.getMessage());
                put("status", "error");
            }});
        } catch (Exception e) {
            logger.error("Unexpected error during email verification: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new java.util.HashMap<String, String>() {{
                put("message", "Email verification failed: " + e.getMessage());
                put("status", "error");
            }});
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmailGet(@RequestParam String token) {
        try {
            logger.info("Email verification attempt (GET) with token: {}", token);
            userRegistrationTokenService.verifyEmail(token);
            logger.info("Email verified successfully");
            return ResponseEntity.ok(new java.util.HashMap<String, String>() {{
                put("message", "Email verified successfully");
                put("status", "success");
            }});
        } catch (RuntimeException e) {
            logger.error("Email verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new java.util.HashMap<String, String>() {{
                put("message", e.getMessage());
                put("status", "error");
            }});
        } catch (Exception e) {
            logger.error("Unexpected error during email verification: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new java.util.HashMap<String, String>() {{
                put("message", "Email verification failed: " + e.getMessage());
                put("status", "error");
            }});
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRegularUser(@PathVariable Long id) {
        try {
            UserDTO user = regularUserService.getRegularUserDTOById(id);
            return ResponseEntity.ok(user); // Changed from UserDTO to user
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getRegularUserByEmail(@PathVariable String email) {
        try {
            UserDTO user = regularUserService.getUserDTOByEmail(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/all")
    public ResponseEntity<?> getAllRegularUsers() {
        try {
            List<UserDTO> users = regularUserService.getAllUsersDTO();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateRegularUser(
            @PathVariable Long id,
            @RequestBody AccountUpdateDTO request) {
    	// @AuthenticationPrincipal UserDetails userDetails
        try {
            // String currentEmail = userDetails.getUsername();
            // UserDTO user = regularUserService.updateRegularUser(id, request);
            return ResponseEntity.ok("Deprecated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRegularUser(
            @PathVariable Long id) {
    	// @AuthenticationPrincipal UserDetails userDetails
        try {
            // String currentEmail = userDetails.getUsername();
            regularUserService.deleteRegularUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PROFILE ENDPOINT
    
    @GetMapping("/profile")
    public ResponseEntity<?> getRegularUserProfile() {
        // @AuthenticationPrincipal UserDetails userDetails
    	try {
            // Long id = userDetails.getId();
    		Long id = 1L; // placeholder
            UserDTO profile = regularUserService.getRegularUserProfile(id);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getRegularUserProfileById(@PathVariable Long id) {
        try {
            UserDTO profile = regularUserService.getUserProfileById(id);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // BLOCK ENDPOINT
    
    @PostMapping("/{id}/block")
    public ResponseEntity<?> blockUser(
            @PathVariable Long id,
            @RequestBody String reason) {
        try {
            RegularUser response = regularUserService.blockUser(id, reason);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/unblock")
    public ResponseEntity<?> unblockUser(
            @PathVariable Long id) {
        // @AuthenticationPrincipal UserDetails userDetails

        try {
            // String adminEmail = userDetails.getUsername();
            RegularUser response = regularUserService.unblockUser(id);
            return ResponseEntity.ok(response);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/{id}/blocked")
    public ResponseEntity<?> getIsUserBlocked(@PathVariable Long id) {
        try {
            UserDTO user = regularUserService.getRegularUserDTOById(id);
            return ResponseEntity.ok(user.isBlocked());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // UTILITY ENDPOINT 
    
    @GetMapping("/check-email/{email}")
    public ResponseEntity<?> checkRegularUserEmail(@PathVariable String email) {
        try {
            boolean exists = regularUserService.emailExists(email);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
