package com.nec.middleware.rbacAuth.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nec.middleware.exception.ValidationException;
import com.nec.middleware.rbacAuth.auth.constants.AuthServiceConstants;
import com.nec.middleware.rbacAuth.auth.dto.request.ForgotPasswordRequest;
import com.nec.middleware.rbacAuth.auth.dto.request.LoginRequest;
import com.nec.middleware.rbacAuth.auth.dto.request.ResetPasswordRequest;
import com.nec.middleware.rbacAuth.auth.dto.request.UpdatePasswordRequest;
import com.nec.middleware.rbacAuth.auth.dto.response.ForgotPasswordResponse;
import com.nec.middleware.rbacAuth.auth.dto.response.KeycloakErrorResponse;
import com.nec.middleware.rbacAuth.auth.dto.response.ResetPasswordResponse;
import com.nec.middleware.rbacAuth.auth.dto.response.TokenResponse;
import com.nec.middleware.rbacAuth.auth.dto.response.UpdatePasswordResponse;
import com.nec.middleware.rbacAuth.auth.exception.InvalidCredentialsException;
import com.nec.middleware.rbacAuth.auth.exception.InvalidTokenException;
import com.nec.middleware.rbacAuth.auth.exception.KeycloakException;
import com.nec.middleware.rbacAuth.auth.exception.SomethingWentWrongException;
import com.nec.middleware.rbacAuth.auth.exception.UserNotFoundException;
import com.nec.middleware.rbacAuth.auth.model.NecPasswordResetToken;
import com.nec.middleware.rbacAuth.auth.repository.NecPasswordResetTokenRepository;
import com.nec.middleware.rbacAuth.auth.security.KeycloakAuthenticatedToken;
import com.nec.middleware.rbacAuth.auth.service.AuthService;
import com.nec.middleware.rbacAuth.auth.service.NotificationService;
import com.nec.middleware.rbacAuth.auth.utils.NecSecurityUtils;
import com.nec.middleware.rbacAuth.keycloak.client.response.KeycloakTokenResponse;
import com.nec.middleware.rbacAuth.keycloak.provider.KeycloakAuthProvider;
import com.nec.middleware.rbacAuth.rbac.constant.ErrorCodeConstants;
import com.nec.middleware.rbacAuth.rbac.constant.RbacConstants;
import com.nec.middleware.rbacAuth.rbac.entity.RbacUser;
import com.nec.middleware.rbacAuth.rbac.repository.RbacUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final RbacUserRepository rbacUserRepository;
    private final AuthenticationManager authenticationManager;
    private final KeycloakAuthProvider keycloakAuthProvider;
    private final ObjectMapper objectMapper;
    private final NecPasswordResetTokenRepository passwordResetTokenRepository;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.password-reset.url:http://localhost:3000/reset-password}")
    private String passwordResetBaseUrl;

    @Value("${app.password-reset.token-validity-minutes:30}")
    private long tokenValidityMinutes;

    // =========================================================================
    // LOGIN
    // =========================================================================

    @Override
    public TokenResponse login(LoginRequest request) {
        log.info("Login attempt for username/email: {}", request.getUsernameOrEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()));
            // Defensive checks: ensure AuthenticationManager returned a fully authenticated token
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("AuthenticationManager returned unauthenticated result for {}", request.getUsernameOrEmail());
                throw new InvalidCredentialsException(AuthServiceConstants.INVALID_CREDENTIALS);
            }


             if (!(authentication instanceof KeycloakAuthenticatedToken keycloakAuth)) {
                  log.error("Unexpected authentication type after login: {}",
                          authentication.getClass().getName());
                 throw new SomethingWentWrongException("AUTH", "An unexpected error occurred during authentication"); // SECURITY FIX: Don't leak method name in error message
             }

            KeycloakTokenResponse keycloakTokenResponse = keycloakAuth.getKeycloakTokenResponse();
            log.info("Login successful for user: {}", request.getUsernameOrEmail());


            return TokenResponse.builder()
                    .accessToken(keycloakTokenResponse.accessToken())
                    .refreshToken(keycloakTokenResponse.refreshToken())
                    .tokenType("Bearer")
                    .expiresIn(keycloakTokenResponse.expiresIn())
                    .passwordToBeChanged(false)
                    .build();

        } catch (AuthenticationException ex) {
            log.warn("Authentication failed for {}: {}", request.getUsernameOrEmail(), ex.getMessage());
            throw ex;
        }
    }

    // =========================================================================
    // REFRESH
    // =========================================================================

    @Override
    public TokenResponse refresh(String refreshTokenHeader) {
        log.info("Token refresh attempt");
        try {
            if (StringUtils.isEmpty(refreshTokenHeader)) {
                throw new InvalidTokenException("Invalid Authorization header");
            }
            String refreshToken = refreshTokenHeader.replace("Bearer ", "").trim();

            // Pure Keycloak refresh — no local DB lookup needed
            KeycloakTokenResponse keycloakTokens = keycloakAuthProvider.refreshToken(refreshToken);

            return TokenResponse.builder()
                    .accessToken(keycloakTokens.accessToken())
                    .refreshToken(keycloakTokens.refreshToken())
                    .tokenType(keycloakTokens.tokenType())
                    .expiresIn(keycloakTokens.expiresIn())
                    .build();

        } catch (Exception exception) {
            if (exception instanceof InvalidTokenException invalidTokenEx) {
                throw new InvalidTokenException(invalidTokenEx.getMessage());
            }
            Throwable cause = exception.getCause();
            if (cause instanceof HttpClientErrorException.Unauthorized unauthorized) {
                try {
                    String body = unauthorized.getResponseBodyAsString();
                    KeycloakErrorResponse kcErr = objectMapper.readValue(body, KeycloakErrorResponse.class);
                    kcErr.setStatus(HttpStatus.UNAUTHORIZED.value());
                    throw new KeycloakException(exception.getMessage(), kcErr);
                } catch (JsonProcessingException e) {
                    throw new SomethingWentWrongException("refresh()", e.getMessage());
                }
            }
            if (cause instanceof HttpStatusCodeException httpEx) {
                try {
                    String body = httpEx.getResponseBodyAsString();
                    KeycloakErrorResponse kcErr = objectMapper.readValue(body, KeycloakErrorResponse.class);
                    kcErr.setStatus(httpEx.getStatusCode().value());
                    throw new KeycloakException(exception.getMessage(), kcErr);
                } catch (JsonProcessingException e) {
                    throw new SomethingWentWrongException("refresh()", e.getMessage());
                }
            }
            if (cause instanceof Exception ex) {
                throw new SomethingWentWrongException(exception.getMessage(), ex.getMessage());
            }
            throw new SomethingWentWrongException("refresh()", exception.getMessage());
        }
    }

    // =========================================================================
    // FORGOT PASSWORD
    // =========================================================================

    @Override
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        log.info("Forgot-password request for: {}", request.getUsernameOrEmail());
        try {
            RbacUser user = rbacUserRepository
                    .findByEmailIgnoreCaseAndIsDeleted(request.getUsernameOrEmail(), RbacConstants.IS_DELETED_FALSE)
                    .orElseThrow(() -> new UserNotFoundException(
                            "User not found with provided username or email"));

            // Invalidate any previously issued unused reset tokens for this user
            passwordResetTokenRepository.invalidateExistingTokensForUser(user.getUserId());

            // Generate and persist reset token
            NecPasswordResetToken resetToken = NecPasswordResetToken.create(user, tokenValidityMinutes);
            passwordResetTokenRepository.save(resetToken);

            String resetLink = passwordResetBaseUrl + "/" + resetToken.getToken();
            log.info("Password reset link generated for user: {}", user.getEmail());

            boolean emailSent = false;
            try {
                notificationService.sendPasswordResetEmail(
                        user.getEmail(), user.getUserName(), resetLink);
                emailSent = true;
            } catch (Exception e) {
                log.warn("Failed to send password reset email to {}: {}",
                        user.getEmail(), e.getMessage());
            }

            return ForgotPasswordResponse.builder()
                    .message("Password reset link has been sent to the provided email")
                    .emailSent(emailSent)
                    .build();

        } catch (UserNotFoundException ex) {
            // Never leak whether email exists; always return the same generic response
            log.warn("Forgot-password request for unknown email: {}", request.getUsernameOrEmail());
            return ForgotPasswordResponse.builder()
                    .message("Password reset link has been sent to the provided email")
                    .emailSent(false)
                    .build();
        } catch (Exception ex) {
            log.error("Error in forgotPassword(): {}", ex.getMessage());
            // On any other error, return generic message without leaking internal details
            return ForgotPasswordResponse.builder()
                    .message("Password reset link has been sent to the provided email")
                    .emailSent(false)
                    .build();
        }
    }

    // =========================================================================
    // RESET PASSWORD  (token-based, unauthenticated)
    // =========================================================================

    @Override
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {
        log.info("Password reset via token");
        try {
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new ValidationException(
                        ErrorCodeConstants.PASSWORD_MISMATCH,
                        ErrorCodeConstants.PASSWORD_MISMATCH_MSG);
            }

            NecPasswordResetToken token = passwordResetTokenRepository
                    .findByToken(request.getResetToken())
                    .orElseThrow(() -> new InvalidTokenException("Reset token is invalid"));

            if (token.isExpired()) {
                throw new InvalidTokenException("Reset token has expired");
            }
            if (token.isUsed()) {
                throw new InvalidTokenException("Reset token has already been used");
            }

            RbacUser user = token.getUser();

            // Mark token as used before attempting Keycloak update
            token.setUsed(true);
            passwordResetTokenRepository.save(token);

            // Update password in Keycloak using Admin API
            keycloakAuthProvider.resetPassword(user.getKeycloakUserId(), request.getNewPassword());
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            rbacUserRepository.save(user);

            log.info("Password reset completed for user: {}", user.getEmail());

            return ResetPasswordResponse.builder()
                    .message("Password has been reset successfully")
                    .passwordUpdated(true)
                    .build();

        } catch (ValidationException | InvalidTokenException ex) {
            log.error("Validation error during resetPassword: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error in resetPassword(): {}", ex.getMessage());
            throw new SomethingWentWrongException("resetPassword()", ex.getMessage());
        }
    }

    // =========================================================================
    // UPDATE PASSWORD  (authenticated user changing own password)
    // =========================================================================

    @Override
    public UpdatePasswordResponse updatePassword(UpdatePasswordRequest request) {
        log.info("Authenticated password update attempt");
        try {
            if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
                throw new ValidationException(
                        ErrorCodeConstants.PASSWORD_MISMATCH,
                        ErrorCodeConstants.PASSWORD_MISMATCH_MSG);
            }

            RbacUser currentUser = NecSecurityUtils.getCurrentUser();

            // Re-fetch from DB to get latest state
            RbacUser user = rbacUserRepository
                    .findByUserIdAndIsDeleted(currentUser.getUserId(), RbacConstants.IS_DELETED_FALSE)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            // Verify current password by authenticating with Keycloak
            try {
                keycloakAuthProvider.authenticateAndGetToken(user.getEmail(), request.getCurrentPassword());
            } catch (InvalidCredentialsException ex) {
                log.warn("Current password validation failed for user: {}", user.getEmail());
                throw ex;
            }

            // Update password in Keycloak using Admin API
            keycloakAuthProvider.resetPassword(user.getKeycloakUserId(), request.getNewPassword());
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
            rbacUserRepository.save(user);

            log.info("Password update completed for user: {}", user.getEmail());

            return UpdatePasswordResponse.builder()
                    .message("Password has been updated successfully")
                    .passwordUpdated(true)
                    .build();

        } catch (ValidationException | UserNotFoundException ex) {
            log.error("Validation error during updatePassword: {}", ex.getMessage());
            throw ex;
        } catch (InvalidCredentialsException ex) {
            log.error("Invalid credentials during updatePassword: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error("Error in updatePassword(): {}", ex.getMessage());
            throw new SomethingWentWrongException("updatePassword()", ex.getMessage());
        }
    }

    // =========================================================================
    // LOGOUT
    // =========================================================================

    @Override
    public String logout(String refreshTokenHeader, String accessTokenHeader) {
        log.info("Logout attempt");
        try {
            if (StringUtils.isEmpty(refreshTokenHeader)) {
                throw new InvalidTokenException("Invalid Authorization header");
            }
            String refreshToken = refreshTokenHeader.replace("Bearer ", "").trim();
            String result = keycloakAuthProvider.logout(refreshToken);

            // Fire-and-forget activity log — extract user from access token
            tryLogLogout(accessTokenHeader);

            return result;

        } catch (Exception exception) {
            if (exception instanceof InvalidTokenException invalidTokenEx) {
                throw new InvalidTokenException(invalidTokenEx.getMessage());
            }
            Throwable cause = exception.getCause();
            if (cause instanceof HttpClientErrorException.Unauthorized unauthorized) {
                try {
                    String body = unauthorized.getResponseBodyAsString();
                    KeycloakErrorResponse kcErr = objectMapper.readValue(body, KeycloakErrorResponse.class);
                    kcErr.setStatus(HttpStatus.UNAUTHORIZED.value());
                    throw new KeycloakException(exception.getMessage(), kcErr);
                } catch (JsonProcessingException e) {
                    throw new SomethingWentWrongException("logout()", e.getMessage());
                }
            }
            if (cause instanceof HttpStatusCodeException httpEx) {
                try {
                    String body = httpEx.getResponseBodyAsString();
                    KeycloakErrorResponse kcErr = objectMapper.readValue(body, KeycloakErrorResponse.class);
                    kcErr.setStatus(httpEx.getStatusCode().value());
                    throw new KeycloakException(exception.getMessage(), kcErr);
                } catch (JsonProcessingException e) {
                    throw new SomethingWentWrongException("logout()", e.getMessage());
                }
            }
            if (cause instanceof Exception ex) {
                throw new SomethingWentWrongException(exception.getMessage(), ex.getMessage());
            }
            throw new SomethingWentWrongException("logout()", exception.getMessage());
        }
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    /**
     * Decodes the JWT access token payload (base64), extracts {@code preferred_username} / {@code sub},
     * and logs the logout event.  Errors are swallowed — logout must never be blocked by this.
     */
    private void tryLogLogout(String accessTokenHeader) {
        try {
            if (StringUtils.isEmpty(accessTokenHeader)) {
                log.warn("No Authorization header for logout activity log — skipping");
                return;
            }
            String jwt = accessTokenHeader.replace("Bearer ", "").trim();
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) {
                log.warn("Malformed JWT in Authorization header — skipping logout activity log");
                return;
            }
            // Decode payload (index 1) — no signature verification needed, just for display
            String padded = parts[1];
            padded = padded.length() % 4 == 0 ? padded : padded + "=".repeat(4 - padded.length() % 4);
            String payloadJson = new String(Base64.getUrlDecoder().decode(padded));
            JsonNode payload = objectMapper.readTree(payloadJson);

            String username = payload.has("preferred_username")
                    ? payload.get("preferred_username").asText()
                    : (payload.has("sub") ? payload.get("sub").asText() : null);

            if (StringUtils.isEmpty(username)) {
                log.warn("Could not extract username from JWT — skipping logout activity log");
                return;
            }

            rbacUserRepository.findByEmailIgnoreCaseAndIsDeleted(username, RbacConstants.IS_DELETED_FALSE)
                    .ifPresentOrElse(
                            user -> log.info("Logout activity logged for user: {}", user.getEmail()),
                            () -> log.warn("User '{}' from JWT not found in DB — skipping logout log", username)
                    );

        } catch (Exception e) {
            log.error("Failed to log logout activity: {}", e.getMessage());
        }
    }
}
