package com.dwellora.service;

public interface EmailService {

    void sendManagerWelcomeEmail(String name, String email, String activationToken);

    void sendResidentWelcomeEmail(String name, String email, String activationToken);
}