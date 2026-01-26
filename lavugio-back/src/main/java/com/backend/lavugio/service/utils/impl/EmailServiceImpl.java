package com.backend.lavugio.service.utils.impl;

import com.backend.lavugio.service.utils.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    @org.springframework.beans.factory.annotation.Value("${spring.mail.from}")
    private String fromEmail;

    /**
     * Šalje običan email sa body tekstom
     *
     * @param to - email primaoca
     * @param subject - naslov emaila
     * @param body - sadržaj emaila
     */
    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            logger.info("Sending email to: {}, subject: {}", to, subject);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to: {}, error: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Šalje email sa attachmentom
     *
     * @param to - email primaoca
     * @param subject - naslov emaila
     * @param body - sadržaj emaila
     * @param attachmentPath - putanja do fajla koji se šalje
     */
    @Async
    public void sendEmailWithAttachment(String to, String subject, String body, String attachmentPath)
            throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);

        // Dodaj attachment
        FileSystemResource file = new FileSystemResource(new File(attachmentPath));
        helper.addAttachment(file.getFilename(), file);

        mailSender.send(message);
    }
}

