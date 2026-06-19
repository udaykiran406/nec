package com.nec.middleware.rbacAuth.keycloak.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ClientCredentialsUtil {

    /**
     * Returns server-configured Keycloak client credentials. Client-supplied
     * header overrides are not accepted.
     */
    public ClientCredentials getClientCredentials(
            HttpServletRequest request,
            String defaultClientId,
            String defaultClientSecret) {

        if (!StringUtils.hasText(defaultClientId) || !StringUtils.hasText(defaultClientSecret)) {
            throw new IllegalArgumentException("Client credentials not found in server configuration");
        }
        return new ClientCredentials(defaultClientId, defaultClientSecret);
    }
}
