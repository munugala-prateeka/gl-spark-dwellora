package com.dwellora.service.impl;

import com.dwellora.exception.NotificationException;
import com.dwellora.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // EmailServiceImpl.java — replace sendManagerWelcomeEmail
    @Override
    public void sendManagerWelcomeEmail(String name, String email, String activationToken) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(email);
            mail.setSubject("Welcome to Dwellora — Activate Your Account");
            mail.setText(
                    "Hello " + name + ",\n\n"
                            + "Your apartment community has been approved on Dwellora.\n\n"
                            + "Please activate your manager account and set your password using the link below:\n\n"
                            + "http://localhost:5173/activate?token=" + activationToken + "\n\n"
                            + "This link expires in 24 hours.\n\n"
                            + "Regards,\nDwellora Team");
            mailSender.send(mail);
        } catch (Exception ex) {
            throw new NotificationException("Failed to send activation email to " + email + ": " + ex.getMessage());
        }
    }
}