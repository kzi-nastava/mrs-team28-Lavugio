package com.backend.lavugio.service.user;

import org.springframework.stereotype.Service;

@Service
public interface UserRegistrationTokenService {
    void sendVerificationEmail(Long userId, String email, String name);
    void verifyEmail(String tokenValue);
}
