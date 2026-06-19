package com.nec.middleware.rbacAuth.keycloak.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.nec.middleware.rbacAuth.keycloak.constants.KeycloakConstants.CLIENT_ID_HEADER;
import static com.nec.middleware.rbacAuth.keycloak.constants.KeycloakConstants.CLIENT_SECRET_HEADER;

/**
 * Reads {@code client-id} and {@code client-secret} HTTP headers and exposes them
 * as request attributes so downstream components (e.g. {@link com.nec.middleware.rbacAuth.keycloak.util.ClientCredentialsUtil})
 * can retrieve them without having direct access to the raw headers.
 */
@Component
public class ClientCredentialsFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String clientId     = request.getHeader(CLIENT_ID_HEADER);
        String clientSecret = request.getHeader(CLIENT_SECRET_HEADER);

        request.setAttribute(CLIENT_ID_HEADER,     StringUtils.hasText(clientId)     ? clientId     : null);
        request.setAttribute(CLIENT_SECRET_HEADER, StringUtils.hasText(clientSecret) ? clientSecret : null);

        filterChain.doFilter(request, response);
    }
}
