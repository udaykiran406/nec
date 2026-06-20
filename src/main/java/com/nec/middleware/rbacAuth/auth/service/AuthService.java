package com.nec.middleware.rbacAuth.auth.service;

import com.nec.middleware.rbacAuth.auth.dto.request.ForgotPasswordRequest;
import com.nec.middleware.rbacAuth.auth.dto.request.LoginRequest;
import com.nec.middleware.rbacAuth.auth.dto.request.ResetPasswordRequest;
import com.nec.middleware.rbacAuth.auth.dto.request.UpdatePasswordRequest;
import com.nec.middleware.rbacAuth.auth.dto.response.ForgotPasswordResponse;
import com.nec.middleware.rbacAuth.auth.dto.response.ResetPasswordResponse;
import com.nec.middleware.rbacAuth.auth.dto.response.TokenResponse;
import com.nec.middleware.rbacAuth.auth.dto.response.UpdatePasswordResponse;

public interface AuthService {

    /**
     * Validates the user exists and is active locally, then delegates
     * authentication to Keycloak and returns an access + refresh token pair.
     */
    TokenResponse login(LoginRequest request);

    /**
     * Exchanges a refresh token for a new access + refresh token pair.
     * No local DB lookup — pure Keycloak refresh.
     *
     * @param refreshTokenHeader the {@code refresh-token} header value
     */
    TokenResponse refresh(String refreshTokenHeader);

    /**
     * Sends a password-reset link to the user's registered email.
     *
     * @param request contains the usernameOrEmail to look up
     */
    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request);

    /**
     * Resets the user's password using a valid (non-expired, non-used) reset token.
     *
     * @param request contains resetToken, newPassword, confirmPassword
     */
    ResetPasswordResponse resetPassword(ResetPasswordRequest request);

    /**
     * Allows an authenticated user to change their own password.
     * Verifies the current password before applying the change.
     *
     * @param request contains currentPassword, newPassword, confirmNewPassword
     */
    UpdatePasswordResponse updatePassword(UpdatePasswordRequest request);

    /**
     * Revokes the Keycloak session for the supplied refresh token.
     *
     * @param refreshTokenHeader the {@code refresh-token} header value
     * @param accessTokenHeader  the {@code Authorization} header value (used for activity logging)
     */
    String logout(String refreshTokenHeader, String accessTokenHeader);
}
