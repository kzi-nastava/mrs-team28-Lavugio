package com.backend.lavugio.service.utils.impl;

import com.backend.lavugio.service.utils.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest  // uključuje Spring kontekst
class EmailServiceImplTest {

    @Autowired
    private EmailService emailService; // Spring će ubaciti mailSender

    @Test
    void sendRealEmail() {
        String to = "testmail@gmail.com";
        String subject = "Integration Test Email";
        String body = "Hello, this is a test from integration test";

        emailService.sendEmail(to, subject, body); // mailSender više nije null
    }
}
