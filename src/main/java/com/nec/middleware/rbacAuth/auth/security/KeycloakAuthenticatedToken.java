package com.nec.middleware.rbacAuth.auth.security;

import com.nec.middleware.rbacAuth.keycloak.client.response.KeycloakTokenResponse;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Holds a successful Keycloak token response after {@link org.springframework.security.authentication.AuthenticationManager}
 * has validated username/password credentials.
 */
@Getter
public class KeycloakAuthenticatedToken extends UsernamePasswordAuthenticationToken {

    private final KeycloakTokenResponse keycloakTokenResponse;

    public KeycloakAuthenticatedToken(
            Object principal,
            Object credentials,
            Collection<? extends GrantedAuthority> authorities,
            KeycloakTokenResponse keycloakTokenResponse) {
        super(principal, credentials, authorities);
        this.keycloakTokenResponse = keycloakTokenResponse;
    }
}
