package com.nec.middleware.rbacAuth.keycloak.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nec.middleware.rbacAuth.auth.constants.AuthServiceConstants;
import com.nec.middleware.rbacAuth.auth.dto.response.KeycloakErrorResponse;
import com.nec.middleware.rbacAuth.auth.exception.InvalidCredentialsException;
import com.nec.middleware.rbacAuth.keycloak.KeycloakProperties;
import com.nec.middleware.rbacAuth.keycloak.client.KeycloakClient;
import com.nec.middleware.rbacAuth.keycloak.client.KeycloakTokenClient;
import com.nec.middleware.rbacAuth.keycloak.client.request.KeycloakUserCreateRequest;
import com.nec.middleware.rbacAuth.keycloak.client.request.KeycloakCredentialRequest;
import com.nec.middleware.rbacAuth.keycloak.client.response.KeycloakTokenResponse;
import com.nec.middleware.rbacAuth.keycloak.util.ClientCredentials;
import com.nec.middleware.rbacAuth.keycloak.util.ClientCredentialsUtil;
import com.nec.middleware.rbacAuth.keycloak.util.KeycloakFormData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAuthProvider {

    private final KeycloakClient keycloakClient;
    private final KeycloakTokenClient keycloakTokenClient;
    private final KeycloakProperties keycloakProperties;
    private final ObjectMapper objectMapper;
    private final ClientCredentialsUtil clientCredentialsUtil;
    private final HttpServletRequest request;

    // -------------------------------------------------------------------------
    // Create user
    // -------------------------------------------------------------------------

    /**
     * Creates a new user in the Keycloak realm and returns the new Keycloak user ID.
     */
    public String createUserInKeycloak(KeycloakUserCreateRequest kcUser) {
        log.info("Creating user in Keycloak: {}", kcUser.username());
        try {
            String adminBearer = fetchAdminBearerToken();

            ResponseEntity<Object> createRes = keycloakClient.createUser(
                    keycloakProperties.realmName(), adminBearer, kcUser);

            Response response = Response.status(createRes.getStatusCode().value())
                    .location(createRes.getHeaders().getLocation())
                    .build();

            String keycloakId = CreatedResponseUtil.getCreatedId(response);
            if (keycloakId == null || keycloakId.isBlank()) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Keycloak did not return a created user ID in createUserInKeycloak()");
            }

            // Keycloak often ignores credentials on POST /users; set password explicitly.
            applyInitialPassword(keycloakId, kcUser);

            log.info("Keycloak user created, id={}", keycloakId);
            return keycloakId;

        } catch (HttpClientErrorException ex) {
            log.error("Keycloak createUser failed: status={}, body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new ResponseStatusException(ex.getStatusCode(), "createUserInKeycloak(): " + ex.getMessage(), ex);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Keycloak createUser unexpected error: {}", ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "createUserInKeycloak(): " + ex.getMessage(), ex);
        }
    }

    // -------------------------------------------------------------------------
    // Validate token + extract sub (user ID)
    // -------------------------------------------------------------------------

    /**
     * Introspects the supplied access token and returns the Keycloak {@code sub} claim (user ID).
     *
     * @throws ResponseStatusException 401 if the token is inactive / invalid
     */
    public String validateTokenGetUid(String accessToken) {
        log.info("Validating token and extracting Keycloak user ID");
        try {
            ClientCredentials creds = clientCredentialsUtil.getClientCredentials(request,
                    keycloakProperties.webClientId(), keycloakProperties.webClientSecret());

            ResponseEntity<Object> response = keycloakTokenClient.postIntrospectForm(
                    keycloakProperties.realmName(),
                    KeycloakFormData.introspect(
                            creds.getClientId(),
                            creds.getClientSecret(),
                            accessToken)
            );

            Map<String, Object> tokenInfo = objectMapper.convertValue(
                    response.getBody(), new TypeReference<>() {});

            if (!Boolean.TRUE.equals(tokenInfo.get("active"))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "TOKEN_INACTIVE_OR_INVALID");
            }

            String userId = (String) tokenInfo.get("sub");
            if (userId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token missing user ID (sub)");
            }
            return userId;

        } catch (HttpClientErrorException ex) {
            log.error("Token introspection failed: status={}, body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new ResponseStatusException(ex.getStatusCode(), "validateTokenGetUid(): " + ex.getMessage(), ex);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Token introspection unexpected error", ex);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "validateTokenGetUid(): " + ex.getMessage(), ex);
        }
    }

    // -------------------------------------------------------------------------
    // Generate token (Direct Password Grant)
    // -------------------------------------------------------------------------

    /**
     * Authenticates username/password against Keycloak and returns tokens only after
     * the token endpoint responds with a non-empty {@code access_token}.
     *
     * @throws InvalidCredentialsException when credentials are rejected by Keycloak
     */
    public KeycloakTokenResponse authenticateAndGetToken(String username, String password) {
        log.info("Authenticating user with Keycloak: {}", username);
        try {
            ClientCredentials creds = clientCredentialsUtil.getClientCredentials(request,
                    keycloakProperties.webClientId(), keycloakProperties.webClientSecret());

            log.debug("Keycloak token request: realm={}, clientId={}, grantType={}, username={}, passwordLength={}",
                    keycloakProperties.realmName(), creds.getClientId(), keycloakProperties.grantType(), username,
                    password != null ? password.length() : 0);

            ResponseEntity<Object> tokenRes = keycloakTokenClient.postTokenForm(
                    keycloakProperties.realmName(),
                    KeycloakFormData.passwordGrant(
                            creds.getClientId(),
                            creds.getClientSecret(),
                            keycloakProperties.grantType(),
                            username,
                            password)
            );

            log.debug("Keycloak token response status: {}, body class: {}",
                    tokenRes.getStatusCode(),
                    tokenRes.getBody() != null ? tokenRes.getBody().getClass().getName() : "null");

            if (!tokenRes.getStatusCode().is2xxSuccessful()) {
                log.warn("Keycloak token endpoint returned non-success status {} for user {}",
                        tokenRes.getStatusCode(), username);
                throw new InvalidCredentialsException(AuthServiceConstants.INVALID_CREDENTIALS);
            }

            KeycloakTokenResponse token = objectMapper.convertValue(tokenRes.getBody(), KeycloakTokenResponse.class);

            log.debug("Keycloak token response parsed: accessTokenPresent={}, refreshTokenPresent={}",
                    token != null && StringUtils.hasText(token.accessToken()),
                    token != null && StringUtils.hasText(token.refreshToken()));

            if (token == null || !StringUtils.hasText(token.accessToken())) {
                log.error("Keycloak returned HTTP {} but token is null or missing access_token for user {}",
                        tokenRes.getStatusCode(), username);
                throw new InvalidCredentialsException(AuthServiceConstants.INVALID_CREDENTIALS);
            }

            log.info("Keycloak authentication successful for user: {}", username);
            return token;

        } catch (InvalidCredentialsException ex) {
            throw ex;
        } catch (RestClientResponseException ex) {
            throw keycloakTokenFailure(username, ex.getStatusCode().value(), ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            log.error("Unexpected error during Keycloak authentication for user {}", username, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "authenticateAndGetToken(): " + ex.getMessage(), ex);
        }
    }

    /**
     * @deprecated Use {@link #authenticateAndGetToken(String, String)} for login flows.
     */
    @Deprecated
    public KeycloakTokenResponse generateToken(String username, String password) {
        return authenticateAndGetToken(username, password);
    }

    // -------------------------------------------------------------------------
    // Refresh token
    // -------------------------------------------------------------------------

    /**
     * Exchanges a refresh token for a new access token.
     */
    public KeycloakTokenResponse refreshToken(String refreshToken) {
        log.info("Refreshing access token");
        try {
            ClientCredentials creds = clientCredentialsUtil.getClientCredentials(request,
                    keycloakProperties.webClientId(), keycloakProperties.webClientSecret());

            ResponseEntity<Object> tokenRes = keycloakTokenClient.postTokenForm(
                    keycloakProperties.realmName(),
                    KeycloakFormData.refreshGrant(
                            creds.getClientId(),
                            creds.getClientSecret(),
                            refreshToken)
            );

            KeycloakTokenResponse token = objectMapper.convertValue(tokenRes.getBody(), KeycloakTokenResponse.class);
            log.info("Token refreshed successfully");
            return token;

        } catch (HttpClientErrorException.Unauthorized ex) {
            log.error("Refresh token invalid: {}", ex.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "refreshToken(): " + ex.getMessage(), ex);
        } catch (HttpStatusCodeException ex) {
            log.error("Refresh error {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new ResponseStatusException(ex.getStatusCode(), "refreshToken(): " + ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Unexpected refresh error", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "refreshToken(): " + ex.getMessage(), ex);
        }
    }

    // -------------------------------------------------------------------------
    // Logout
    // -------------------------------------------------------------------------

    /**
     * Revokes the session associated with the supplied refresh token.
     */
    public String logout(String refreshToken) {
        log.info("Logging out user session");
        try {
            ClientCredentials creds = clientCredentialsUtil.getClientCredentials(request,
                    keycloakProperties.webClientId(), keycloakProperties.webClientSecret());

            keycloakTokenClient.postLogoutForm(
                    keycloakProperties.realmName(),
                    KeycloakFormData.logout(
                            creds.getClientId(),
                            creds.getClientSecret(),
                            refreshToken)
            );

            log.info("Logout successful");
            return "LOGOUT_SUCCESS";

        } catch (HttpClientErrorException.Unauthorized ex) {
            log.error("Logout failed – invalid token: {}", ex.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "logout(): " + ex.getMessage(), ex);
        } catch (HttpStatusCodeException ex) {
            log.error("Logout error {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new ResponseStatusException(ex.getStatusCode(), "logout(): " + ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Unexpected logout error", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "logout(): " + ex.getMessage(), ex);
        }
    }

    // -------------------------------------------------------------------------
    // Delete user (rollback helper)
    // -------------------------------------------------------------------------

    /**
     * Deletes a user from Keycloak. Errors are logged but not re-thrown so that
     * this method can be safely used inside rollback / compensation logic.
     */
    public void deleteUserInKeycloak(String keycloakUserId) {
        log.info("Deleting Keycloak user id={}", keycloakUserId);
        try {
            String adminBearer = fetchAdminBearerToken();
            keycloakClient.deleteUser(keycloakProperties.realmName(), keycloakUserId, adminBearer);
            log.info("Keycloak user {} deleted", keycloakUserId);
        } catch (HttpClientErrorException ex) {
            log.error("Keycloak deleteUser failed: status={}, body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
        } catch (Exception ex) {
            log.error("Keycloak deleteUser unexpected error: {}", ex.getMessage());
        }
    }

    /**
     * Resets a user's password in Keycloak. Calls the admin REST API endpoint to set a new password.
     *
     * @param keycloakUserId the Keycloak user ID
     * @param newPassword the new password
     * @throws ResponseStatusException on failure
     */
    public void resetPassword(String keycloakUserId, String newPassword) {
        resetPassword(keycloakUserId, newPassword, false);
    }

    /**
     * Resets a user's password in Keycloak.
     *
     * @param temporary when true, user must change password on next login (blocks direct-grant until changed)
     */
    public void resetPassword(String keycloakUserId, String newPassword, boolean temporary) {
        log.info("Resetting password for Keycloak user id={} (temporary={})", keycloakUserId, temporary);
        try {
            String adminBearer = fetchAdminBearerToken();

            Map<String, Object> resetRequest = Map.of(
                    "type", "password",
                    "value", newPassword,
                    "temporary", temporary
            );

            keycloakClient.resetPassword(
                    keycloakProperties.realmName(),
                    keycloakUserId,
                    adminBearer,
                    resetRequest
            );
            log.info("Keycloak password reset successful for user id={}", keycloakUserId);
        } catch (HttpClientErrorException ex) {
            log.error("Keycloak resetPassword failed: status={}, body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new ResponseStatusException(ex.getStatusCode(), "resetPassword(): " + ex.getMessage(), ex);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Keycloak resetPassword unexpected error: {}", ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "resetPassword(): " + ex.getMessage(), ex);
        }
    }

    /**
     * Sets a user's enabled/disabled status in Keycloak.
     *
     * @param keycloakUserId the Keycloak user ID
     * @param enabled true to enable the user, false to disable
     * @throws ResponseStatusException on failure
     */
    public void setUserEnabled(String keycloakUserId, boolean enabled) {
        log.info("Setting user id={} enabled={}", keycloakUserId, enabled);
        try {
            String adminBearer = fetchAdminBearerToken();

            Map<String, Object> updateRequest = Map.of("enabled", enabled);

            keycloakClient.setUserEnabled(
                    keycloakProperties.realmName(),
                    keycloakUserId,
                    adminBearer,
                    updateRequest
            );
            log.info("Keycloak user {} status updated to enabled={}", keycloakUserId, enabled);
        } catch (HttpClientErrorException ex) {
            log.error("Keycloak setUserEnabled failed: status={}, body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new ResponseStatusException(ex.getStatusCode(), "setUserEnabled(): " + ex.getMessage(), ex);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Keycloak setUserEnabled unexpected error: {}", ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "setUserEnabled(): " + ex.getMessage(), ex);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Fetches a Keycloak admin bearer token using the Resource Owner Password Credentials flow.
     *
     * <p>{@code admin-cli} is a PUBLIC client — it never has a {@code client_secret}.
     * Sending a {@code client_secret} for a public client causes Keycloak 26 to return
     * {@code 401 invalid_grant: Invalid user credentials}.  This method therefore calls
     * {@link com.nec.middleware.rbacAuth.keycloak.client.KeycloakClient#getAdminToken}
     * which intentionally omits {@code client_secret}.
     *
     * <p>Parameters are POSTed as {@code application/x-www-form-urlencoded} body — NOT
     * appended to the URL.  Spring 6 routes {@code @RequestParam} values to the form body
     * only when no class-level {@code contentType} conflicts with the method-level value;
     * see the {@link com.nec.middleware.rbacAuth.keycloak.client.KeycloakClient} interface javadoc
     * for the full explanation.
     */
    private RuntimeException keycloakTokenFailure(String username, int status, String body, Exception cause) {
        log.warn("Keycloak token endpoint error for user {}: status={}, body={}", username, status, body);

        KeycloakErrorResponse keycloakError = parseKeycloakErrorBody(body);
        log.debug("Keycloak error response: error={}, errorDescription={}", keycloakError.getError(), keycloakError.getErrorDescription());

        if (isInvalidCredentialsError(status, keycloakError)) {
            log.warn("Identified as invalid credentials error. Throwing InvalidCredentialsException for user {}", username);
            throw new InvalidCredentialsException(AuthServiceConstants.INVALID_CREDENTIALS);
        }

        throw new ResponseStatusException(
                HttpStatus.valueOf(status),
                "authenticateAndGetToken(): Keycloak rejected token request",
                cause);
    }

    private KeycloakErrorResponse parseKeycloakErrorBody(String body) {
        if (!StringUtils.hasText(body)) {
            return new KeycloakErrorResponse();
        }
        try {
            return objectMapper.readValue(body, KeycloakErrorResponse.class);
        } catch (JsonProcessingException ex) {
            log.debug("Could not parse Keycloak error body as JSON: {}", body);
            return new KeycloakErrorResponse();
        }
    }

    private boolean isInvalidCredentialsError(int status, KeycloakErrorResponse keycloakError) {
        if (status == HttpStatus.UNAUTHORIZED.value()) {
            return true;
        }
        if (status != HttpStatus.BAD_REQUEST.value()) {
            return false;
        }
        if ("invalid_grant".equalsIgnoreCase(keycloakError.getError())) {
            String description = keycloakError.getErrorDescription();
            if (description != null) {
                String lower = description.toLowerCase();
                if (lower.contains("account is not fully set up")
                        || lower.contains("account is not fullyset up")) {
                    return false;
                }
                if (lower.contains("invalid user credentials")) {
                    return true;
                }
            }
            // Default invalid_grant to wrong password only when no other description matches.
            return description == null || description.isBlank();
        }
        String description = keycloakError.getErrorDescription();
        return description != null
                && description.toLowerCase().contains("invalid user credentials");
    }

    /**
     * Keycloak frequently does not persist credentials embedded in the create-user payload.
     * Always call reset-password after create so direct-grant login works.
     */
    private void applyInitialPassword(String keycloakUserId, KeycloakUserCreateRequest kcUser) {
        if (kcUser.credentials() == null || kcUser.credentials().isEmpty()) {
            return;
        }
        KeycloakCredentialRequest credential = kcUser.credentials().getFirst();
        if (!"password".equalsIgnoreCase(credential.type())
                || !StringUtils.hasText(credential.value())) {
            return;
        }
        resetPassword(keycloakUserId, credential.value(), credential.temporary());
        if (!credential.temporary()) {
            clearRequiredActions(keycloakUserId, kcUser.emailVerified());
        }
    }

    private void clearRequiredActions(String keycloakUserId, boolean emailVerified) {
        try {
            String adminBearer = fetchAdminBearerToken();
            Map<String, Object> updateRequest = Map.of(
                    "requiredActions", List.of(),
                    "emailVerified", emailVerified
            );
            keycloakClient.setUserEnabled(
                    keycloakProperties.realmName(),
                    keycloakUserId,
                    adminBearer,
                    updateRequest
            );
            log.debug("Cleared required actions for Keycloak user id={}", keycloakUserId);
        } catch (Exception ex) {
            log.warn("Failed to clear required actions for Keycloak user id={}: {}",
                    keycloakUserId, ex.getMessage());
        }
    }

    private String fetchAdminBearerToken() {
        String adminRealm  = keycloakProperties.requireAdminRealm();
        String adminClient = keycloakProperties.adminClientId();
        String adminUser   = keycloakProperties.adminUsername();
        String grantType   = keycloakProperties.grantType();

        log.info("Requesting admin token: realm={}, client_id={}, grant_type={}, username={}", adminRealm, adminClient, grantType, adminUser);

        try {
            ResponseEntity<Object> tokenRes = keycloakTokenClient.postTokenForm(
                    adminRealm,
                    KeycloakFormData.adminPasswordGrant(
                            adminClient,
                            grantType,
                            adminUser,
                            keycloakProperties.adminPassword())
            );
            KeycloakTokenResponse token = objectMapper.convertValue(tokenRes.getBody(), KeycloakTokenResponse.class);
            if (token == null || token.accessToken() == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Keycloak admin token response was empty or missing access_token");
            }
            log.info("Admin token obtained successfully for realm={}", adminRealm);
            return "Bearer " + token.accessToken();
        } catch (HttpClientErrorException ex) {
            log.error("Admin token request failed: status={}, realm={}, client_id={}, username={}, body={}",
                    ex.getStatusCode(), adminRealm, adminClient, adminUser,
                    ex.getResponseBodyAsString());
            throw new ResponseStatusException(ex.getStatusCode(),
                    "fetchAdminBearerToken(): " + ex.getMessage(), ex);
        }
    }
}
