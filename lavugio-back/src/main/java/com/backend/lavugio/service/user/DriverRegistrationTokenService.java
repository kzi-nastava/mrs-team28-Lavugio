package com.backend.lavugio.service.user;

import com.backend.lavugio.repository.user.DriverRegistrationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface DriverRegistrationTokenService {
    void sendActivationEmail(Long driverId, String email);
    void activateDriver(String tokenValue, String password);
}
