package com.nec.middleware.rbacAuth.auth.service;

/**
 * Sends transactional emails for authentication flows.
 */
public interface NotificationService {

    /**
     * Sends a password-reset email containing the one-time reset link.
     *
     * @param toEmail   recipient email address
     * @param toName    recipient display name
     * @param resetLink full URL the user must visit to reset their password
     */
    void sendPasswordResetEmail(String toEmail, String toName, String resetLink);

    /**
     * Sends a welcome email to a newly registered user.
     * Not yet implemented.
     *
     * @param toEmail recipient email address
     * @param toName  recipient display name
     */
    void sendWelcomeEmail(String toEmail, String toName);
}
