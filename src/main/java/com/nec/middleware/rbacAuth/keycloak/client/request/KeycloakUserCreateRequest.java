package com.nec.middleware.rbacAuth.keycloak.client.request;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record KeycloakUserCreateRequest(
        String username,
        String email,
        String firstName,
        String lastName,
        boolean enabled,
        boolean emailVerified,
        List<KeycloakCredentialRequest> credentials,
        Map<String, List<String>> attributes
) {}
