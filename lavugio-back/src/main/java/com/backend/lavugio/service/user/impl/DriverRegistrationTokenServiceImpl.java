package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.model.user.Driver;
import com.backend.lavugio.model.user.DriverRegistrationToken;
import com.backend.lavugio.repository.user.DriverRegistrationTokenRepository;
import com.backend.lavugio.repository.user.DriverRepository;
import com.backend.lavugio.service.user.DriverRegistrationTokenService;
import com.backend.lavugio.service.utils.EmailService;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DriverRegistrationTokenServiceImpl implements DriverRegistrationTokenService {
    @Autowired
    private DriverRegistrationTokenRepository driverRegistrationTokenRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private  String activationLinkBase = "http://localhost:4200/activate-account?token=";

    public void sendActivationEmail(Long driverId, String email) {
        String tokenValue = UUID.randomUUID().toString();

        DriverRegistrationToken token = new DriverRegistrationToken();
        token.setToken(tokenValue);
        token.setDriverId(driverId);
        driverRegistrationTokenRepository.save(token);

        String activationLink = activationLinkBase + tokenValue;

        String body = "Driver registration link\n\n";
        body += "This is your activation link:\n";
        body += activationLink;

        emailService.sendEmail(email, "Driver activation link" , body);
    }

    public void activateDriver(String tokenValue, String password) {
        DriverRegistrationToken token = driverRegistrationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (token.isUsed()) {
            throw new RuntimeException("Token already used");
        }

        if (token.isExpired()) {
            throw new RuntimeException("Token expired");
        }

        Driver driver = driverRepository.findById(token.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setPassword(passwordEncoder.encode(password));
        driverRepository.save(driver);
        token.setUsed(true);
        driverRegistrationTokenRepository.save(token);
    }
}
