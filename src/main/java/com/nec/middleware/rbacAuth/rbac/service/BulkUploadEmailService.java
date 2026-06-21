package com.nec.middleware.rbacAuth.rbac.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Optional;

/**
 * Service for sending user credentials via email after bulk upload.
 *
 * <p>Features:
 * - HTML email templates with login instructions
 * - Graceful error handling (failures logged but don't block user creation)
 * - Configurable sender email and login URL
 * - Password is marked as temporary, requiring change on first login
 *
 * <p>Configuration (application.yaml):
 * <pre>
 * mail:
 *   from-email: noreply@nec.go.tz
 *   from-name: NEC Authentication System
 * app:
 *   auth-url: https://auth.nec.go.tz/auth/realms/nec/account/password
 *   support-email: support@nec.go.tz
 * </pre>
 */
/**
 * Service for sending user credentials via email after bulk upload.
 *
 * <p>Features:
 * - HTML email templates with login instructions
 * - Graceful error handling (failures logged but don't block user creation)
 * - Configurable sender email and login URL
 * - Password is marked as temporary, requiring change on first login
 * - Optional JavaMailSender (if not configured, emails are logged instead)
 *
 * <p>Configuration (application.yaml):
 * <pre>
 * spring:
 *   mail:
 *     host: smtp.gmail.com
 *     port: 587
 *     username: your-email@gmail.com
 *     password: your-app-password
 *
 * app:
 *   mail:
 *     from-email: noreply@nec.go.tz
 *     from-name: NEC Authentication System
 *   auth-url: http://localhost:3000/login
 *   support-email: support@nec.go.tz
 * </pre>
 */
@Slf4j
@Service
public class BulkUploadEmailService {

    private final Optional<JavaMailSender> mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${app.mail.from-email:noreply@nec.go.tz}")
    private String fromEmail;

    @Value("${app.mail.from-name:NEC Authentication System}")
    private String fromName;

    @Value("${app.auth-url:http://localhost:3000/login}")
    private String authUrl;

    @Value("${app.support-email:support@nec.go.tz}")
    private String supportEmail;

    public BulkUploadEmailService(Optional<JavaMailSender> mailSender) {
        this.mailSender = mailSender;
        if (mailSender.isEmpty()) {
            log.warn("JavaMailSender not configured. Bulk upload credential emails will fail. "
                    + "Set spring.mail.host, spring.mail.username, and spring.mail.password.");
        }
    }

    /**
     * Sends username + generated password to the user after bulk upload.
     *
     * @param email recipient address
     * @param username login username (email)
     * @param generatedPassword the same password set in Keycloak
     * @return true if sent successfully, false otherwise
     */
    public boolean sendUserCredentials(String email, String username, String generatedPassword) {
        return sendUserCredentials(email, username, username, generatedPassword);
    }

    /**
     * Sends username + generated password to the user after bulk upload.
     */
    public boolean sendUserCredentials(String email, String username, String userName, String generatedPassword) {
        log.debug("Preparing credentials email: recipient={}, loginUsername={}, password={}",
                email, username, generatedPassword);

        if (mailSender.isEmpty()) {
            log.error("Email send failed — JavaMailSender bean not available. recipient={}", email);
            return false;
        }
        if (!org.springframework.util.StringUtils.hasText(mailUsername)) {
            log.error("Email send failed — spring.mail.username is not configured. recipient={}", email);
            return false;
        }

        return sendCredentialsEmail(email, userName, username, generatedPassword);
    }

