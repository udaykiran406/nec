package com.nec.middleware.rbacAuth.auth.controller;

import com.nec.middleware.rbacAuth.auth.dto.request.ForgotPasswordRequest;
import com.nec.middleware.rbacAuth.auth.dto.request.LoginRequest;
import com.nec.middleware.rbacAuth.auth.dto.request.ResetPasswordRequest;
import com.nec.middleware.rbacAuth.auth.dto.request.UpdatePasswordRequest;
import com.nec.middleware.rbacAuth.auth.dto.response.*;
import com.nec.middleware.rbacAuth.auth.service.AuthService;
import com.nec.middleware.rbacAuth.auth.utils.Authorize;
import com.nec.middleware.rbacAuth.auth.utils.NecSecurityUtils;
import com.nec.middleware.rbacAuth.rbac.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.nec.middleware.rbacAuth.auth.constants.AuthServiceConstants.API_SUCCESS_MESSAGE;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Keycloak-backed login, token refresh, logout and password management")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login", description = "Authenticate with email and password. Returns access + refresh token pair.")
    // POST /api/v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @Operation(summary = "Refresh token", description = "Exchange a valid refresh token for a new access + refresh token pair.")
    // POST /api/v1/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @RequestHeader("refresh-token") String refreshToken) {
        TokenResponse response = authService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.ok("Token refreshed successfully", response));
    }

    @Operation(summary = "Logout", description = "Revoke the Keycloak session for the supplied refresh token.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader("refresh-token") String refreshToken,
            @RequestHeader(value = "Authorization", required = false) String accessToken) {
        String result = authService.logout(refreshToken, accessToken);
        return ResponseEntity.ok(ApiResponse.ok("Logout successful", result));
    }

    @Operation(summary = "Forgot password", description = "Send a password-reset link to the user's registered email.")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResponse>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        ForgotPasswordResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.ok("Password reset link sent", response));
    }

    @Operation(summary = "Reset password", description = "Reset the user's password using a valid reset token received by email.")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<ResetPasswordResponse>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        ResetPasswordResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.ok("Password reset successfully", response));
    }

    @Operation(summary = "Update password", description = "Authenticated user changes their own password. Requires a valid Bearer token.")
    @PutMapping("/update-password")
    public ResponseEntity<ApiResponse<UpdatePasswordResponse>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request) {
        UpdatePasswordResponse response = authService.updatePassword(request);
        return ResponseEntity.ok(ApiResponse.ok("Password updated successfully", response));
    }

    @PostMapping("/user-context")
    @Authorize()
    public ResponseEntity<ApiResponse<NecUserContextDTO>> getUserContext() {
        return ResponseEntity.ok(ApiResponse.ok(API_SUCCESS_MESSAGE, NecSecurityUtils.getUserContext()));
    }
}
