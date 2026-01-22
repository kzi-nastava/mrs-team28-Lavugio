package com.backend.lavugio.controller.user;

import com.backend.lavugio.dto.user.UpdatePasswordDTO;
import com.backend.lavugio.dto.user.UserProfileDTO;
import com.backend.lavugio.model.user.Account;
import com.backend.lavugio.model.user.Administrator;
import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.vehicle.Vehicle;
import com.backend.lavugio.service.user.AccountService;
import com.backend.lavugio.service.user.AdministratorService;
import com.backend.lavugio.service.user.DriverService;
import com.backend.lavugio.service.user.RegularUserService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private RegularUserService regularUserService;

    @Autowired
    private AdministratorService administratorService;

    @Autowired
    private DriverService driverService;

    private static Long accountId = 1L;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile() {
        Account account = accountService.getAccountByEmail("sarah.driver@example.com");

        if (account == null) {
            return ResponseEntity.notFound().build();
        }

        UserProfileDTO dto = new UserProfileDTO();

        // Zajednički podaci
        dto.setEmail(account.getEmail());
        dto.setName(account.getName());
        dto.setSurname(account.getLastName());
        dto.setPhoneNumber(account.getPhoneNumber());
        // TODO: Popuni adresu iz odgovarajućeg izvora
        dto.setAddress("Neka adresa");
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
                //dto.setVehicleSeats(vehicle.getSeats());
            }

        } else if (account instanceof RegularUser) {
            dto.setRole("REGULAR_USER");
        } else if (account instanceof Administrator) {
            dto.setRole("ADMINISTRATOR");
        }

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateCurrentUserProfile(@RequestBody UserProfileDTO updatedProfile) {
        // Authentication auth
        System.out.println("Pozvan update profila");
        String loggedInUser = "REGULAR_USER";
        try {
            if (loggedInUser == "DRIVER") {
                driverService.updateDriverDTO(accountId, updatedProfile);
            } else if (loggedInUser == "REGULAR_USER") {
                regularUserService.updateRegularUser(accountId, updatedProfile);
            } else {
                administratorService.updateAdministratorDTO(accountId, updatedProfile);
            }
            return ResponseEntity.ok().body(Map.of("Message", "Update successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/profile-photo")
    public ResponseEntity<?> uploadProfilePhoto(@RequestParam("file") MultipartFile file) {
        // Authentication auth
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
        // Authentication auth
        System.out.println("Pozvana promena lozinke");
        try {
            accountService.changePassword(accountId, passwordUpdate.getOldPassword(), passwordUpdate.getNewPassword());
            return ResponseEntity.ok().body("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
