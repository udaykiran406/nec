package com.nec.middleware.rbacAuth.auth.service.impl;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import com.nec.middleware.rbacAuth.auth.exception.SomethingWentWrongException;
import com.nec.middleware.rbacAuth.auth.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailjetNotificationService implements NotificationService {

    private final MailjetClient mailjetClient;

    @Value("${mailjet.from.email}")
    private String fromEmail;

    @Value("${mailjet.from.name}")
    private String fromName;

    @Override
    public void sendPasswordResetEmail(String toEmail, String toName, String resetLink) {
        try {
            String htmlPart = buildPasswordResetHtml(toName, resetLink);
            String textPart = "Use this link to reset your password: " + resetLink;

            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", fromEmail)
                                            .put("Name", fromName))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", toEmail)
                                                    .put("Name", toName)))
                                    .put(Emailv31.Message.SUBJECT, "Reset Your Password")
                                    .put(Emailv31.Message.HTMLPART, htmlPart)
                                    .put(Emailv31.Message.TEXTPART, textPart)));

            MailjetResponse response = mailjetClient.post(request);
            if (response.getStatus() != 200) {
                log.error("Mailjet password reset email failed for {}: status={}, body={}",
                        toEmail, response.getStatus(), response.getData());
                throw new SomethingWentWrongException(
                        "sendPasswordResetEmail()",
                        "Failed to send password reset email");
            }
            log.info("Password reset email sent to {}", toEmail);
        } catch (MailjetException e) {
            log.error("Mailjet error sending password reset email to {}: {}", toEmail, e.getMessage(), e);
            throw new SomethingWentWrongException(
                    "sendPasswordResetEmail()",
                    "Failed to send password reset email");
        } catch (SomethingWentWrongException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error sending password reset email to {}: {}", toEmail, e.getMessage(), e);
            throw new SomethingWentWrongException(
                    "sendPasswordResetEmail()",
                    "Failed to send password reset email");
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String toName) {
        log.info("Welcome email not yet implemented");
    }

    private String buildPasswordResetHtml(String toName, String resetLink) {
        String displayName = (toName != null && !toName.isBlank()) ? toName : "User";
        return "<html><body>"
                + "<p>Hello " + displayName + ",</p>"
                + "<p>We received a request to reset your password. Click the link below to proceed:</p>"
                + "<p><a href=\"" + resetLink + "\">Reset Password</a></p>"
                + "<p>If you did not request this, you can safely ignore this email.</p>"
                + "</body></html>";
    }
}
