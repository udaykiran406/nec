package com.nec.middleware.rbacAuth.keycloak.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.nec.middleware.rbacAuth.keycloak.constants.KeycloakConstants.CLIENT_ID_HEADER;
import static com.nec.middleware.rbacAuth.keycloak.constants.KeycloakConstants.CLIENT_SECRET_HEADER;

@Component
public class ClientCredentialsUtil {

    public ClientCredentials getClientCredentials(HttpServletRequest request) {
        String clientId     = request.getHeader(CLIENT_ID_HEADER); // SECURITY FIX: Read credentials from HTTP headers, not server-side attributes
        String clientSecret = request.getHeader(CLIENT_SECRET_HEADER); // SECURITY FIX: Read credentials from HTTP headers, not server-side attributes

        if (!StringUtils.hasText(clientId) || !StringUtils.hasText(clientSecret)) {
            throw new IllegalArgumentException("Client credentials (client-id / client-secret) not found in request headers");
        }
        return new ClientCredentials(clientId, clientSecret);
    }

    /**
     * Retrieves client credentials from HTTP request headers, falling back to provided defaults if not found.
     *
     * @param request The HTTP request
     * @param defaultClientId The default client ID to use if not in headers
     * @param defaultClientSecret The default client secret to use if not in headers
     * @return ClientCredentials with values from headers or defaults
     */
    public ClientCredentials getClientCredentials(HttpServletRequest request, String defaultClientId, String defaultClientSecret) {
        String clientId     = request.getHeader(CLIENT_ID_HEADER); // SECURITY FIX: Read credentials from HTTP headers, not server-side attributes
        String clientSecret = request.getHeader(CLIENT_SECRET_HEADER); // SECURITY FIX: Read credentials from HTTP headers, not server-side attributes

        // Use provided header values if present, otherwise use defaults
        String finalClientId     = StringUtils.hasText(clientId) ? clientId : defaultClientId;
        String finalClientSecret = StringUtils.hasText(clientSecret) ? clientSecret : defaultClientSecret;

        if (!StringUtils.hasText(finalClientId) || !StringUtils.hasText(finalClientSecret)) {
            throw new IllegalArgumentException("Client credentials (client-id / client-secret) not found in request headers or configuration");
        }
        return new ClientCredentials(finalClientId, finalClientSecret);
    }
}
