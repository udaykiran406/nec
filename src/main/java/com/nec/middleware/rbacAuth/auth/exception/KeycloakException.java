package com.nec.middleware.rbacAuth.auth.exception;

import com.nec.middleware.rbacAuth.auth.dto.response.KeycloakErrorResponse;
import lombok.Getter;

/**
 * Thrown when Keycloak returns a structured error body (e.g. invalid_grant, invalid_client).
 */
@Getter
public class KeycloakException extends RuntimeException {
    private final KeycloakErrorResponse keycloakErrorResponse;
    private final String errorTraceMethod;

    public KeycloakException(String errorTraceMethod, KeycloakErrorResponse keycloakErrorResponse) {
        super(keycloakErrorResponse.getErrorDescription());
        this.keycloakErrorResponse = keycloakErrorResponse;
        this.errorTraceMethod = errorTraceMethod;
    }
}
