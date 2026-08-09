package com.dwellora.service;

/**
 * Service interface defining transactional email delivery operations.
 */
public interface EmailService {

    /**
     * Sends an account activation welcome email to a newly created manager.
     */
    void sendManagerWelcomeEmail(String name, String email, String activationToken);

    /**
     * Sends an account activation welcome email to a newly created resident.
     */
    void sendResidentWelcomeEmail(String name, String email, String activationToken);
}