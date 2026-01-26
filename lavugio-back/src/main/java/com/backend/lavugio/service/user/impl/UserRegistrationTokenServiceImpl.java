package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.user.UserRegistrationToken;
import com.backend.lavugio.repository.user.UserRegistrationTokenRepository;
import com.backend.lavugio.repository.user.RegularUserRepository;
import com.backend.lavugio.service.user.UserRegistrationTokenService;
import com.backend.lavugio.service.utils.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserRegistrationTokenServiceImpl implements UserRegistrationTokenService {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationTokenServiceImpl.class);

    @Autowired
    private UserRegistrationTokenRepository userRegistrationTokenRepository;

    @Autowired
    private RegularUserRepository regularUserRepository;

    @Autowired
    private EmailService emailService;

    private final String verificationLinkBase = "http://localhost:4200/verify-email?token=";

    @Override
    public void sendVerificationEmail(Long userId, String email, String name) {
        try {
            // Create verification token
            String tokenValue = UUID.randomUUID().toString();
            
            UserRegistrationToken token = new UserRegistrationToken();
            token.setToken(tokenValue);
            token.setUserId(userId);
            userRegistrationTokenRepository.save(token);

            // Create verification link
            String verificationLink = verificationLinkBase + tokenValue;

            // Send email
            String subject = "Verify Your Lavugio Account";
            String body = "Hello " + name + ",\n\n" +
                    "Thank you for registering with Lavugio!\n\n" +
                    "Please verify your email by clicking the link below:\n" +
                    verificationLink + "\n\n" +
                    "This link will expire in 24 hours.\n\n" +
                    "Best regards,\n" +
                    "Lavugio Team";

            emailService.sendEmail(email, subject, body);
            logger.info("Verification email sent to: {}", email);
        } catch (Exception e) {
            logger.error("Error sending verification email to {}: {}", email, e.getMessage());
        }
    }

    @Override
    public void verifyEmail(String tokenValue) {
        logger.info("Attempting to verify email with token: {}", tokenValue);
        
        if (tokenValue == null || tokenValue.trim().isEmpty()) {
            logger.error("Token is null or empty");
            throw new RuntimeException("Token cannot be empty");
        }
        
        UserRegistrationToken token = userRegistrationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> {
                    logger.warn("Verification token not found in database: {}", tokenValue);
                    return new RuntimeException("Invalid token - token not found");
                });

        logger.info("Token found. Used: {}, Expires at: {}, Current time: {}", 
                token.isUsed(), token.getExpiresAt(), java.time.LocalDateTime.now());

        if (token.isUsed()) {
            logger.warn("Token already used: {}", tokenValue);
            throw new RuntimeException("Token already used");
        }

        if (token.isExpired()) {
            logger.warn("Token expired. Expires at: {}, Current: {}", 
                    token.getExpiresAt(), java.time.LocalDateTime.now());
            throw new RuntimeException("Token expired");
        }

        RegularUser user = regularUserRepository.findById(token.getUserId())
                .orElseThrow(() -> {
                    logger.error("User not found with id: {}", token.getUserId());
                    return new RuntimeException("User not found");
                });

        // Mark user as verified
        user.setEmailVerified(true);
        regularUserRepository.save(user);
        logger.info("User email marked as verified for user id: {}", token.getUserId());

        // Mark token as used
        token.setUsed(true);
        userRegistrationTokenRepository.save(token);
        logger.info("Token marked as used");

        logger.info("Email verified successfully for user id: {}", token.getUserId());
    }
}
