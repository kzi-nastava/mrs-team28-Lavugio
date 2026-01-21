package com.backend.lavugio;

import com.backend.lavugio.service.utils.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailTests {

    @Autowired
    EmailService emailService;

    @Test
    public void testSendEmail(){
        String to = "pera@peric.com";
        String subject = "testmail";
        String body = "This is a test mail";

        emailService.sendEmail(to, subject, body);
    }
}
