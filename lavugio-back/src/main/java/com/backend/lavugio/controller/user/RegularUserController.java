package com.backend.lavugio.controller.user;

import com.backend.lavugio.dto.user.UserDTO;
import com.backend.lavugio.dto.user.UserRegistrationDTO;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.service.user.RegularUserService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class RegularUserController {

    @Autowired
    private RegularUserService regularUserService;

    @PostMapping("/register/regularUser")
    public ResponseEntity<?> registerRegularUser(@RequestBody UserRegistrationDTO request) {
        try {
            RegularUser user = new RegularUser(); // Placeholder vrednost
            regularUserService.createRegularUser(user);
            UserDTO createdUser = new UserDTO(); // Placeholder vrednost
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/regularUser/{id}")
    public ResponseEntity<?> getRegularUser(@PathVariable Long id) {
        try {
            RegularUser user = regularUserService.getRegularUserById(id);
            UserDTO createdUser = new UserDTO(); // Placeholder vrednost
            return ResponseEntity.ok(UserDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/regular/{id}")
    public ResponseEntity<?> updateRegularUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String currentEmail = userDetails.getUsername();
            RegularUserDTO user = regularUserService.updateRegularUser(id, request, currentEmail);
            return ResponseEntity.ok(user);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/regular/{id}")
    public ResponseEntity<?> deleteRegularUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String currentEmail = userDetails.getUsername();
            regularUserService.deleteRegularUser(id, currentEmail);
            return ResponseEntity.ok("User deleted successfully");
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/regular/profile")
    public ResponseEntity<?> getRegularUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            RegularUserProfileDTO profile = regularUserService.getRegularUserProfile(email);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
