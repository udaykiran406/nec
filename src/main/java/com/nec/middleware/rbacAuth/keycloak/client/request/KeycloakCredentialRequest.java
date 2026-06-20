package com.nec.middleware.rbacAuth.keycloak.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Keycloak credential payload for setting a user's initial password on create.
 */
public record KeycloakCredentialRequest(
        String type,
        String value,
        @JsonProperty("temporary") boolean temporary
) {
    public static KeycloakCredentialRequest password(String value) {
        return new KeycloakCredentialRequest("password", value, false);
    }
}
