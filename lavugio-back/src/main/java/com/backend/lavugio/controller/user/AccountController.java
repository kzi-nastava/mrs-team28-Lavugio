package com.backend.lavugio.controller.user;

import com.backend.lavugio.dto.user.*;
import com.backend.lavugio.model.user.Account;
import com.backend.lavugio.model.user.Administrator;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.security.JwtUtil;
import com.backend.lavugio.security.SecurityUtils;
import com.backend.lavugio.service.user.AccountService;
import com.backend.lavugio.service.user.AdministratorService;
import com.backend.lavugio.service.user.DriverService;
import com.backend.lavugio.service.user.RegularUserService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private RegularUserService regularUserService;

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private DriverService driverService;



    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile() {
        // Get authenticated user ID from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long accountId = JwtUtil.extractAccountId(authentication);
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Account account = accountService.getAccountById(accountId);

        if (account == null) {
            return ResponseEntity.notFound().build();
        }

        UserProfileDTO dto = new UserProfileDTO();

        // Zajednički podaci
        dto.setEmail(account.getEmail());
        dto.setName(account.getName());
        dto.setSurname(account.getLastName());
        dto.setPhoneNumber(account.getPhoneNumber());
        dto.setAddress(account.getAddress());
        dto.setProfilePhotoPath(account.getProfilePhotoPath());

        if (account instanceof Driver) {
            Driver driver = (Driver) account;
            dto.setRole("DRIVER");

            Vehicle vehicle = driver.getVehicle();
            if (vehicle != null) {
                dto.setVehicleMake(vehicle.getMake());
                dto.setVehicleModel(vehicle.getModel());
                dto.setVehicleType(vehicle.getType().toString());
                dto.setVehicleLicensePlate(vehicle.getLicensePlate());
                dto.setVehicleColor(vehicle.getColor());
                dto.setVehicleBabyFriendly(vehicle.isBabyFriendly());
                dto.setVehiclePetFriendly(vehicle.isPetFriendly());
                dto.setVehicleSeats(vehicle.getPassengerSeats());
            }

        } else if (account instanceof RegularUser) {
            dto.setRole("REGULAR_USER");
        } else if (account instanceof Administrator) {
            dto.setRole("ADMINISTRATOR");
        }

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateCurrentUserProfile(@RequestBody AccountUpdateDTO updatedProfile) {
        // Get authenticated user ID from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long accountId = JwtUtil.extractAccountId(authentication);
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("Pozvan update profila");
        try {
            accountService.updateAccount(accountId, updatedProfile);
            return ResponseEntity.ok().body(Map.of("Message", "Update successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/profile-photo")
    public ResponseEntity<?> uploadProfilePhoto(@RequestParam("file") MultipartFile file) {
        // Get authenticated user ID from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long accountId = JwtUtil.extractAccountId(authentication);
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("Pozvan upload profilne");
        try {
            Account updatedAccount = accountService.updateProfilePhoto(accountId, file);
            return ResponseEntity.ok(updatedAccount.getProfilePhotoPath());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/profile-photo")
    public ResponseEntity<Resource> getProfilePhoto() {
        // Get authenticated user ID from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long accountId = JwtUtil.extractAccountId(authentication);
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("Pozvano dobavljanje profilne");

        try {
            Resource photo = accountService.getProfilePhoto(accountId);

            if (photo == null) {
                return ResponseEntity.notFound().build();
            }

            String mimeType = Files.probeContentType(photo.getFile().toPath());
            if (mimeType == null) {
                mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(photo);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody UpdatePasswordDTO passwordUpdate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long accountId = JwtUtil.extractAccountId(authentication);
        if (accountId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("Pozvana promena lozinke");
        try {
            accountService.changePassword(accountId, passwordUpdate.getOldPassword(), passwordUpdate.getNewPassword());
            return ResponseEntity.ok().body("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/email-suggestions")
    public ResponseEntity<List<EmailSuggestionDTO>> getEmailSuggestions(@RequestParam String query) {
        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        Pageable pageable = PageRequest.of(0, 5);
        List<String> emails = accountService.findTop5EmailsByPrefix(query.trim(), pageable);

        List<EmailSuggestionDTO> suggestions = emails.stream()
                .map(EmailSuggestionDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(suggestions);
    }

    @PostMapping("/block")
    public ResponseEntity<?> blockUser(@RequestBody BlockUserDTO blockUserDTO) {
        try {
            accountService.blockUser(blockUserDTO);
            return ResponseEntity.ok().body(Map.of("message", "User blocked successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/is-blocked")
    public ResponseEntity<?> isBlocked() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long accountId = JwtUtil.extractAccountId(authentication);
            IsAccountBlockedDTO isAccountBlockedDTO = accountService.isBlocked(accountId);
            return ResponseEntity.ok(isAccountBlockedDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/can-order-ride")
    public ResponseEntity<?> canOrder() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long accountId = JwtUtil.extractAccountId(authentication);
            CanOrderRideDTO canOrderRideDTO = accountService.canOrderRide(accountId);
            return ResponseEntity.ok(canOrderRideDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
