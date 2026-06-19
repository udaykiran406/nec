package com.nec.middleware.rbacAuth.keycloak.client;

import com.nec.middleware.rbacAuth.keycloak.client.request.KeycloakUserCreateRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

/**
 * IMPORTANT: Do NOT add {@code contentType} at the class level here.
 *
 * <p>Form-urlencoded token calls pass a single {@code @RequestParam MultiValueMap}
 * so every field (especially {@code password}) is encoded in the POST body.
 *
 * <p>The {@code accept = "application/json"} header is safe at class level because it
 * only affects the {@code Accept} header, not the content-type routing logic.
 */
@HttpExchange(accept = "application/json")
public interface KeycloakClient {

    /**
     * Token for confidential clients (sends {@code client_secret}).
     * Used for web clients (nec-web) and confidential service accounts.
     *
     * <p>Params are sent in the request body (form-urlencoded), NOT the query string.
     */
    @PostExchange(value = "/realms/{realm}/protocol/openid-connect/token",
            contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<Object> getToken(
            @PathVariable String realm,
            @RequestParam MultiValueMap<String, String> formData
    );

    /**
     * Token for public clients (no {@code client_secret}).
     * Used for {@code admin-cli} which is always a public client in Keycloak.
     *
     * <p>Params are sent in the request body (form-urlencoded), NOT the query string.
     */
    @PostExchange(value = "/realms/{realm}/protocol/openid-connect/token",
            contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<Object> getAdminToken(
            @PathVariable String realm,
            @RequestParam MultiValueMap<String, String> formData
    );

    /** Create a user in the realm */
    @PostExchange(value = "/admin/realms/{realm}/users",
            contentType = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Object> createUser(
            @PathVariable String realm,
            @RequestHeader("Authorization") String adminToken,
            @RequestBody KeycloakUserCreateRequest userData
    );

    /** Introspect (validate) a token */
    @PostExchange(
            value = "/realms/{realm}/protocol/openid-connect/token/introspect",
            contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    ResponseEntity<Object> introspectToken(
            @PathVariable String realm,
            @RequestParam MultiValueMap<String, String> formData
    );

    /** Refresh an access token */
    @PostExchange(
            value = "/realms/{realm}/protocol/openid-connect/token",
            contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    ResponseEntity<Object> refreshToken(
            @PathVariable String realm,
            @RequestParam MultiValueMap<String, String> formData
    );

    /** Logout / revoke refresh token */
    @PostExchange(
            value = "/realms/{realm}/protocol/openid-connect/logout",
            contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    ResponseEntity<Object> logout(
            @PathVariable String realm,
            @RequestParam MultiValueMap<String, String> formData
    );

    /** Delete a user from the realm */
    @DeleteExchange(value = "/admin/realms/{realm}/users/{userId}")
    ResponseEntity<Void> deleteUser(
            @PathVariable String realm,
            @PathVariable String userId,
            @RequestHeader("Authorization") String adminToken
    );

    /** Reset a user's password */
    @PutExchange(value = "/admin/realms/{realm}/users/{userId}/reset-password",
            contentType = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> resetPassword(
            @PathVariable String realm,
            @PathVariable String userId,
            @RequestHeader("Authorization") String adminToken,
            @RequestBody Object resetPasswordRequest
    );

    /** Set user enabled/disabled status */
    @PutExchange(value = "/admin/realms/{realm}/users/{userId}",
            contentType = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> setUserEnabled(
            @PathVariable String realm,
            @PathVariable String userId,
            @RequestHeader("Authorization") String adminToken,
            @RequestBody Object userUpdateRequest
    );
}
