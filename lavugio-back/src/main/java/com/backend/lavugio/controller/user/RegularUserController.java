package com.backend.lavugio.controller.user;

import com.backend.lavugio.dto.user.UpdateUserDTO;
import com.backend.lavugio.dto.user.UserDTO;
import com.backend.lavugio.dto.user.UserRegistrationDTO;
import com.backend.lavugio.dto.user.LoginRequestDTO;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.user.Account;
import com.backend.lavugio.service.user.AccountService;
import com.backend.lavugio.service.user.RegularUserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/regularUsers")
public class RegularUserController {

    @Autowired
    private RegularUserService regularUserService;
    @Autowired
    private AccountService accountService;
    
    // REGISTRATION

    @PostMapping("/register")
    public ResponseEntity<?> registerRegularUser(@RequestBody UserRegistrationDTO request) {
        try {
            UserDTO user = regularUserService.createRegularUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(user); // Changed from createdUser to user
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
            @RequestBody UpdateUserDTO request) {
    	// @AuthenticationPrincipal UserDetails userDetails
        try {
            // String currentEmail = userDetails.getUsername();
            UserDTO user = regularUserService.updateRegularUser(id, request);
            return ResponseEntity.ok(user);
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
    
    // AUTHENTICATION ENDPOINT

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            Account account = accountService.authenticate(request.getEmail(), request.getPassword());
            UserDTO userDTO = new UserDTO();
            userDTO.setId(account.getId());
            userDTO.setName(account.getName());
            userDTO.setLastName(account.getLastName());
            userDTO.setEmail(account.getEmail());
            userDTO.setPhoneNumber(account.getPhoneNumber());
            userDTO.setProfilePhotoPath(account.getProfilePhotoPath());
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
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
