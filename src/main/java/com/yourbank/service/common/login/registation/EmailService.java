package com.yourbank.service.common.login.registation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendPasswordResetEmail(String email, String token) {
        // In a real application, you would send an actual email
        String resetLink = "http://yourdomain.com/reset-password?token=" + token;
        String message = "To reset your password, click the link below:\n" + resetLink;
        logger.info("Sending password reset email to {}: {}", email, message);
    }

    public void sendPasswordChangedNotification(String email) {
        logger.info("Sending password changed notification to {}", email);
    }
}