    /**
     * Sends credentials to the user's email after bulk upload.
     * Failures are logged but do not block user creation.
     *
     * @return true if email sent successfully, false if failed
     */
    public boolean sendCredentialsEmail(String email, String userName, String username, String tempPassword) {
        if (mailSender.isEmpty()) {
            log.error("Email send failed — JavaMailSender not configured. recipient={}", email);
            return false;
        }
        if (!org.springframework.util.StringUtils.hasText(mailUsername)) {
            log.error("Email send failed — spring.mail.username is not configured. recipient={}", email);
            return false;
        }

        log.debug("Sending credentials email via SMTP user={} to recipient={}", mailUsername, email);

        try {
            MimeMessage mimeMessage = mailSender.get().createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(email);
            helper.setSubject("Your NEC Account Credentials - Action Required");

            String htmlContent = buildCredentialsEmailHtml(userName, username, tempPassword);
            helper.setText(htmlContent, true);

            mailSender.get().send(mimeMessage);
            log.info("Credentials email sent successfully to: {} (login username: {})", email, username);
            log.debug("Email send success for recipient={}", email);
            return true;

        } catch (MessagingException e) {
            log.error("Email send failed for recipient={}: {}", email, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Unexpected error sending credentials email to {}", email, e);
            return false;
        }
    }

    /**
     * Builds the HTML content for the credentials email.
     *
     * @param userName the user's full name
     * @param username the username for login
     * @param tempPassword the temporary password
     * @return HTML string for the email body
     */
    private String buildCredentialsEmailHtml(String userName, String username, String tempPassword) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #1e40af; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                        .content { background-color: #f9fafb; padding: 20px; border: 1px solid #e5e7eb; }
                        .credentials { background-color: #fff; padding: 15px; margin: 15px 0; border-left: 4px solid #1e40af; }
                        .credential-row { margin: 10px 0; }
                        .label { font-weight: bold; color: #1e40af; }
                        .value { font-family: monospace; background-color: #f3f4f6; padding: 5px 10px; border-radius: 3px; margin-top: 5px; }
                        .warning { background-color: #fef3c7; border-left: 4px solid #f59e0b; padding: 10px; margin: 15px 0; border-radius: 3px; }
                        .warning-title { font-weight: bold; color: #d97706; margin-bottom: 5px; }
                        .button { display: inline-block; background-color: #1e40af; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                        .footer { background-color: #f3f4f6; padding: 15px; text-align: center; font-size: 12px; color: #666; border-radius: 0 0 5px 5px; }
                        .timestamp { color: #999; font-size: 12px; margin-top: 10px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>Welcome to NEC Authentication System</h1>
                        </div>
                        <div class="content">
                            <p>Dear <strong>%s</strong>,</p>
                            
                            <p>Your account has been successfully created in the NEC system. Below are your login credentials:</p>
                            
                            <div class="credentials">
                                <div class="credential-row">
                                    <div class="label">Email / Username:</div>
                                    <div class="value">%s</div>
                                </div>
                                <div class="credential-row">
                                    <div class="label">Temporary Password:</div>
                                    <div class="value">%s</div>
                                </div>
                            </div>
                            
                            <div class="warning">
                                <div class="warning-title">⚠️ Important: Password Change Required</div>
                                <p>This is a <strong>temporary password</strong>. You must change it on your first login.</p>
                                <p>For security reasons, never share this password with anyone.</p>
                            </div>
                            
                            <h3>How to Login:</h3>
                            <ol>
                                <li>Visit: <a href="%s">%s</a></li>
                                <li>Enter your email/username and temporary password</li>
                                <li>You will be prompted to set a new password</li>
                                <li>Use a strong password with uppercase, lowercase, numbers, and special characters</li>
                            </ol>
                            
                            <p>
                                <a href="%s" class="button">Login Now</a>
                            </p>
                            
                            <h3>Need Help?</h3>
                            <p>If you experience any issues logging in or need to reset your password, please contact our support team:</p>
                            <p>
                                <strong>Email:</strong> <a href="mailto:%s">%s</a><br>
                                <strong>Response Time:</strong> Within 24 hours
                            </p>
                            
                            <p>
                                <strong>Security Tips:</strong>
                                <ul>
                                    <li>Never share your password with anyone</li>
                                    <li>Always use a secure connection (HTTPS)</li>
                                    <li>Log out after each session</li>
                                    <li>Report suspicious activity immediately</li>
                                </ul>
                            </p>
                        </div>
                        <div class="footer">
                            <p>© 2026 National Elections Commission. All rights reserved.</p>
                            <p class="timestamp">Email sent: %%DATE%%</p>
                            <p>This is an automated message. Please do not reply to this email.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                    userName,
                    username,
                    tempPassword,
                    authUrl,
                    authUrl,
                    authUrl,
                    supportEmail,
                    supportEmail
                );
    }

    /**
     * Sends an alert email if user creation succeeds but email notification fails.
     * This helps track which users didn't receive credentials.
     *
     * If JavaMailSender is not configured, this is skipped silently.
     *
     * @param email the user's email
     * @param userName the user's name
     * @param errorMessage the error message
     */
    public void sendEmailFailureAlert(String email, String userName, String errorMessage) {
        // Skip if mail not configured
        if (mailSender.isEmpty()) {
            log.debug("Mail not configured. Email failure alert for {} would be sent (if mail was configured): {}",
                    email, errorMessage);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(supportEmail);
            message.setSubject("⚠️ Bulk Upload Alert: Failed to Send Credentials Email");
            message.setText(
                    "User creation succeeded but credentials email delivery failed:\n\n" +
                    "User Email: " + email + "\n" +
                    "User Name: " + userName + "\n" +
                    "Error: " + errorMessage + "\n\n" +
                    "Action Required: Manually send credentials to the user or resend via password reset."
            );
            mailSender.get().send(message);
            log.info("Email failure alert sent to support team for user: {}", email);
        } catch (Exception e) {
            log.error("Failed to send email failure alert", e);
        }
    }
}

