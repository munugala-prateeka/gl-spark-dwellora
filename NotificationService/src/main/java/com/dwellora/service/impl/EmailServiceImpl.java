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

    @Override
    public void sendManagerWelcomeEmail(String name, String email) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(email);
            mail.setSubject("Welcome to Dwellora");
            mail.setText(
                    "Hello "
                            + name
                            + ",\n\n"
                            + "Your apartment has been approved.\n\n"
                            + "You can now login to Dwellora.\n\n"
                            + "Default Password : manager123\n\n"
                            + "Regards,\n"
                            + "Dwellora Team");

            mailSender.send(mail);
        } catch (Exception ex) {
            throw new NotificationException(
                    "Failed to send welcome email to " + email + ": " + ex.getMessage());
        }
    }
}