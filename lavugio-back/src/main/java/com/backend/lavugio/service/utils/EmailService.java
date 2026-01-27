package com.backend.lavugio.service.utils;

import com.backend.lavugio.model.ride.Ride;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmailService {
    void sendEmailWithAttachment(String to, String subject, String body, String attachmentPath) throws MessagingException;
    void sendEmail(String to, String subject, String body);
    void sendFoundRideEmail(List<String> passengerEmails, Ride ride);
}
