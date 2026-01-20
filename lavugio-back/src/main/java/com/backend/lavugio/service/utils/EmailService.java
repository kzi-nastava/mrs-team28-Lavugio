package com.backend.lavugio.service.utils;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendEmailWithAttachment(String to, String subject, String body, String attachmentPath) throws MessagingException;
    void sendEmail(String to, String subject, String body);
}
