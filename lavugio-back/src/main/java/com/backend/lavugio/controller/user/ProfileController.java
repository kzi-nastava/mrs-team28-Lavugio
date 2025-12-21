package com.backend.lavugio.controller.user;

import com.backend.lavugio.dto.user.AccountProfileDTO;
import com.backend.lavugio.dto.user.AdministratorProfileDTO;
import com.backend.lavugio.dto.user.DriverProfileDTO;
import com.backend.lavugio.dto.user.RegularUserProfileDTO;
import com.backend.lavugio.model.user.*;
import com.backend.lavugio.service.user.AccountService;
import com.backend.lavugio.service.user.AdministratorService;
import com.backend.lavugio.service.user.DriverService;
import com.backend.lavugio.service.user.RegularUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/profile")
@CrossOrigin(origins = "http://localhost:4200")
public class ProfileController {
    private final AccountService accountService;
    private final RegularUserService regularUserService;
    private final DriverService driverService;
    private final AdministratorService administratorService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProfileController(AccountService accountService,
                             RegularUserService regularUserService,
                             DriverService driverService,
                             AdministratorService administratorService) {
        this.accountService = accountService;
        this.regularUserService = regularUserService;
        this.driverService = driverService;
        this.administratorService = administratorService;
        this.modelMapper = new ModelMapper();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        Optional<Account> accountOpt = accountService.getAccountById(id);

        if (!accountOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Account not found");
        }

        Account account = accountOpt.get();

        if (account instanceof RegularUser) {
            RegularUser user = regularUserService.getRegularUserById(id);
            if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            return ResponseEntity.ok(mapToRegularUserProfileDTO(user));
        }
        else if (account instanceof Driver) {
            Driver driver = driverService.getDriverById(id);
            if (driver == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found");
            return ResponseEntity.ok(mapToDriverProfileDTO(driver));
        }
        else if (account instanceof Administrator) {
            Administrator admin = administratorService.getAdministratorById(id);
            if (admin == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
            return ResponseEntity.ok(mapToAdminProfileDTO(admin));
        }

        return ResponseEntity.ok(mapToProfileDTO(account));
    }

    // Mapping methods
    private AccountProfileDTO mapToProfileDTO(Account account) {
        AccountProfileDTO dto = modelMapper.map(account, AccountProfileDTO.class);
        dto.setAccountType(getAccountType(account));
        return dto;
    }

    private RegularUserProfileDTO mapToRegularUserProfileDTO(RegularUser user) {
        RegularUserProfileDTO dto = modelMapper.map(user, RegularUserProfileDTO.class);
        dto.setAccountType("REGULAR_USER");
        // Map additional fields if needed
        return dto;
    }

    private DriverProfileDTO mapToDriverProfileDTO(Driver driver) {
        DriverProfileDTO dto = modelMapper.map(driver, DriverProfileDTO.class);
        dto.setAccountType("DRIVER");
        return dto;
    }

    private AdministratorProfileDTO mapToAdminProfileDTO(Administrator admin) {
        AdministratorProfileDTO dto = modelMapper.map(admin, AdministratorProfileDTO.class);
        dto.setAccountType("ADMIN");
        return dto;
    }

    private String getAccountType(Account account) {
        if (account instanceof RegularUser) return "REGULAR_USER";
        if (account instanceof Driver) return "DRIVER";
        if (account instanceof Administrator) return "ADMIN";
        return "UNKNOWN";
    }

}
