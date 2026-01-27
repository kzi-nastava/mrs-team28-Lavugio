package com.backend.lavugio.controller.user;

import com.backend.lavugio.dto.user.LoginRequestDTO;
import com.backend.lavugio.dto.user.LoginResponseDTO;
import com.backend.lavugio.dto.user.AccountUpdateDTO;
import com.backend.lavugio.dto.user.UserDTO;
import com.backend.lavugio.dto.user.UserRegistrationDTO;
import com.backend.lavugio.dto.user.EmailVerificationDTO;
import com.backend.lavugio.model.user.Account;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.security.JwtUtil;
import com.backend.lavugio.service.user.*;
import com.backend.lavugio.repository.user.DriverRepository;
import com.backend.lavugio.repository.user.AdministratorRepository;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
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
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private AdministratorRepository administratorRepository;
    @Autowired
    private com.backend.lavugio.repository.user.PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private com.backend.lavugio.service.utils.EmailService emailService;
    @Autowired
    private com.backend.lavugio.repository.user.AccountRepository accountRepository;
    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Autowired
    private DriverActivityService driverActivityService;
    @Autowired
    private DriverAvailabilityService driverAvailabilityService;
    
    // REGISTRATION & AUTHENTICATION

    @PostMapping("/register")
    public ResponseEntity<?> registerRegularUser(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String name,
            @RequestParam String lastName,
            @RequestParam String phoneNumber,
            @RequestParam String address,
            @RequestParam(required = false) MultipartFile profilePicture) {
        try {
            logger.info("Registration attempt for email: {}", email);
            
            // Create DTO from request parameters
            UserRegistrationDTO request = new UserRegistrationDTO();
            request.setEmail(email);
            request.setPassword(password);
            request.setName(name);
            request.setLastName(lastName);
            request.setPhoneNumber(phoneNumber);
            request.setAddress(address);
            
            // Handle profile picture if provided
            String profilePhotoPath = null;
            if (profilePicture != null && !profilePicture.isEmpty()) {
                profilePhotoPath = saveProfilePicture(profilePicture);
            } else {
                // Assign default profile picture
                profilePhotoPath = getDefaultProfilePicture();
            }
            request.setProfilePhotoPath(profilePhotoPath);
            
            UserDTO user = regularUserService.createRegularUser(request);
            
            // Send verification email
            userRegistrationTokenService.sendVerificationEmail(user.getId(), user.getEmail(), user.getName());
            
            logger.info("Registration successful for email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (com.backend.lavugio.exception.EmailAlreadyExistsException e) {
            logger.warn("Registration failed - Email already exists: {}", email);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new com.backend.lavugio.dto.ErrorResponseDTO(
                            HttpStatus.CONFLICT.value(),
                            "Email already registered: " + e.getMessage(),
                            "EMAIL_ALREADY_EXISTS",
                            java.time.LocalDateTime.now(),
                            "/api/regularUsers/register"
                    ));
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage(), e);
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

    private String saveProfilePicture(MultipartFile file) throws Exception {
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        // Use absolute path in user's project directory
        String projectRoot = System.getProperty("user.dir");
        String uploadsDir = projectRoot + "/uploads/profile-photos/";
        java.nio.file.Files.createDirectories(java.nio.file.Paths.get(uploadsDir));

        String filename = java.util.UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String filepath = uploadsDir + filename;
        
        file.transferTo(new java.io.File(filepath));
        
        return "/uploads/profile-photos/" + filename;
    }

    private String getDefaultProfilePicture() {
        return "/uploads/profile-photos/default-profile.png";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            logger.info("Login attempt for email: {}", request.getEmail());
            
            // Authenticate user
            Account account = accountService.authenticate(request.getEmail(), request.getPassword());
            
            // Check if email is verified
            if (!account.isEmailVerified()) {
                logger.warn("Login failed - Email not verified: {}", request.getEmail());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new com.backend.lavugio.dto.ErrorResponseDTO(
                                HttpStatus.FORBIDDEN.value(),
                                "Please verify your email address before logging in. Check your inbox for the verification link.",
                                "EMAIL_NOT_VERIFIED",
                                java.time.LocalDateTime.now(),
                                "/api/regularUsers/login"
                        ));
            }
            
            // Determine user role
            String role = "REGULAR_USER"; // Default
            
            // If account is a driver, set them as available
            Driver driver = driverRepository.findById(account.getId()).orElse(null);
            if (driver != null) {
                role = "DRIVER";
                logger.info("Driver set to active on login: {}", account.getId());
            } else if (administratorRepository.existsById(account.getId())) {
                role = "ADMIN";
            }
            
            // Generate JWT token with role
            String token = jwtUtil.generateToken(account.getEmail(), account.getId(), role);
            
            // Prepare response
            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(token);
            response.setUserId(account.getId());
            response.setEmail(account.getEmail());
            response.setName(account.getName());
            response.setRole(role);
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
            return ResponseEntity.ok(java.util.Map.of(
                "message", "Email verified successfully",
                "success", true
            ));
        } catch (Exception e) {
            logger.error("Email verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(java.util.Map.of(
                "message", e.getMessage(),
                "success", false
            ));
        }
    }

    @PostMapping("/logout/{userId}")
    public ResponseEntity<?> logout(@PathVariable Long userId) {
        try {
            logger.info("Logout attempt for user: {}", userId);
            
            // If user is a driver, check for active rides before allowing logout
            Driver driver = driverRepository.findById(userId).orElse(null);
            if (driver != null) {
                // Check if driver has active ride
                if (driver.isDriving()) {
                    logger.warn("Driver {} cannot logout - has active ride", userId);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new java.util.HashMap<String, String>() {{
                                put("message", "Cannot logout while on an active ride");
                            }});
                }
                
                // Set driver to inactive on logout
                driver.setActive(false);
                driverRepository.save(driver);
                logger.info("Driver set to inactive on logout: {}", userId);
            }
            
            return ResponseEntity.ok(new java.util.HashMap<String, String>() {{
                put("message", "Logout successful");
            }});
        } catch (Exception e) {
            logger.error("Logout failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new java.util.HashMap<String, String>() {{
                put("message", "Logout failed: " + e.getMessage());
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
    
    // PASSWORD RESET
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody java.util.Map<String, String> request) {
        try {
            String email = request.get("email");
            
            // Find user by email
            Account account = accountService.getAccountByEmail(email);
            if (account == null) {
                // For security reasons, don't reveal if email exists
                return ResponseEntity.ok(java.util.Map.of("message", "If an account with that email exists, a password reset link has been sent."));
            }
            
            // Generate password reset token
            String token = java.util.UUID.randomUUID().toString();
            com.backend.lavugio.model.user.PasswordResetToken resetToken = new com.backend.lavugio.model.user.PasswordResetToken();
            resetToken.setToken(token);
            resetToken.setUserId(account.getId());
            resetToken.setUsed(false);
            
            passwordResetTokenRepository.save(resetToken);
            
            // Send password reset email
            String resetLink = "http://localhost:4200/reset-password?token=" + token;
            emailService.sendEmail(
                email,
                "Password Reset Request",
                "Hello " + account.getName() + ",\n\n" +
                "You requested to reset your password. Please click the link below to reset your password:\n\n" +
                resetLink + "\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Lavugio Team"
            );
            
            logger.info("Password reset email sent to: {}", email);
            return ResponseEntity.ok(java.util.Map.of("message", "If an account with that email exists, a password reset link has been sent."));
        } catch (Exception e) {
            logger.error("Error in forgot password: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "An error occurred while processing your request."));
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody java.util.Map<String, String> request) {
        try {
            String token = request.get("token");
            String newPassword = request.get("newPassword");
            
            // Find reset token
            com.backend.lavugio.model.user.PasswordResetToken resetToken = 
                    passwordResetTokenRepository.findByToken(token)
                            .orElseThrow(() -> new RuntimeException("Invalid reset token"));
            
            // Check if token is expired or already used
            if (resetToken.isExpired()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(java.util.Map.of("error", "Reset token has expired"));
            }
            
            if (resetToken.isUsed()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(java.util.Map.of("error", "Reset token has already been used"));
            }
            
            // Get account and update password with encoding
            Account account = accountRepository.findById(resetToken.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            String encodedPassword = passwordEncoder.encode(newPassword);
            account.setPassword(encodedPassword);
            accountRepository.save(account);
            
            // Mark token as used
            resetToken.setUsed(true);
            passwordResetTokenRepository.save(resetToken);
            
            logger.info("Password reset successful for user ID: {}", resetToken.getUserId());
            return ResponseEntity.ok(java.util.Map.of("message", "Password successfully reset"));
        } catch (RuntimeException e) {
            logger.error("Error in reset password: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error in reset password: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "An error occurred while resetting password"));
        }
    }
}
