package com.nec.middleware.rbacAuth.keycloak.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nec.middleware.rbacAuth.keycloak.KeycloakProperties;
import com.nec.middleware.rbacAuth.keycloak.client.KeycloakClient;
import com.nec.middleware.rbacAuth.keycloak.client.KeycloakTokenClient;
import com.nec.middleware.rbacAuth.keycloak.util.ClientCredentialsUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test to ensure client credentials used for Keycloak calls come only from server-side configuration.
 */
public class KeycloakClientCredentialsConfigOnlyTest {

    @Test
    public void clientCredentialsAreTakenFromConfiguration_not_from_headers() {
        // Arrange: configure server-side Keycloak properties
        KeycloakProperties kp = new KeycloakProperties(
                "http://localhost:8080", // baseUrl
                null, // serverUrl
                "realm", // realmName
                "master", // adminRealm
                "password", // grantType
                "admin-client", // adminClientId
                "admin-secret", // adminClientSecret
                "admin-user", // adminUsername
                "admin-pass", // adminPassword
                "configured-client-id", // webClientId (the secure configured value)
                "configured-client-secret" // webClientSecret
        );

        KeycloakClient kcClient = mock(KeycloakClient.class);
        KeycloakTokenClient tokenClient = mock(KeycloakTokenClient.class);

        // No credential headers present — util falls back to config values (webClientId / webClientSecret)
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        when(httpRequest.getHeader(org.mockito.ArgumentMatchers.anyString())).thenReturn(null);

        ObjectMapper objectMapper = new ObjectMapper();
        ClientCredentialsUtil credsUtil = new ClientCredentialsUtil();

        KeycloakAuthProvider provider = new KeycloakAuthProvider(kcClient, tokenClient, kp, objectMapper, credsUtil, httpRequest);

        // Capture the form data sent to the token endpoint
        ArgumentCaptor<MultiValueMap> captor = ArgumentCaptor.forClass(MultiValueMap.class);

        when(tokenClient.postTokenForm(eq(kp.realmName()), captor.capture()))
                .thenReturn(ResponseEntity.ok(Map.of("access_token", "tok", "refresh_token", "ref")));

        // Act: perform authentication (no headers are provided to provider; attacker headers would be ignored)
        provider.authenticateAndGetToken("user", "password");

        // Assert: the form contained the configured (server-side) client credentials
        MultiValueMap form = captor.getValue();
        assertEquals("configured-client-id", form.getFirst("client_id"));
        assertEquals("configured-client-secret", form.getFirst("client_secret"));
    }
}

